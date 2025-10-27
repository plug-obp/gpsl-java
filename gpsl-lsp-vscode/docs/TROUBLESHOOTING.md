# Troubleshooting GPSL VS Code Extension

This guide helps resolve common issues. For general information, see [README.md](../README.md).

## Java Installation

### Requirements

The extension requires **Java 23 or higher**. The extension searches for Java in this order:

1. `gpsl.javaHome` VS Code setting
2. `JAVA_HOME` environment variable
3. System `PATH`

### Installing Java

Download Java from one of these sources:

- [Eclipse Temurin](https://adoptium.net/) (recommended, open-source)
- [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK](https://openjdk.org/)

Or use a version manager like [SDKMAN](https://sdkman.io/):

### Install Java

```bash
# Using SDKMAN! (recommended)
sdk install java 23-tem
```

### Verify Java Installation

```bash
java -version
```

Should show version 23 or higher.

### Configure Java Path

If Java is installed but not detected, set the path in VS Code settings:

**Method 1: VS Code Settings UI**

1. Open Settings (Ctrl+, or Cmd+,)
2. Search for "gpsl.javaHome"
3. Enter the Java installation path (e.g., `/usr/lib/jvm/java-23-openjdk`)

**Method 2: settings.json**

```json
```json
{
  "gpsl.javaHome": "/path/to/your/java23+"
}
```

### Set JAVA_HOME

```bash
export JAVA_HOME=/path/to/your/java23+
export PATH=$JAVA_HOME/bin:$PATH
```
```

**Method 3: Environment Variable**

```bash
export JAVA_HOME=/path/to/your/java23+
```

Add to `~/.bashrc`, `~/.zshrc`, or equivalent for persistence.

## Error: "couldn't create connection to server" / "ENOENT"

This error means the LSP server executable is missing or not built.

### Solution for Development

Build the LSP server from the repository root:

```bash
./gradlew :gpsl-lsp:installDist
```

This creates the server at: `gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp`

### Solution for Installed Extension

The server should be bundled in the extension. If missing:

1. Reinstall the extension
2. Download the latest `.vsix` from [releases](https://github.com/plug-obp/gpsl-java/releases)
3. Install via: Extensions → "..." → "Install from VSIX..."

## Testing the Server Manually

Test if the server starts correctly (for development):

```bash
# From repository root
./gradlew :gpsl-lsp:installDist

# Test server startup
```bash
# Debug LSP server (requires Java 23+)
JAVA_HOME=/path/to/your/java23+ \
  /path/to/gpsl-lsp/bin/gpsl-lsp
```

The server should start and wait for LSP input (this is normal). Press Ctrl+C to exit.

## Debugging the Extension

### View Logs

1. Open Output panel: View → Output
2. Select "GPSL Language Server" from dropdown
3. Check for error messages

### Debug Mode (Development)

1. Open `gpsl-lsp-vscode` folder in VS Code
2. Press F5 to launch Extension Development Host
3. Check Debug Console for error messages
4. Check Output panel for server logs

## Common Issues

### Extension Not Activating

**Symptom**: No syntax highlighting, no LSP features

**Solution**: Open a `.gpsl` file to trigger activation. The extension only activates for `.gpsl` files.

### Syntax Highlighting Works but No LSP Features

**Symptom**: Colors appear but no diagnostics, go-to-definition, or outline

**Cause**: Language server failed to start

**Solutions**:

1. Verify Java 23+ is installed: `java -version`
2. Configure Java path (see [Java Installation](#java-installation) above)
3. Check Output panel for server errors
4. For development: Build server with `./gradlew :gpsl-lsp:installDist`

### Grammar Changes Not Showing

After modifying `syntaxes/gpsl.tmLanguage.json`:

- Reload window: Cmd+R (macOS) or Ctrl+R (Windows/Linux)
- Or: Command Palette → "Developer: Reload Window"

## Development Workflow

### First Time Setup

```bash
# Build LSP server (from repository root)
./gradlew :gpsl-lsp:installDist

# Install dependencies (from gpsl-lsp-vscode/)
npm install

# Compile extension
npm run compile
```

### After Code Changes

**Extension code** (`src/extension.ts`):

```bash
npm run compile
# Then reload Extension Development Host (Cmd+R)
```

**LSP server code** (Java):

```bash
./gradlew :gpsl-lsp:installDist
# Then reload Extension Development Host (Cmd+R)
```

**Watch mode** (auto-recompile extension):

```bash
npm run watch
```

## Getting More Help

If issues persist:

1. **Check logs**: Output panel → "GPSL Language Server"
2. **Check Debug Console** (when running with F5)
3. **Verify paths** in extension logs match actual locations
4. **Report issues**: [GitHub Issues](https://github.com/plug-obp/gpsl-java/issues)

Include in your report:

- VS Code version
- Extension version
- Java version (`java -version`)
- Operating system
- Error messages from Output panel
- Steps to reproduce   ```
