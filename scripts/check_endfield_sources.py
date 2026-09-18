#!/usr/bin/env python3
"""Compare referenced Talos Wiki revisions; never publish or approve builds."""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
from html import escape
import json
import os
from pathlib import Path
import tempfile
import time
from urllib.parse import quote, unquote, urlencode, urlsplit
from urllib.request import HTTPRedirectHandler, Request, build_opener

ROOT = Path(__file__).resolve().parents[1]
HOST = "endfield.wiki.gg"
API = f"https://{HOST}/api.php"
GAME_VERSION = "Dreamscape of Wind and Snow · 2026-09"
MAX_BYTES = 1_048_576
USER_AGENT = "GameBuildSourceCheck/1.0 (+https://github.com/carlosflima/gamebuild)"


class CheckError(ValueError):
    pass


def require(condition, message):
    if not condition:
        raise CheckError(message)


def utc_now():
    return datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")


def timestamp(value):
    require(isinstance(value, str), "Timestamp ausente")
    require(datetime.strptime(value, "%Y-%m-%dT%H:%M:%SZ").strftime("%Y-%m-%dT%H:%M:%SZ") == value,
            "Timestamp inválido")
    return value


def text(value):
    require(isinstance(value, str) and 0 < len(value) <= 2048, "Texto inválido")
    require(not any(ord(c) < 32 or ord(c) == 127 for c in value), "Controle em texto")
    return value


def positive_int(value):
    require(type(value) is int and value > 0, "Inteiro positivo obrigatório")
    return value


def unique_object(pairs):
    result = {}
    for key, value in pairs:
        require(key not in result, f"Chave JSON duplicada: {key}")
        result[key] = value
    return result


def decode_json(raw):
    require(0 < len(raw) <= MAX_BYTES, "Documento vazio ou maior que 1 MiB")
    return json.loads(raw.decode("utf-8"), object_pairs_hook=unique_object)


def read_json(path):
    with path.open("rb") as source:
        return decode_json(source.read(MAX_BYTES + 1))


def collect_sources(catalog):
    require(isinstance(catalog, dict), "Catálogo deve ser um objeto JSON")
    require(type(catalog.get("schemaVersion")) is int and catalog["schemaVersion"] == 1,
            "Schema de catálogo incompatível")
    require(catalog.get("gameVersion") == GAME_VERSION, "Snapshot incompatível")
    positive_int(catalog.get("revision"))
    builds = catalog.get("builds")
    require(isinstance(builds, list) and 0 < len(builds) <= 100, "Lista de builds inválida")
    supported, unsupported, operators = {}, {}, set()
    for build in builds:
        character = text(build["characterId"])
        require(character.startswith("endfield-") and character not in operators,
                "Operador inválido ou duplicado")
        operators.add(character)
        require(build["type"] == "F2P" and build["team"] == [], "Contrato F2P/equipe inválido")
        require(isinstance(build["sources"], list) and build["sources"], "Fontes ausentes")
        for source in build["sources"]:
            url = text(source["url"])
            parsed = urlsplit(url)
            require(parsed.scheme == "https" and parsed.hostname and not parsed.username
                    and not parsed.password and parsed.port is None, "URL de fonte inválida")
            if parsed.hostname == HOST and parsed.path.startswith("/wiki/") and not parsed.query:
                title = unquote(parsed.path[len("/wiki/"):], errors="strict").replace("_", " ")
                text(title)
                require(0 < len(title) <= 255 and not any(c in title for c in "|#\r\n\t"),
                        "Título Wiki inválido")
                supported.setdefault(title, set()).add(character)
            else:
                unsupported.setdefault(url, set()).add(character)
    require(0 < len(supported) <= 200, "Quantidade de páginas Wiki inválida")
    return supported, unsupported, operators


def validate_record(record):
    require(isinstance(record, dict) and set(record) == {
        "resolvedTitle", "pageId", "revisionId", "timestamp"
    }, "Registro de revisão inválido")
    text(record["resolvedTitle"])
    positive_int(record["pageId"])
    positive_int(record["revisionId"])
    timestamp(record["timestamp"])


