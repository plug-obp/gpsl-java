# GPSL VS Code Extension (client)

This is a minimal VS Code client for the GPSL Language Server in this repo.

## Prerequisites
- Java 21+ (repo uses toolchains 23)
- Build the server distribution:

```
./gradlew :gpsl-lsp:installDist
```

This creates the runnable script at:
- macOS/Linux: `gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp`
- Windows: `gpsl-lsp\\build\\install\\gpsl-lsp\\bin\\gpsl-lsp.bat`

## Develop
```
cd gpsl-lsp-vscode
npm install
npm run compile
```

In VS Code, run the "Launch Extension" configuration (F5) and open a folder with `.gpsl` files.

The extension launches the server script on stdio and shows diagnostics as you type.
