# Distribution Setup for Maintainers

> For maintainers only. Users: see [README.md](../README.md).

This document covers building and releasing the extension.

## Local Build

```bash
# From repository root
./gradlew :gpsl-lsp:installDist

# From gpsl-lsp-vscode/
npm install
npm run bundle-server
npm run compile
npm run package
```

Creates `gpsl-lsp-vscode-X.Y.Z.vsix`

## Release Process

See [VERSION_MANAGEMENT.md](VERSION_MANAGEMENT.md) for detailed release instructions.

## Package Contents

- `out/` - Compiled TypeScript
- `server/` - Bundled LSP server (Java)
- `syntaxes/`, `snippets/` - Language support
- `README.md`, `CHANGELOG.md` - Documentation

## Automated Release

GitHub Actions automatically builds and releases when you push a version tag (`v*.*.*`).

See `.github/workflows/vscode-extension-release.yml` for details.