def validate_baseline(baseline):
    require(isinstance(baseline, dict) and set(baseline) == {
        "schemaVersion", "provider", "gameVersion", "observedAt", "complete", "pages"
    }, "Baseline inválida")
    require(type(baseline["schemaVersion"]) is int and baseline["schemaVersion"] == 1
            and baseline["provider"] == HOST and baseline["gameVersion"] == GAME_VERSION,
            "Baseline incompatível")
    require(baseline["complete"] is True, "Baseline incompleta não pode ser adotada")
    timestamp(baseline["observedAt"])
    pages = baseline["pages"]
    require(isinstance(pages, dict) and 0 < len(pages) <= 200, "Páginas da baseline inválidas")
    for title, record in pages.items():
        text(title)
        validate_record(record)


def parse_revisions(payload, requested):
    require(isinstance(payload, dict) and payload.get("batchcomplete") is True,
            "Resposta incompleta da API")
    require(not any(k in payload for k in ("error", "warnings", "continue")),
            "Erro, aviso ou paginação inesperada da API")
    query = payload.get("query")
    require(isinstance(query, dict) and "interwiki" not in query, "Consulta inválida")
    aliases = {}
    for key in ("normalized", "redirects"):
        for alias in query.get(key, []):
            origin, target = text(alias["from"]), text(alias["to"])
            require(origin not in aliases, "Aliases duplicados")
            if origin != target:
                aliases[origin] = target

    def resolve(title):
        visited = set()
        while title in aliases:
            require(title not in visited, "Ciclo de redirecionamento")
            visited.add(title)
            title = aliases[title]
        return title

    pages = {}
    require(isinstance(query.get("pages"), list), "Páginas ausentes")
    for page in query["pages"]:
        title = text(page["title"])
        require(title not in pages, "Página duplicada na resposta")
        if page.get("missing") is True:
            pages[title] = None
            continue
        require("invalid" not in page and page.get("ns") == 0, "Página inválida ou fora do escopo")
        revisions = page.get("revisions")
        require(isinstance(revisions, list) and len(revisions) == 1, "Revisão ausente")
        revision = revisions[0]
        record = {
            "resolvedTitle": title,
            "pageId": positive_int(page["pageid"]),
            "revisionId": positive_int(revision["revid"]),
            "timestamp": timestamp(revision["timestamp"]),
        }
        validate_record(record)
        pages[title] = record
    require(set(pages) == {resolve(title) for title in requested},
            "Resposta omitiu ou acrescentou páginas")
    return {title: pages[resolve(title)] for title in requested}


