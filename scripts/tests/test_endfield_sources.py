import contextlib
import copy
from email.message import Message
import io
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import Mock, patch
from urllib.parse import parse_qs, urlsplit

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import check_endfield_sources as checker

NOW = "2026-09-17T17:00:00Z"
ROOT = Path(__file__).resolve().parents[2]


def record(title="Alpha", revision=10, page_id=1):
    return {"resolvedTitle": title, "pageId": page_id, "revisionId": revision, "timestamp": NOW}


def baseline(pages=None):
    return {"schemaVersion": 1, "provider": checker.HOST, "gameVersion": checker.GAME_VERSION,
            "observedAt": NOW, "complete": True,
            "pages": {"Alpha": record()} if pages is None else pages}


def catalog():
    return {"schemaVersion": 1, "revision": 1, "gameVersion": checker.GAME_VERSION, "builds": [
        {"characterId": "endfield-alpha", "type": "F2P", "team": [],
         "sources": [{"url": "https://endfield.wiki.gg/wiki/Alpha"},
                     {"url": "https://endfieldhub.org/guides/progression/alpha"}]},
        {"characterId": "endfield-beta", "type": "F2P", "team": [],
         "sources": [{"url": "https://endfield.wiki.gg/wiki/Alpha"}]},
    ]}


def api_page(title="Alpha", revision=10, page_id=1):
    return {"title": title, "ns": 0, "pageid": page_id,
            "revisions": [{"revid": revision, "timestamp": NOW}]}


def payload(*pages, **extras):
    return {"batchcomplete": True, "query": {"pages": list(pages), **extras}}


