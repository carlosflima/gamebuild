# Game Builds — Arquitetura

## Objetivo

Aplicativo Android para consultar builds versionadas de personagens de múltiplos jogos. Os catálogos são distribuídos no APK; cada build registra suas referências externas.

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

Builds incluem tipo, versão, arma, equipamentos, prioridades, equipe, notas e fontes. Esses dados são locais: os links servem como referências e não são baixados para substituir automaticamente as builds.

Atualizações de roster e builds exigem uma nova versão do aplicativo. A baseline de cada jogo e os snapshots acompanhados estão descritos no [README](../README.md).

## Textos remotos e funcionamento offline

`TermsRepository` combina os textos empacotados em `app/src/main/assets/terms.json` com o último cache válido. A atualização remota roda em `Dispatchers.IO` e consulta exclusivamente `config/terms.json` na branch `main` deste repositório.

O download exige HTTPS, não segue redirecionamentos, usa timeouts e limita o documento a 64 KiB. `TermsDocumentParser` valida schema, chaves, quantidade de entradas e valores. Se a atualização falhar, a interface mantém os textos já carregados.

A configuração remota altera apenas os textos mapeados. Ela não modifica catálogos, lógica de navegação ou funcionalidades. Consulte [o catálogo de termos](../config/README.md) para o fluxo de edição.

## Verificação

A CI Android compila o APK debug, executa testes unitários e Android lint, compila a release endurecida e disponibiliza o APK debug identificado pela versão.

Os testes cobrem integridade e isolamento dos catálogos, filtros e transições de estado, formatação do compartilhamento, vocabulário por jogo e validação dos termos. A CI atual não executa testes instrumentados de interface; fontes ampliadas, TalkBack, imagens offline e abertura de aplicativos externos precisam de verificação em dispositivo ou emulador.
