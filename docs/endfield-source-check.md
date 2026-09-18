# Verificação de mudanças nas fontes Endfield

`scripts/check_endfield_sources.py` compara as revisões atuais das páginas referenciadas da Talos Wiki com `config/endfield-source-baseline-v1.json`. O relatório relaciona mudanças aos operadores afetados e oferece links para comparar revisões quando possível.

## Executar

Requer Python 3.10 ou superior, sem pacotes adicionais. Na raiz do repositório:

```sh
python -B scripts/check_endfield_sources.py
```

A consulta usa a API pública `https://endfield.wiki.gg/api.php`, em lotes sequenciais de até 50 títulos, com cliente identificado, `maxlag=5`, timeout de 15 segundos por operação de socket e limite de 1 MiB por resposta. Redirecionamentos HTTP são recusados; aliases e redirecionamentos de páginas retornados pela API são resolvidos nos metadados.

Os resultados ficam em `build/source-check/`, já ignorado pelo Git:

- `report.json`: páginas alteradas, ausentes, novas ou retiradas das referências; operadores afetados; erros e fontes sem cobertura.
- `candidate.json`: observação atual, separada da baseline. `complete: false` impede sua adoção se a coleta falhar ou alguma página estiver ausente. Um candidato anterior é invalidado antes de iniciar uma nova verificação.

Parâmetros opcionais: `--catalog`, `--baseline` e `--output-dir`. Os arquivos de entrada não podem coincidir com os de saída. O verificador nunca modifica as builds ou a baseline recebida.

| Saída | Significado |
|---|---|
| `0` | Nenhuma mudança nas páginas Wiki consultadas; a cobertura continua parcial. |
| `1` | Há pendências para revisão, incluindo páginas ausentes ou referências novas/removidas. |
| `2` | Verificação não concluída por falha de entrada, rede, resposta ou gravação. Consulte o erro; não adote o candidato. |

Confira sempre o código de saída e `checkedAt`: uma falha antes de escrever o relatório pode deixar o relatório anterior no diretório. Apenas `candidate.json` de uma coleta completa e revisada pode virar a nova baseline.

## Cobertura inicial e limites

A consulta real de 17/09/2026 encontrou as **41 páginas** usadas por **28 dos 31 operadores**. Isso significa pelo menos uma referência Wiki por operador coberto, não que todas as suas recomendações estejam monitoradas. Endministrator, Perlica e Chen Qianyu não têm referências Wiki no catálogo atual. O relatório lista também as 34 URLs de outros sites que não são consultadas.

A baseline inicial registra a primeira observação, não uma nova revisão editorial de todas as builds. A consulta completa foi repetida com sucesso em 18/09/2026, sem diferenças nas revisões acompanhadas.

Apenas revisões diretas das páginas e mudanças no destino de aliases são detectadas. Alterações em templates transcluídos, imagens ou módulos podem mudar a página exibida sem alterar sua revisão direta e ficam fora desta cobertura. Mudanças cosméticas da Wiki também podem gerar pendências: revisão diferente não significa que a build deva mudar.

Endfield Hub, Game8, Prydwen e o site oficial ainda exigem consulta editorial. O Hub retornou HTTP 403 na tentativa de leitura direta; este verificador não tenta contornar esse bloqueio.

A ferramenta roda uma vez por execução. Não há agendamento, serviço em segundo plano, leitura dos sites pelo app nem criação automática de issues/PRs. Este incremento preserva os workflows existentes.

## Revisar e publicar

1. Abrir as páginas/diffs indicados e conferir o impacto nas builds, mantendo a rota F2P, o snapshot e a equipe vazia.
2. Se houver correção de conteúdo, seguir [a publicação do catálogo](../config/ENDFIELD.md), incluindo aumento de `revision` e data de publicação.
3. Atualizar apenas os registros já revisados na baseline. Só adotar o candidato inteiro quando todas as suas pendências tiverem sido examinadas; uma baseline atualizada silencia essas diferenças nas próximas consultas.
4. Fazer as mudanças por issue e branch da base exata assinada, com diff conferido, testes, CI Android completa e merge com `expected_head_sha`.

Uma indisponibilidade não deve ser tratada como ausência de mudanças nem usada para apagar a referência anterior.

## Testes

```sh
python -B -m unittest discover -s scripts/tests -v
```

A suíte é offline: cobre contrato da baseline publicada, deduplicação, aliases, páginas ausentes, revisões, erros, limites, preservação de entradas e escrita atômica. Os testes Python são executados separadamente da CI Android atual; nenhum workflow foi alterado.

Referências técnicas: [MediaWiki Revisions](https://www.mediawiki.org/wiki/API:Revisions), [normalização e redirecionamentos](https://www.mediawiki.org/wiki/API:Query#Title_normalization_and_redirection) e [boas práticas da API](https://www.mediawiki.org/wiki/API:Etiquette).
