# Version Management Guide

This document describes how to manage versions and create releases for the GPSL VS Code extension.

## Versioning Strategy

The GPSL VS Code extension follows [Semantic Versioning](https://semver.org/) (SemVer):

```
MAJOR.MINOR.PATCH
```

- **MAJOR**: Incompatible API changes or breaking changes
- **MINOR**: New features in a backward-compatible manner
- **PATCH**: Backward-compatible bug fixes

### Version Synchronization

The extension version should generally align with the GPSL Java library versions, but may diverge:

- **Extension version**: Reflects the extension's own feature set and bug fixes
- **LSP server version**: The bundled Java LSP server version (tracked separately in Java modules)

## Release Process

### 1. Update Version Number

Update the version in `package.json`:

```bash
cd gpsl-lsp-vscode
npm version [major|minor|patch]  # or specify exact version like 1.2.3
```

This updates:
- `package.json` version field
- Creates a git tag (if in a git repo)

### 2. Update CHANGELOG.md

Add release notes to `CHANGELOG.md`:

```markdown
## [X.Y.Z] - YYYY-MM-DD

### Added
- New features

### Changed
- Changes in existing functionality

### Fixed
- Bug fixes

### Removed
- Removed features
```

### 3. Build and Test Locally

```bash
# Build the LSP server
cd /path/to/gpsl-java
./gradlew :gpsl-lsp:installDist

# Bundle and package the extension
cd gpsl-lsp-vscode
npm install
npm run bundle-server
npm run compile
npm run package
```

This creates a `.vsix` file in the extension directory.

### 4. Test the VSIX Package

Install the `.vsix` file locally to test:

1. In VS Code, go to Extensions
2. Click "..." menu → "Install from VSIX..."
3. Select the generated `.vsix` file
4. Test all features with `.gpsl` files

### 5. Create a Git Tag

```bash
git add .
git commit -m "Release v1.0.0"
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main
git push origin v1.0.0
```

### 6. Automated Release (Recommended)

The GitHub Actions workflow (`.github/workflows/vscode-extension-release.yml`) automatically:

1. Triggers on version tags (`v*.*.*`)
2. Builds the Java components
3. Bundles the LSP server
4. Compiles the extension
5. Creates a `.vsix` package
6. Creates a GitHub Release
7. Uploads the `.vsix` as a release asset

Just push a version tag to trigger:

```bash
git tag v1.0.0
git push origin v1.0.0
```

### 7. Manual Release (Alternative)

If not using GitHub Actions:

1. Build and package as described above
2. Go to GitHub repository → Releases → "Draft a new release"
3. Choose the tag (or create new tag `v1.0.0`)
4. Set release title: "Release v1.0.0"
5. Add release notes from CHANGELOG.md
6. Upload the `.vsix` file
7. Publish release

## Publishing to VS Code Marketplace (Optional)

### Prerequisites

1. Create a [Visual Studio Marketplace publisher account](https://marketplace.visualstudio.com/manage)
2. Generate a Personal Access Token (PAT) in Azure DevOps
3. Store the PAT as a GitHub secret named `VSCE_PAT`

### Manual Publishing

```bash
cd gpsl-lsp-vscode
npm install -g @vscode/vsce
vsce login <publisher-name>
vsce publish
```

### Automated Publishing

Uncomment the last step in `.github/workflows/vscode-extension-release.yml`:

```yaml
- name: Publish to VS Code Marketplace
  working-directory: gpsl-lsp-vscode
  run: vsce publish -p ${{ secrets.VSCE_PAT }}
```

## Version History

Track all versions in CHANGELOG.md following the Keep a Changelog format.

## Example Release Workflow

```bash
# 1. Make changes and commit
git add .
git commit -m "Add new feature"

# 2. Update version
cd gpsl-lsp-vscode
npm version minor  # 0.1.0 → 0.2.0

# 3. Update CHANGELOG.md
# Edit CHANGELOG.md to add release notes

# 4. Commit version changes
git add .
git commit -m "Bump version to 0.2.0"

# 5. Create and push tag
git tag -a v0.2.0 -m "Release 0.2.0"
git push origin main
git push origin v0.2.0

# GitHub Actions will automatically build and create the release
```

## Rollback

If a release has issues:

1. Create a hotfix with a patch version
2. Delete the problematic tag and release from GitHub
3. Never reuse version numbers

## Breaking Changes

When making breaking changes (e.g., grammar changes affecting syntax):

1. Increment MAJOR version
2. Document breaking changes clearly in CHANGELOG.md
3. Consider adding migration guide in release notes
4. Communicate to users via release announcement

## Pre-releases

For testing before official release:

```bash
# Use pre-release version
npm version 1.0.0-beta.1

# Tag as pre-release
git tag v1.0.0-beta.1
git push origin v1.0.0-beta.1

# Mark as pre-release on GitHub
```
