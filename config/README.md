# Catálogo de termos do Game Builds

`config/terms.json` é a única fonte remota oficial de textos editáveis do aplicativo.

## Como editar

Altere somente os valores dentro de `terms`. Mantenha `schemaVersion` em `1` e preserve as chaves existentes para evitar incompatibilidade com versões instaladas do app.

Exemplo:

```json
"character.search.label": "Buscar personagem"
```

pode ser alterado para:

```json
"character.search.label": "Pesquisar personagem"
```

Depois que a alteração for mesclada na `main`, a próxima abertura do app consulta a versão atual de `config/terms.json` e aplica os valores válidos.

## Ordem de carregamento

1. O app abre imediatamente com os termos da última versão válida salva em cache.
2. Se ainda não houver cache, usa `app/src/main/assets/terms.json`, que é o fallback distribuído dentro do APK.
3. Em segundo plano, consulta `config/terms.json` na branch `main`.
4. Se `schemaVersion` for compatível e o JSON for válido, aplica os novos termos e salva essa versão no cache.
5. Se a rede falhar ou o arquivo remoto for inválido, mantém o cache/fallback sem interromper a abertura do app.

## Observação

A atualização remota corrige rótulos e textos já mapeados no catálogo. Mudanças estruturais de tela, novas funcionalidades ou novas chaves ainda exigem uma atualização do aplicativo.
