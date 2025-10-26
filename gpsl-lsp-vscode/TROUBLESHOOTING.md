# Troubleshooting GPSL VS Code Extension

## Error: "couldn't create connection to server" / "ENOENT"

This error means the LSP server hasn't been built yet.

### Solution

Build the LSP server by running this command from the repository root:

```bash
./gradlew :gpsl-lsp:installDist
```

This creates the server at: `gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp`

### Verify the server exists

```bash
ls -la gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp
```

You should see the executable script file.

## Error: Java version issues

The extension expects Java 23 at:
```
/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home
```

### Check your Java installation

```bash
ls -la /Library/Java/JavaVirtualMachines/
```

### If Java 23 is not installed

1. Download from: https://adoptium.net/
2. Or use SDKMAN:
   ```bash
   sdk install java 23-tem
   ```

### If Java 23 is in a different location

Update the `JAVA_HOME` path in `src/extension.ts`:

```typescript
env.JAVA_HOME = '/path/to/your/java23';
```

Then recompile:
```bash
pnpm run compile
```

## Testing the Server Manually

You can test if the server starts correctly:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home \
  gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp
```

The server should start and wait for LSP input (this is normal). Press Ctrl+C to exit.

## Debugging the Extension

1. Open `gpsl-lsp-vscode` folder in VS Code
2. Press F5 to launch the extension in debug mode
3. Check the "Debug Console" for error messages
4. Check the "GPSL LSP" output channel for server logs

## Development Workflow

### First time setup

```bash
# From repository root
./gradlew :gpsl-lsp:installDist

# From gpsl-lsp-vscode directory
pnpm install
pnpm run compile
```

### After making changes

**To extension code:**
```bash
cd gpsl-lsp-vscode
pnpm run compile
```
Then reload the extension window (Cmd+R in the Extension Development Host)

**To LSP server code:**
```bash
./gradlew :gpsl-lsp:installDist
```
Then reload the extension window

### Watch mode

For automatic recompilation of extension code:
```bash
cd gpsl-lsp-vscode
pnpm run watch
```

## Common Issues

### Extension not activating

Make sure you have a `.gpsl` file open. The extension only activates when a GPSL file is opened.

### Syntax highlighting works but no LSP features

This means the server failed to start. Check:
1. Server is built: `./gradlew :gpsl-lsp:installDist`
2. Java 23 is available
3. Check the "GPSL LSP" output channel for error messages

### Changes to grammar not showing

After modifying `syntaxes/gpsl.tmLanguage.json`, you need to reload the window:
- In Extension Development Host: Cmd+R
- Or: Run "Developer: Reload Window" from command palette

## Getting Help

If issues persist:

1. Check the extension logs:
   - View → Output → Select "GPSL LSP" or "GPSL LSP Trace"

2. Check the Debug Console when running in debug mode (F5)

3. Verify file paths in extension logs match actual file locations

4. Make sure the extension folder structure is:
   ```
   gpsl-java/
   ├── gpsl-lsp/
   │   └── build/install/gpsl-lsp/bin/gpsl-lsp
   └── gpsl-lsp-vscode/
       ├── out/extension.js
       └── ...
   ```
