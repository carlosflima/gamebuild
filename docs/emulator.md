# Android Emulator

## Recomendação

Use Android Studio para instalar o Android SDK e criar um AVD. O desenvolvimento pode continuar no VS Code.

### Perfil sugerido

- Device: Pixel 8 ou equivalente
- API: 35
- ABI: x86_64
- RAM: 4 GB ou mais

## VS Code

Abra a pasta raiz do projeto. Com o AVD iniciado, conecte o dispositivo via ADB.

No Windows, após gerar o Gradle Wrapper pelo Android Studio/Gradle, use:

```powershell
.\gradlew.bat assembleDebug
adb shell am start -n com.carlosflima.gamebuild.debug/com.carlosflima.gamebuild.MainActivity
```

No Linux/macOS:

```bash
./gradlew assembleDebug
adb shell am start -n com.carlosflima.gamebuild.debug/com.carlosflima.gamebuild.MainActivity
```
