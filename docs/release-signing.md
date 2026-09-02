# Guia de build de release assinada

Este documento descreve o uso seguro do workflow manual `Signed Release Build` (`.github/workflows/release-signed.yml`).

O workflow atual **não cria GitHub Release, não publica em loja e não roda automaticamente**. Ele apenas gera APK/AAB assinados e `SHA256SUMS.txt` como artefato temporário do GitHub Actions.

## Pré-requisitos

Antes do primeiro teste:

1. confirme que a `main` está com o Android CI verde;
2. confira `versionName` e `versionCode` em `app/build.gradle.kts`;
3. tenha um keystore de release armazenado fora do repositório e com backup seguro;
4. nunca versione `.jks`, `.keystore`, senhas ou o conteúdo dos Secrets.

## Secrets necessários

Configure em **Settings → Secrets and variables → Actions** os seguintes Repository Secrets:

- `GAMEBUILDS_KEYSTORE_BASE64`: conteúdo do keystore codificado em Base64, em uma única linha;
- `GAMEBUILDS_KEYSTORE_PASSWORD`: senha do keystore;
- `GAMEBUILDS_KEY_ALIAS`: alias da chave usada para assinatura;
- `GAMEBUILDS_KEY_PASSWORD`: senha da chave.

Para gerar o valor Base64 localmente, use uma máquina confiável e não envie o arquivo resultante para o repositório. Exemplos:

```bash
# Linux/GNU coreutils
base64 -w 0 release.jks > release.jks.base64

# Alternativa portátil
base64 release.jks | tr -d '\n' > release.jks.base64
```

Copie o conteúdo de `release.jks.base64` para o Secret `GAMEBUILDS_KEYSTORE_BASE64` e remova o arquivo de texto Base64 após a configuração.

## Tag de teste

O workflow exige uma tag existente no formato `v<versionName>` e valida que ela pertence ao histórico da `main`.

Para `versionName = "0.3.5"`, a tag esperada é `v0.3.5`.

A criação da tag deve ser um ato explícito de versionamento. Antes disso, confirme o commit exato da `main` que será marcado. Não mova uma tag de release já publicada para outro commit.

Exemplo local, somente depois de escolher conscientemente o commit:

```bash
git checkout main
git pull --ff-only
git tag -a v0.3.5 -m "Game Builds v0.3.5"
git push origin v0.3.5
```

## Executar o workflow

No GitHub:

1. abra **Actions**;
2. selecione **Signed Release Build**;
3. escolha **Run workflow**;
4. informe a tag existente, por exemplo `v0.3.5`;
5. execute o workflow.

O job valida formato da tag, vínculo com a `main`, `versionName`, `versionCode` e presença dos Secrets antes da compilação assinada.

O keystore é reconstruído somente em `$RUNNER_TEMP`, recebe permissão restrita e é removido em um step `always()` ao final do job.

## Resultado esperado

Se a execução terminar com sucesso, o GitHub Actions disponibiliza por 14 dias um artefato `Game-Builds-<versionName>-signed-release` contendo:

- `Game-Builds-<versionName>-signed-release.apk`;
- `Game-Builds-<versionName>-signed-release.aab`;
- `SHA256SUMS.txt`.

O workflow verifica a assinatura do APK com `apksigner` e do AAB com `jarsigner` antes do upload.

Para conferir os checksums depois de baixar os três arquivos:

```bash
sha256sum -c SHA256SUMS.txt
```

## Checklist antes de qualquer publicação real

- [ ] Android CI da `main` verde;
- [ ] `versionName` e `versionCode` revisados;
- [ ] keystore original e backup guardados fora do repositório;
- [ ] quatro Repository Secrets configurados;
- [ ] tag `v<versionName>` criada no commit correto da `main`;
- [ ] `Signed Release Build` concluído com sucesso;
- [ ] assinatura do APK/AAB validada pelo workflow;
- [ ] `SHA256SUMS.txt` conferido após download;
- [ ] APK instalado/testado em dispositivo físico;
- [ ] publicação real autorizada explicitamente em etapa separada.

## Limites de segurança

Não coloque valores de Secrets em issues, PRs, comentários, screenshots ou logs. Não adicione comandos com `set -x` aos steps que manipulam credenciais. A existência deste workflow não é autorização para criar tags, publicar releases ou distribuir builds.
