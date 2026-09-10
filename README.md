# Game Builds

Aplicativo Android para consultar, filtrar e comparar builds de personagens em uma interface focada em uso mobile.

## Estado atual — V0.3.5

A baseline funcional de Neverness to Everness (NTE) desta versão está concluída: o roster atualmente acompanhado pelo app possui cobertura de builds, seleção de personagem, alternativas Meta/F2P quando aplicáveis, comparação rápida e apresentação visual de armas e equipamentos.

A estrutura local de dados para Warframe e Arknights: Endfield também já foi inicializada e registrada no repositório. Esses jogos permanecem indisponíveis na interface até que seus catálogos recebam conteúdo mínimo confiável.

### Jogos

- **Neverness to Everness (NTE)** — baseline funcional concluída e disponível.
- **Warframe** — estrutura de dados inicializada; conteúdo em preparação.
- **Arknights: Endfield** — estrutura de dados inicializada; conteúdo em preparação.

Os jogos ainda indisponíveis aparecem na seleção inicial com ação desabilitada e indicação **Em breve**.

## Funcionalidades

- seleção de jogo e personagem;
- busca e filtros de personagens;
- filtros de builds **Todas / Meta / F2P** quando aplicável;
- comparação rápida entre alternativas de build;
- imagens de personagens, armas e equipamentos;
- popup de atributos especiais para equipamentos compatíveis;
- fundos visuais específicos para a tela inicial e páginas internas;
- fallback local no fundo da página de personagem quando a imagem remota não carregar;
- navegação de retorno por botão e gesto do Android;
- fontes externas associadas às builds;
- pipeline de CI com build debug, testes unitários, lint e build release endurecida.

## Arquitetura e tecnologia

- Kotlin;
- Jetpack Compose + Material 3;
- ViewModel + StateFlow;
- Repository Pattern;
- fontes e catálogos locais separados por jogo;
- Coil para carregamento de imagens;
- GitHub Actions;
- Java 17;
- Android compileSdk 37 e targetSdk 35.

## Desenvolvimento

Abra o projeto no Android Studio para configurar SDK/AVD e executar o aplicativo.

Com o ambiente Android configurado, as principais verificações usadas no CI são:

```bash
gradle assembleDebug
gradle testDebugUnitTest
gradle lintDebug
gradle assembleRelease
```

## Teste em dispositivo físico

O CI gera uma APK debug com o padrão `Game-Builds-<versionName>-debug`. Ela é usada para as rodadas de validação mobile antes de qualquer etapa de release assinada.

## Release assinada

O repositório possui um workflow separado e manual para gerar APK/AAB assinados usando GitHub Actions Secrets, sem versionar keystore ou credenciais. O fluxo valida tag e versão, verifica as assinaturas e gera `SHA256SUMS.txt`, mas não publica GitHub Release nem envia artefatos para lojas automaticamente.

As instruções de configuração e teste seguro estão em [`docs/release-signing.md`](docs/release-signing.md).

## Próximos passos

- povoar o catálogo de Warframe com um primeiro conjunto pequeno e verificável de personagens/builds antes de habilitá-lo na UI;
- iniciar depois o catálogo de Arknights: Endfield com o mesmo critério de qualidade;
- continuar melhorias incrementais no NTE quando houver novos dados confiáveis, sem reabrir a baseline já concluída;
- melhorar fallbacks locais para imagens remotas e a evolução visual das páginas;
- configurar e testar o workflow manual de release assinada em ambiente controlado antes de qualquer publicação real.
