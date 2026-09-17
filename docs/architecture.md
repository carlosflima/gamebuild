# Game Builds — Arquitetura

## Objetivo

Aplicativo Android para consultar builds versionadas de personagens de múltiplos jogos. Os catálogos são distribuídos no APK; Endfield também recebe conteúdo validado de um catálogo remoto. Cada build registra suas referências externas.

## Camadas

- **Domínio:** `Game`, `GameCharacter`, `CharacterBuild`, `BuildSource` e `AppTerms` representam jogos, personagens, builds, referências e textos da interface.
- **Dados:** `LocalGameRepository` encaminha consultas às implementações de `LocalGameDataSource` de NTE, Warframe e Endfield. Cada jogo mantém seus catálogos de personagens e builds; Endfield também usa `EndfieldAdditionalBuildCatalog`.
- **Estado:** `GameBuildViewModel` expõe `GameBuildUiState` por `StateFlow`. Seleção de jogo e personagem, busca, filtros, comparação e mensagens de erro derivam desse estado.
- **Interface:** `GameBuildApp` usa Jetpack Compose e Material 3. Observa o estado com respeito ao ciclo de vida e encaminha as ações ao ViewModel.

O repositório verifica registros duplicados de fontes locais por jogo. Os catálogos indexam as builds pelo identificador do personagem.

## Navegação e apresentação

O fluxo é seleção de jogo → lista de personagens → builds do personagem. Botão e gesto de voltar usam as mesmas transições do ViewModel. Retornar à lista preserva busca e filtro; retornar aos jogos limpa o estado.

A tela permite filtrar builds por tipo e comparar alternativas quando há mais de um tipo disponível. O compartilhamento é formatado por `BuildShareFormatter` e entregue ao seletor do Android. Fontes abrem em aplicativos externos; falhas de abertura recebem feedback na interface.

`GameTerms` adapta o vocabulário por jogo, como Operadores em Endfield e Mods em Warframe. Coil carrega imagens remotas, com fundos locais e iniciais como fallback. Os cartões de seleção podem crescer conforme o conteúdo e o tamanho da fonte.

O ViewModel mantém o estado durante mudanças de configuração. Um `SavedStateHandle` fornecido pela factory do Android permite restaurar jogo, personagem, busca e filtros após recriação do processo com a tarefa preservada. Apenas nomes, IDs e textos de seleção são salvos; roster e builds são recarregados dos catálogos atuais, descartando seleções indisponíveis. Voltar aos jogos ou trocar de jogo limpa as seleções anteriores. Esse estado não é um histórico permanente e não é restaurado após force-stop ou remoção da tarefa.

## Builds e referências

Builds incluem tipo, versão, arma, equipamentos, prioridades, equipe, notas e fontes. Os links servem como referências e não são lidos automaticamente. NTE e Warframe usam os catálogos locais; Endfield pode receber atualizações de conteúdo pelo catálogo publicado neste repositório.

Atualizações de roster, snapshot e formato exigem uma versão compatível do aplicativo. A baseline de cada jogo e os snapshots acompanhados estão descritos no [README](../README.md).

### Catálogo remoto de Endfield

A factory cria `RemoteEndfieldRepository`, que mantém os catálogos locais como reserva. O ViewModel carrega o cache em segundo plano e depois consulta `config/endfield-builds-v1.json` em main. A tela oferece atualização manual, data de publicação e feedback em falhas. O documento só substitui as builds após validação integral e gravação atômica do cache; revisões antigas ou alteradas sem incremento são recusadas.

A versão inicial aceita apenas os 31 operadores conhecidos, seus IDs de build, F2P, equipe vazia e o snapshot atual. O download usa HTTPS fixo, não segue redirecionamentos, tem timeouts e limite de 256 KiB. Falhas preservam o cache/catálogo local. Ao receber dados, o ViewModel atualiza somente o detalhe Endfield atualmente aberto, sem perder busca, filtros ou navegação. Veja [o contrato e o fluxo de publicação](../config/ENDFIELD.md).

A revisão de acessibilidade das recomendações continua no fluxo de PR. Esta etapa não automatiza a leitura dos sites.

## Textos remotos e funcionamento offline

`TermsRepository` combina os textos empacotados em `app/src/main/assets/terms.json` com o último cache válido. A atualização remota roda em `Dispatchers.IO` e consulta exclusivamente `config/terms.json` na branch `main` deste repositório.

O download exige HTTPS, não segue redirecionamentos, usa timeouts e limita o documento a 64 KiB. `TermsDocumentParser` valida schema, chaves, quantidade de entradas e valores. Se a atualização falhar, a interface mantém os textos já carregados.

A configuração remota altera apenas os textos mapeados. Ela não modifica catálogos, lógica de navegação ou funcionalidades. Consulte [o catálogo de termos](../config/README.md) para o fluxo de edição.

## Verificação

A CI Android compila o APK debug, executa testes unitários e Android lint, compila a release endurecida e disponibiliza o APK debug identificado pela versão.

Os testes cobrem integridade e isolamento dos catálogos, filtros e transições de estado, formatação do compartilhamento, vocabulário por jogo e validação dos termos. A CI atual não executa testes instrumentados de interface; fontes ampliadas, TalkBack, imagens offline e abertura de aplicativos externos precisam de verificação em dispositivo ou emulador.
