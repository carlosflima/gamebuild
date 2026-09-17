# Catálogo remoto de Endfield

`config/endfield-builds-v1.json` publica as builds dos 31 operadores já incluídos no APK. Esta primeira versão mantém exatamente o snapshot `Dreamscape of Wind and Snow · 2026-09`, uma build F2P por operador e equipes vazias.

## Publicação

1. Revisar as fontes do operador e confirmar a rota acessível. A classificação F2P no JSON não prova o custo dos equipamentos; essa verificação continua fazendo parte da revisão.
2. Criar issue e branch a partir do SHA exato e verificado de `main`.
3. Editar os campos de conteúdo no JSON, manter todos os operadores e incrementar `revision` (inteiro positivo). Atualizar `publishedAt` com a data de publicação em `AAAA-MM-DD`; essa data não representa nova revisão de todas as fontes.
4. Conferir o diff e executar a CI Android completa. O teste `EndfieldCatalogParserTest` lê o arquivo publicado e valida cobertura, IDs, snapshot, tipo F2P e equipe vazia.
5. Abrir PR e mesclar com `expected_head_sha` somente após CI verde. Conferir main, assinatura e fechamento da issue.

Depois do merge, o app consulta esse arquivo ao iniciar uma nova sessão e pelo botão **Atualizar** nas telas Endfield. Atualizações de conteúdo compatível passam a dispensar um novo APK após a instalação da versão que implementa o catálogo remoto. Mudanças de roster, snapshot, formato ou funcionalidades ainda exigem uma versão compatível do aplicativo.

## Contrato e conteúdo

- `schemaVersion`: `1`.
- `revision`: inteiro positivo e crescente. Para reverter uma recomendação, publicar o conteúdo anterior com uma revisão maior. Não reutilizar uma revisão com conteúdo diferente.
- `publishedAt`: data ISO válida, apresentada no app como **Catálogo de DD/MM/AAAA**.
- `gameVersion`: exatamente `Dreamscape of Wind and Snow · 2026-09`.
- `builds`: conjunto completo dos 31 operadores locais, IDs de personagem/build preservados e sem duplicatas.
- Cada build contém `id`, `characterId`, `title`, `type`, `weapon`, `equipment`, `statPriority`, `team`, `notes` e `sources`.
- `type` é `F2P`, `equipment` tem quatro posições (repetições são permitidas), `team` é vazio e as fontes usam HTTPS.

A revisão 1 foi gerada a partir dos dois catálogos Kotlin existentes, sem alterar recomendações. Os catálogos Kotlin continuam sendo a reserva distribuída no APK. Campos de imagem, novos operadores, NTE e Warframe não são alterados por este arquivo. Novos operadores continuam seguindo o fluxo de inclusão e atualização do APK.

## Falhas, cache e compatibilidade

O app carrega o cache antes de esperar a rede. Se não houver cache válido, usa as builds do APK. O download usa endereço HTTPS fixo neste repositório/main, sem redirecionamento, timeouts de 5 segundos e limite de 256 KiB, conferido também durante a leitura.

O documento inteiro passa pela validação antes de ser salvo. A gravação usa arquivo temporário e substituição atômica; uma falha de rede, formato, versão ou gravação mantém a última versão disponível. Revisões inferiores e alterações que reutilizam a mesma revisão são recusadas. O cache não armazena listas incompletas.

As consultas e a gravação ocorrem fora da thread da interface. A tela aplica o resultado ao personagem ainda selecionado, mantendo busca e filtros; respostas tardias não reabrem telas nem substituem o conteúdo de outro jogo. Pedidos simultâneos são evitados.

Esta etapa não consulta nem monitora os sites de referência automaticamente. A publicação ainda depende da revisão das fontes e do merge. Não há novo workflow, alteração de release ou credenciais.

## Validação em dispositivo

Além da CI: abrir Endfield online e offline; atualizar uma build aberta; trocar de jogo durante o carregamento; testar erro e nova tentativa; fechar/reabrir com cache; conferir fonte ampliada e navegação por gestos com a barra de atualização.
