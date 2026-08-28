# Catálogo de termos do Game Builds

O arquivo `config/terms.json` será a fonte remota de textos editáveis do aplicativo.

## Como editar

Altere somente os valores dentro de `terms`. Mantenha as chaves existentes para evitar incompatibilidade com versões instaladas do app.

Exemplo:

```json
"character.search.label": "Buscar personagem"
```

pode ser alterado para:

```json
"character.search.label": "Pesquisar personagem"
```

## Fallback local

`app/src/main/assets/terms.json` contém a cópia padrão distribuída dentro do APK. Ela será usada quando não houver internet ou quando o arquivo remoto não puder ser carregado.

## Próxima etapa

A integração remota fará o app consultar `config/terms.json` na abertura, validar `schemaVersion`, salvar a última versão válida localmente e usar o fallback embutido quando necessário.
