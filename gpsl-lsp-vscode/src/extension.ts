import * as vscode from 'vscode';
import * as path from 'node:path';
import * as fs from 'node:fs';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient/node';

let client: LanguageClient | undefined;

export function activate(context: vscode.ExtensionContext) {
  const serverScript = process.platform === 'win32' ? 'gpsl-lsp.bat' : 'gpsl-lsp';
  const serverPath = path.join(context.asAbsolutePath('..'), 'gpsl-lsp', 'build', 'install', 'gpsl-lsp', 'bin', serverScript);

  // Check if server exists
  if (!fs.existsSync(serverPath)) {
    const message = `GPSL LSP server not found at: ${serverPath}\n\nPlease build it first by running:\n./gradlew :gpsl-lsp:installDist`;
    vscode.window.showErrorMessage(message);
    console.error(message);
    return;
  }

  // Log server path for debugging
  console.log(`GPSL LSP server path: ${serverPath}`);

  // Set JAVA_HOME to use Java 23 (required for the LSP server)
  const env = { ...process.env };
  if (process.platform === 'darwin') {
    const javaHome = '/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home';
    if (fs.existsSync(javaHome)) {
      env.JAVA_HOME = javaHome;
      console.log(`Using JAVA_HOME: ${javaHome}`);
    } else {
      console.warn(`Java 23 not found at ${javaHome}, using system default`);
    }
  }

  const serverOptions: ServerOptions = {
    command: serverPath,
    args: [],
    options: { env }
  };

  const clientOptions: LanguageClientOptions = {
    documentSelector: [{ language: 'gpsl', scheme: 'file' }],
    synchronize: {},
    outputChannel: vscode.window.createOutputChannel('GPSL LSP'),
    traceOutputChannel: vscode.window.createOutputChannel('GPSL LSP Trace'),
    initializationOptions: {},
    // Disable trace to avoid setTrace warnings
    connectionOptions: {
      maxRestartCount: 5
    }
  };

  client = new LanguageClient('gpsl-lsp', 'GPSL Language Server', serverOptions, clientOptions);
  
  // Start the client (returns a Promise in v9)
  client.start().catch(err => {
    const message = `Failed to start GPSL LSP server: ${err.message}`;
    vscode.window.showErrorMessage(message);
    console.error('GPSL LSP start error:', err);
  });
  
  // Dispose by stopping the client when the extension deactivates
  context.subscriptions.push({ dispose: () => { client?.stop(); } });
}

export function deactivate(): Thenable<void> | undefined {
  return client?.stop();
}