class NoRedirects(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        raise CheckError("Redirecionamento HTTP recusado")


def request_batch(titles):
    query = urlencode({
        "action": "query", "prop": "revisions", "rvprop": "ids|timestamp",
        "titles": "|".join(titles), "redirects": "1", "format": "json",
        "formatversion": "2", "maxlag": "5",
    })
    request = Request(f"{API}?{query}", headers={
        "User-Agent": USER_AGENT, "Accept": "application/json", "Accept-Encoding": "identity",
    })
    with build_opener(NoRedirects()).open(request, timeout=15) as response:
        require(response.status == 200, "Status HTTP inesperado")
        require(response.headers.get_content_type() == "application/json", "Resposta não é JSON")
        return decode_json(response.read(MAX_BYTES + 1))


def fetch_revisions(titles, request=None, pause=time.sleep):
    request = request or request_batch
    ordered = sorted(titles)
    records = {}
    for start in range(0, len(ordered), 50):
        if start:
            pause(1)
        batch = ordered[start:start + 50]
        records.update(parse_revisions(request(batch), batch))
    return records


def source_url(title):
    return f"https://{HOST}/wiki/{quote(title.replace(' ', '_'), safe='')}"


def inspect_sources(catalog, baseline, fetch=None, checked_at=None):
    fetch = fetch or fetch_revisions
    validate_baseline(baseline)
    supported, unsupported, operators = collect_sources(catalog)
    observed = checked_at or utc_now()
    records, errors = {}, []
    try:
        records = fetch(supported)
        require(set(records) == set(supported), "Coleta incompleta")
        for record in records.values():
            if record is not None:
                validate_record(record)
    except Exception as error:
        records = {}
        errors.append(f"{type(error).__name__}: {error}")

    changes = []
    unchanged = 0
    if not errors:
        for title, after in sorted(records.items()):
            before = baseline["pages"].get(title)
            if after is not None and after == before:
                unchanged += 1
                continue
            kind = "missing" if after is None else "untracked" if before is None else "changed"
            change = {
                "kind": kind, "title": title, "url": source_url(title),
                "characters": sorted(supported[title]), "before": before, "after": after,
            }
            if (before and after and before["pageId"] == after["pageId"]
                    and before["revisionId"] != after["revisionId"]):
                change["diffUrl"] = f"https://{HOST}/index.php?" + urlencode({
                    "title": after["resolvedTitle"], "oldid": before["revisionId"],
                    "diff": after["revisionId"],
                })
            changes.append(change)
        for title in sorted(set(baseline["pages"]) - set(supported)):
            changes.append({"kind": "removed_reference", "title": title, "url": source_url(title),
                            "characters": [], "before": baseline["pages"][title], "after": None})

    complete = not errors and all(record is not None for record in records.values())
    candidate = {
        "schemaVersion": 1, "provider": HOST, "gameVersion": GAME_VERSION,
        "observedAt": observed, "complete": complete,
        "pages": {title: record for title, record in sorted(records.items()) if record is not None},
    }
    report = {
        "checkedAt": observed, "catalogRevision": catalog["revision"],
        "scope": "Talos Wiki: revisões diretas das páginas referenciadas; cobertura parcial",
        "counts": {"trackedPages": len(supported), "unchangedPages": unchanged,
                   "coveredOperators": len(set().union(*supported.values())),
                   "totalOperators": len(operators), "unsupportedSources": len(unsupported)},
        "changes": changes, "errors": errors, "candidateComplete": complete,
        "uncoveredOperators": sorted(operators - set().union(*supported.values())),
        "unsupportedSources": [{"url": url, "characters": sorted(characters)}
                               for url, characters in sorted(unsupported.items())],
    }
    return report, candidate


def markdown_text(value):
    value = escape(str(value), quote=False)
    return "".join(" " if char.isspace() else "\\" + char if char in "\\`*_[]|" else char
                   for char in value)


def markdown_link(label, url):
    # Keep URL delimiters but encode characters that could terminate a Markdown link.
    destination = quote(url, safe=":/?#@!$&'*,;=+%")
    return f"[{markdown_text(label)}]({destination})"


def render_markdown(report):
    counts = report["counts"]
    if report["errors"]:
        status = "Verificação não concluída"
    elif not report["candidateComplete"]:
        status = "Revisão necessária: coleta incompleta"
    elif report["changes"]:
        status = "Revisão necessária"
    else:
        status = "Nenhuma mudança nas páginas consultadas"
    lines = [
        "# Revisão de fontes Endfield", "", f"**{status}. Cobertura parcial.**", "",
        f"Consulta (UTC): {markdown_text(report['checkedAt'])}  ",
        f"Revisão do catálogo: {report['catalogRevision']}", "", "## Cobertura", "",
        f"- Páginas Wiki acompanhadas: {counts['trackedPages']}.",
        f"- Páginas confirmadas sem alteração: {counts['unchangedPages']}.",
        f"- Operadores com ao menos uma referência Wiki: "
        f"{counts['coveredOperators']}/{counts['totalOperators']}.",
        f"- URLs de outros sites sem consulta automática: {counts['unsupportedSources']}.", "",
        "A cobertura considera revisões diretas das páginas. Templates, módulos e imagens "
        "podem mudar sem alterar essa revisão. Uma referência Wiki não cobre toda a build.", "",
        "Operadores sem referência Wiki: " + (
            ", ".join(map(markdown_text, report["uncoveredOperators"]))
            if report["uncoveredOperators"] else "nenhum"
        ) + ".", "",
    ]
    if report["errors"]:
        lines.extend(["## Erros", ""])
        lines.extend(f"- {markdown_text(error)}" for error in report["errors"])
        lines.append("")
    lines.extend(["## Pendências para revisão", ""])
    kinds = {"changed": "Página alterada", "missing": "Página ausente",
             "untracked": "Nova referência", "removed_reference": "Referência retirada"}
    for change in report["changes"]:
        lines.extend([
            f"### {kinds[change['kind']]}: {markdown_text(change['title'])}", "",
            markdown_link("Abrir página", change["url"]), "",
            "Operadores afetados: " + (
                ", ".join(map(markdown_text, change["characters"]))
                if change["characters"] else "nenhum no catálogo atual"
            ) + ".", "",
        ])
        for key, label in (("before", "Baseline"), ("after", "Consulta atual")):
            revision = change[key]
            details = (f"{markdown_text(revision['resolvedTitle'])}; página {revision['pageId']}; "
                       f"revisão {revision['revisionId']}; {markdown_text(revision['timestamp'])}"
                       if revision else "sem registro")
            lines.append(f"- {label}: {details}.")
        if "diffUrl" in change:
            lines.extend(["", markdown_link("Comparar revisões", change["diffUrl"])])
        lines.append("")
    if not report["changes"]:
        lines.extend(["Pendências não apuradas devido à falha na consulta." if report["errors"]
                      else "Nenhuma pendência nas páginas consultadas.", ""])
    lines.extend(["## Fontes sem consulta automática", ""])
    for source in report["unsupportedSources"]:
        lines.append(f"- {markdown_link(source['url'], source['url'])} — "
                     + ", ".join(map(markdown_text, source["characters"])))
    if not report["unsupportedSources"]:
        lines.append("Nenhuma URL de outro site no catálogo atual.")
    lines.extend(["", "## Próximos passos", "",
                  "Candidato completo; exige revisão antes de adoção."
                  if report["candidateComplete"] else "Candidato incompleto; não adotar.", "",
                  "Confira páginas e diferenças antes de alterar recomendações: uma edição "
                  "cosmética também gera alerta. Preserve a rota F2P, o snapshot e a equipe vazia.", "",
                  "Builds e baseline permanecem preservadas. Publicações exigem revisão, PR, "
                  "testes, CI completa e merge protegido. Falhas não significam ausência de mudanças.", ""])
    return "\n".join(lines)


def write_json(path, document):
    write_text(path, json.dumps(document, ensure_ascii=False, indent=2, sort_keys=True) + "\n")


def write_text(path, content):
    path.parent.mkdir(parents=True, exist_ok=True)
    descriptor, temporary = tempfile.mkstemp(prefix=path.name + ".", dir=path.parent)
    try:
        with os.fdopen(descriptor, "w", encoding="utf-8", newline="\n") as output:
            output.write(content)
            output.flush()
            os.fsync(output.fileno())
        os.replace(temporary, path)
    finally:
        if os.path.exists(temporary):
            os.unlink(temporary)


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--catalog", type=Path, default=ROOT / "config/endfield-builds-v1.json")
    parser.add_argument("--baseline", type=Path, default=ROOT / "config/endfield-source-baseline-v1.json")
    parser.add_argument("--output-dir", type=Path, default=ROOT / "build/source-check")
    args = parser.parse_args(argv)
    report_path = args.output_dir / "report.json"
    markdown_path = args.output_dir / "report.md"
    candidate_path = args.output_dir / "candidate.json"
    try:
        inputs = {args.catalog.resolve(), args.baseline.resolve()}
        outputs = {report_path.resolve(), markdown_path.resolve(), candidate_path.resolve()}
        require(len(inputs) == 2 and len(outputs) == 3 and not inputs & outputs,
                "Os arquivos de entrada não podem ser sobrescritos")
        # Invalidate older candidates before reading inputs or accessing the network.
        write_json(candidate_path, {
            "schemaVersion": 1, "provider": HOST, "gameVersion": GAME_VERSION,
            "observedAt": utc_now(), "complete": False, "pages": {},
        })
        catalog, baseline = read_json(args.catalog), read_json(args.baseline)
        report, candidate = inspect_sources(catalog, baseline)
        write_json(report_path, report)
        write_text(markdown_path, render_markdown(report))
        # Only a complete collection produces an adoptable candidate.
        write_json(candidate_path, candidate)
        counts = report["counts"]
        print(f"Wiki: {counts['trackedPages']} páginas, "
              f"{counts['coveredOperators']}/{counts['totalOperators']} operadores; "
              f"{len(report['changes'])} pendências, {len(report['errors'])} erros. Cobertura parcial.")
        print(f"Relatório: {report_path}")
        print(f"Relatório para revisão: {markdown_path}")
        print("Builds e baseline preservadas. Candidato exige revisão e PR.")
        return 2 if report["errors"] else 1 if report["changes"] else 0
    except (OSError, ValueError, KeyError, TypeError) as error:
        print(f"Verificação não concluída: {type(error).__name__}: {error}")
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