class SourceCheckTest(unittest.TestCase):
    def inspect(self, records, previous=None):
        return checker.inspect_sources(catalog(), previous or baseline(),
                                       fetch=lambda titles: records, checked_at=NOW)

    def test_published_baseline_matches_catalog_sources(self):
        current = checker.read_json(ROOT / "config/endfield-builds-v1.json")
        saved = checker.read_json(ROOT / "config/endfield-source-baseline-v1.json")
        checker.validate_baseline(saved)
        supported, _, operators = checker.collect_sources(current)
        self.assertEqual(set(supported), set(saved["pages"]))
        self.assertEqual(31, len(operators))
        self.assertEqual(41, len(supported))

    def test_shared_sources_are_deduplicated_and_coverage_is_explicit(self):
        supported, unsupported, operators = checker.collect_sources(catalog())
        self.assertEqual({"Alpha": {"endfield-alpha", "endfield-beta"}}, supported)
        self.assertEqual(1, len(unsupported))
        self.assertEqual(2, len(operators))

    def test_decodes_wiki_titles_and_normalizes_spaces(self):
        data = catalog()
        data["builds"][0]["sources"][0]["url"] = "https://endfield.wiki.gg/wiki/Pathfinder%27s_Beacon"
        self.assertIn("Pathfinder's Beacon", checker.collect_sources(data)[0])

    def test_rejects_other_snapshots_and_invalid_catalogs(self):
        for change in (
            lambda c: c.update(gameVersion="future"),
            lambda c: c.update(schemaVersion=True),
            lambda c: c.update(builds=[]),
            lambda c: c["builds"].append(c["builds"][0]),
            lambda c: c["builds"][0].update(type="META"),
            lambda c: c["builds"][0].update(team=["operator"]),
        ):
            data = catalog()
            change(data)
            with self.subTest(data=data), self.assertRaises(ValueError):
                checker.collect_sources(data)
        with self.assertRaises(ValueError):
            checker.collect_sources([])

    def test_rejects_invalid_urls_and_query_injection_in_titles(self):
        for url in ("http://endfield.wiki.gg/wiki/Alpha",
                    "https://user@endfield.wiki.gg/wiki/Alpha",
                    "https://endfield.wiki.gg/wiki/Alpha%7CBeta",
                    "https://endfield.wiki.gg/wiki/Alpha%00",
                    "https://endfield.wiki.gg/wiki/"):
            data = catalog()
            data["builds"][0]["sources"][0]["url"] = url
            with self.subTest(url=url), self.assertRaises(ValueError):
                checker.collect_sources(data)

    def test_follows_normalization_and_wiki_redirects_without_http_navigation(self):
        response = payload(api_page("Target"),
                           normalized=[{"from": "alpha", "to": "Alpha"}],
                           redirects=[{"from": "Alpha", "to": "Target"}])
        self.assertEqual({"alpha": record("Target")},
                         checker.parse_revisions(response, ["alpha"]))

    def test_reports_missing_pages(self):
        parsed = checker.parse_revisions(payload({"title": "Alpha", "missing": True}), ["Alpha"])
        report, candidate = self.inspect(parsed)
        self.assertEqual("missing", report["changes"][0]["kind"])
        self.assertFalse(candidate["complete"])
        with self.assertRaises(ValueError):
            checker.validate_baseline(candidate)

    def test_rejects_incomplete_and_failed_api_responses(self):
        cases = [
            {}, {"batchcomplete": True, "error": {"code": "maxlag"}},
            {**payload(api_page()), "continue": {}},
            {**payload(api_page()), "warnings": {"query": "partial"}},
            payload(), payload(api_page("Other")),
            payload(api_page(), api_page()),
            payload({"title": "Alpha", "ns": 0, "pageid": 1}),
            payload(api_page(), interwiki=[{"title": "Alpha"}]),
        ]
        for response in cases:
            with self.subTest(response=response), self.assertRaises((ValueError, KeyError)):
                checker.parse_revisions(response, ["Alpha"])

    def test_rejects_cyclic_redirects_and_invalid_revision_metadata(self):
        response = payload(api_page(), redirects=[
            {"from": "Alpha", "to": "Beta"}, {"from": "Beta", "to": "Alpha"}])
        with self.assertRaises(ValueError):
            checker.parse_revisions(response, ["Alpha"])
        for field, value in (("revid", 0), ("revid", True), ("timestamp", "yesterday")):
            page = api_page()
            page["revisions"][0][field] = value
            with self.subTest(field=field), self.assertRaises(ValueError):
                checker.parse_revisions(payload(page), ["Alpha"])

    def test_groups_requests_in_serial_batches_of_fifty(self):
        calls, pauses = [], []
        def request(titles):
            calls.append(titles)
            return payload(*(api_page(title, page_id=index + 1)
                             for index, title in enumerate(titles)))
        titles = {f"Page {n}" for n in range(101)}
        result = checker.fetch_revisions(titles, request, pauses.append)
        self.assertEqual([50, 50, 1], list(map(len, calls)))
        self.assertEqual([1, 1], pauses)
        self.assertEqual(titles, set(result))

    def test_unchanged_pages_do_not_create_changes(self):
        report, candidate = self.inspect({"Alpha": record()})
        self.assertEqual([], report["changes"])
        self.assertEqual(1, report["counts"]["unchangedPages"])
        self.assertEqual(1, report["counts"]["unsupportedSources"])
        self.assertTrue(candidate["complete"])
        checker.validate_baseline(candidate)

    def test_change_lists_all_affected_operators_and_exact_diff(self):
        report, _ = self.inspect({"Alpha": record(revision=11)})
        change = report["changes"][0]
        self.assertEqual("changed", change["kind"])
        self.assertEqual(["endfield-alpha", "endfield-beta"], change["characters"])
        query = parse_qs(urlsplit(change["diffUrl"]).query)
        self.assertEqual(["10"], query["oldid"])
        self.assertEqual(["11"], query["diff"])

    def test_renamed_or_recreated_pages_are_not_reported_as_unchanged(self):
        for value in (record("Target"), record(page_id=2)):
            report, _ = self.inspect({"Alpha": value})
            self.assertEqual("changed", report["changes"][0]["kind"])
            self.assertNotIn("diffUrl", report["changes"][0])

    def test_markdown_change_shows_operators_metadata_and_revision_link(self):
        report, _ = self.inspect({"Alpha": record(revision=11)})
        rendered = checker.render_markdown(report)
        self.assertIn("**Revisão necessária. Cobertura parcial.**", rendered)
        self.assertIn("Consulta (UTC): " + NOW, rendered)
        self.assertIn("Revisão do catálogo: 1", rendered)
        self.assertIn("### Página alterada: Alpha", rendered)
        self.assertIn("Operadores afetados: endfield-alpha, endfield-beta.", rendered)
        self.assertIn("Baseline: Alpha; página 1; revisão 10; " + NOW, rendered)
        self.assertIn("Consulta atual: Alpha; página 1; revisão 11; " + NOW, rendered)
        self.assertIn("[Comparar revisões](" + report["changes"][0]["diffUrl"] + ")", rendered)
        self.assertIn("Candidato completo; exige revisão", rendered)

    def test_markdown_distinguishes_missing_new_and_removed_references(self):
        for records, previous, label in (
            ({"Alpha": None}, baseline(), "Página ausente"),
            ({"Alpha": record()}, baseline({"Old": record("Old")}), "Nova referência"),
        ):
            with self.subTest(label=label):
                report, _ = self.inspect(records, previous)
                rendered = checker.render_markdown(report)
                self.assertIn("### " + label + ": Alpha", rendered)
                self.assertNotIn("Comparar revisões", rendered)
                self.assertIn("sem registro", rendered)
                if records["Alpha"] is None:
                    self.assertIn("coleta incompleta", rendered)
                    self.assertIn("Candidato incompleto; não adotar.", rendered)
                else:
                    self.assertIn("### Referência retirada: Old", rendered)
                    self.assertIn("nenhum no catálogo atual", rendered)

    def test_markdown_alias_change_shows_both_titles_without_inventing_diff(self):
        report, _ = self.inspect({"Alpha": record("Target")})
        rendered = checker.render_markdown(report)
        self.assertIn("Baseline: Alpha;", rendered)
        self.assertIn("Consulta atual: Target;", rendered)
        self.assertNotIn("Comparar revisões", rendered)

    def test_report_identifies_uncovered_operators_and_unsupported_sources(self):
        data = catalog()
        data["builds"].append({"characterId": "endfield-gamma", "type": "F2P", "team": [],
                               "sources": [{"url": "https://example.org/guide"}]})
        report, _ = checker.inspect_sources(data, baseline(),
                                           fetch=lambda _: {"Alpha": record()}, checked_at=NOW)
        self.assertEqual(["endfield-gamma"], report["uncoveredOperators"])
        self.assertEqual(2, report["counts"]["coveredOperators"])
        self.assertEqual(3, report["counts"]["totalOperators"])
        rendered = checker.render_markdown(report)
        self.assertIn("Operadores sem referência Wiki: endfield-gamma.", rendered)
        self.assertIn("[https://example.org/guide](https://example.org/guide) — endfield-gamma", rendered)
        self.assertIn("ao menos uma referência Wiki: 2/3", rendered)

    def test_unchanged_markdown_keeps_partial_coverage_and_review_requirement(self):
        report, _ = self.inspect({"Alpha": record()})
        rendered = checker.render_markdown(report)
        self.assertIn("Nenhuma mudança nas páginas consultadas. Cobertura parcial.", rendered)
        self.assertIn("Páginas confirmadas sem alteração: 1", rendered)
        self.assertIn("Operadores sem referência Wiki: nenhum.", rendered)
        self.assertIn("https://endfieldhub.org/guides/progression/alpha", rendered)
        self.assertIn("Templates, módulos e imagens", rendered)
        self.assertIn("exige revisão antes de adoção", rendered)

    def test_failed_markdown_does_not_claim_sources_are_unchanged(self):
        report, _ = checker.inspect_sources(catalog(), baseline(),
                                           fetch=Mock(side_effect=TimeoutError("offline")), checked_at=NOW)
        rendered = checker.render_markdown(report)
        self.assertIn("Verificação não concluída", rendered)
        self.assertIn("Pendências não apuradas", rendered)
        self.assertIn("TimeoutError: offline", rendered)
        self.assertIn("Candidato incompleto; não adotar.", rendered)
        self.assertNotIn("Nenhuma mudança", rendered)
        self.assertNotIn("Nenhuma pendência", rendered)

    def test_markdown_keeps_external_text_and_special_urls_as_data(self):
        data = catalog()
        url = 'https://example.org/a_(b)?q=<tag>"'
        data["builds"][0]["sources"].append({"url": url})
        report, _ = checker.inspect_sources(data, baseline(), fetch=Mock(
            side_effect=ValueError("<script>x</script>\n![img](bad) `code` | *bold* &")), checked_at=NOW)
        rendered = checker.render_markdown(report)
        self.assertNotIn("<script>", rendered)
        self.assertNotIn("\n![img]", rendered)
        self.assertIn(r"!\[img\](bad) \`code\` \| \*bold\* &amp;", rendered)
        self.assertIn("(https://example.org/a_%28b%29?q=%3Ctag%3E%22)", rendered)

    def test_markdown_handles_catalog_with_only_supported_sources(self):
        data = catalog()
        data["builds"][0]["sources"].pop()
        report, _ = checker.inspect_sources(data, baseline(),
                                           fetch=lambda _: {"Alpha": record()}, checked_at=NOW)
        self.assertIn("Nenhuma URL de outro site no catálogo atual.", checker.render_markdown(report))

    def test_new_and_removed_references_require_review(self):
        report, _ = self.inspect({"Alpha": record()}, baseline({"Old": record("Old")}))
        self.assertEqual({"untracked", "removed_reference"},
                         {change["kind"] for change in report["changes"]})

    def test_failed_collection_cannot_be_mistaken_for_unchanged(self):
        report, candidate = checker.inspect_sources(
            catalog(), baseline(), fetch=Mock(side_effect=TimeoutError("offline")), checked_at=NOW)
        self.assertEqual(1, len(report["errors"]))
        self.assertFalse(report["candidateComplete"])
        self.assertEqual(0, report["counts"]["unchangedPages"])
        self.assertEqual({}, candidate["pages"])
        with self.assertRaises(ValueError):
            checker.validate_baseline(candidate)

    def test_partial_collection_is_discarded(self):
        report, candidate = self.inspect({})
        self.assertTrue(report["errors"])
        self.assertFalse(candidate["complete"])

    def test_invalid_baseline_is_rejected_before_network(self):
        fetch = Mock()
        invalid = baseline()
        invalid["complete"] = False
        with self.assertRaises(ValueError):
            checker.inspect_sources(catalog(), invalid, fetch=fetch)
        fetch.assert_not_called()

    def test_inspection_never_mutates_catalog_or_baseline(self):
        data, saved = catalog(), baseline()
        original = copy.deepcopy((data, saved))
        checker.inspect_sources(data, saved, fetch=lambda _: {"Alpha": record(revision=11)})
        self.assertEqual(original, (data, saved))

    def test_rejects_duplicate_json_keys_and_oversized_documents(self):
        for raw in (b'{"revision":1,"revision":2}', b"x" * (checker.MAX_BYTES + 1), b"\xff"):
            with self.assertRaises(ValueError):
                checker.decode_json(raw)

    def test_download_is_fixed_host_bounded_and_identified(self):
        response = Mock()
        response.status = 200
        response.headers = Message()
        response.headers["Content-Type"] = "application/json; charset=utf-8"
        response.read.return_value = json.dumps(payload(api_page())).encode()
        opener = Mock()
        opener.open.return_value.__enter__ = Mock(return_value=response)
        opener.open.return_value.__exit__ = Mock(return_value=False)
        with patch.object(checker, "build_opener", return_value=opener):
            checker.request_batch(["Alpha"])
        request = opener.open.call_args.args[0]
        self.assertEqual(checker.HOST, urlsplit(request.full_url).hostname)
        self.assertEqual(checker.USER_AGENT, request.get_header("User-agent"))
        self.assertEqual(15, opener.open.call_args.kwargs["timeout"])
        response.read.assert_called_once_with(checker.MAX_BYTES + 1)

    def test_http_redirects_are_refused(self):
        with self.assertRaises(ValueError):
            checker.NoRedirects().redirect_request(None, None, 302, "", {}, "https://other.test")

    def run_command(self, root, fetch):
        with patch.object(checker, "fetch_revisions", fetch), contextlib.redirect_stdout(io.StringIO()):
            return checker.main(["--catalog", str(root / "catalog.json"),
                                 "--baseline", str(root / "baseline.json"),
                                 "--output-dir", str(root / "out")])

    def prepare_inputs(self, root):
        checker.write_json(root / "catalog.json", catalog())
        checker.write_json(root / "baseline.json", baseline())

    def test_command_writes_reports_but_preserves_inputs(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            self.prepare_inputs(root)
            original = [(root / file).read_bytes() for file in ("catalog.json", "baseline.json")]
            result = self.run_command(root, Mock(return_value={"Alpha": record(revision=11)}))
            self.assertEqual(1, result)
            self.assertEqual(original, [(root / file).read_bytes()
                                        for file in ("catalog.json", "baseline.json")])
            self.assertTrue(checker.read_json(root / "out/candidate.json")["complete"])
            self.assertEqual("changed", checker.read_json(root / "out/report.json")["changes"][0]["kind"])
            rendered = (root / "out/report.md").read_text(encoding="utf-8")
            self.assertIn("### Página alterada: Alpha", rendered)
            self.assertIn("endfield-beta", rendered)

    def test_command_invalidates_stale_candidate_on_network_failure(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            self.prepare_inputs(root)
            checker.write_json(root / "out/candidate.json", baseline())
            self.assertEqual(2, self.run_command(root, Mock(side_effect=TimeoutError("offline"))))
            self.assertFalse(checker.read_json(root / "out/candidate.json")["complete"])
            self.assertTrue(checker.read_json(root / "out/report.json")["errors"])
            self.assertIn("Verificação não concluída",
                          (root / "out/report.md").read_text(encoding="utf-8"))

    def test_markdown_output_cannot_overwrite_an_input(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            saved = root / "report.md"
            checker.write_json(saved, baseline())
            checker.write_json(root / "catalog.json", catalog())
            original = saved.read_bytes()
            with patch.object(checker, "fetch_revisions") as fetch, contextlib.redirect_stdout(io.StringIO()):
                result = checker.main(["--catalog", str(root / "catalog.json"),
                                       "--baseline", str(saved), "--output-dir", str(root)])
            self.assertEqual(2, result)
            fetch.assert_not_called()
            self.assertEqual(original, saved.read_bytes())

    def test_failed_markdown_write_leaves_candidate_incomplete(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            self.prepare_inputs(root)
            write_text = checker.write_text
            def fail_markdown(path, content):
                if path.suffix == ".md":
                    raise OSError("read-only report")
                write_text(path, content)
            with patch.object(checker, "write_text", side_effect=fail_markdown):
                result = self.run_command(root, Mock(return_value={"Alpha": record()}))
            self.assertEqual(2, result)
            self.assertFalse(checker.read_json(root / "out/candidate.json")["complete"])

    def test_invalid_input_does_not_leave_an_adoptable_candidate(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            self.prepare_inputs(root)
            (root / "baseline.json").write_text("{", encoding="utf-8")
            checker.write_json(root / "out/candidate.json", baseline())
            fetch = Mock()
            self.assertEqual(2, self.run_command(root, fetch))
            fetch.assert_not_called()
            self.assertFalse(checker.read_json(root / "out/candidate.json")["complete"])

    def test_command_refuses_output_overlapping_input(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            saved = root / "candidate.json"
            checker.write_json(saved, baseline())
            checker.write_json(root / "catalog.json", catalog())
            original = saved.read_bytes()
            with patch.object(checker, "fetch_revisions") as fetch, contextlib.redirect_stdout(io.StringIO()):
                result = checker.main(["--catalog", str(root / "catalog.json"),
                                       "--baseline", str(saved), "--output-dir", str(root)])
            self.assertEqual(2, result)
            fetch.assert_not_called()
            self.assertEqual(original, saved.read_bytes())

    def test_atomic_write_keeps_existing_file_on_replace_failure(self):
        with tempfile.TemporaryDirectory() as folder:
            path = Path(folder) / "report.json"
            checker.write_json(path, {"original": True})
            with patch.object(checker.os, "replace", side_effect=OSError("write failed")):
                with self.assertRaises(OSError):
                    checker.write_json(path, {"new": True})
            self.assertEqual({"original": True}, checker.read_json(path))
            self.assertEqual([path], list(Path(folder).iterdir()))


if __name__ == "__main__":
    unittest.main()
