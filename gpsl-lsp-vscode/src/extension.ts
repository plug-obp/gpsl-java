import * as vscode from 'vscode';
import * as path from 'node:path';
import * as fs from 'node:fs';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient/node';

let client: LanguageClient | undefined;

function findJavaExecutable(): string | null {
  // 1. Check user configuration
  const config = vscode.workspace.getConfiguration('gpsl');
  const configuredJavaHome = config.get<string>('javaHome');
  
  if (configuredJavaHome) {
    const javaPath = path.join(configuredJavaHome, 'bin', process.platform === 'win32' ? 'java.exe' : 'java');
    if (fs.existsSync(javaPath)) {
      console.log(`Using configured Java: ${javaPath}`);
      return javaPath;
    } else {
      console.warn(`Configured Java not found at: ${javaPath}`);
    }
  }

  // 2. Check JAVA_HOME environment variable
  const javaHome = process.env.JAVA_HOME;
  if (javaHome) {
    const javaPath = path.join(javaHome, 'bin', process.platform === 'win32' ? 'java.exe' : 'java');
    if (fs.existsSync(javaPath)) {
      console.log(`Using JAVA_HOME: ${javaPath}`);
      return javaPath;
    } else {
      console.warn(`JAVA_HOME set but java not found at: ${javaPath}`);
    }
  }

  // 3. Try system java
  const javaCommand = process.platform === 'win32' ? 'java.exe' : 'java';
  console.log(`Using system Java: ${javaCommand}`);
  return javaCommand;
}

export function activate(context: vscode.ExtensionContext) {
  const javaExecutable = findJavaExecutable();
  
  if (!javaExecutable) {
    const message = 'Java 23 or higher is required for GPSL language support.\n\n' +
                   'Please install Java and either:\n' +
                   '1. Set JAVA_HOME environment variable, or\n' +
                   '2. Configure "gpsl.javaHome" in VS Code settings';
    vscode.window.showErrorMessage(message);
    console.error('Java not found');
    return;
  }

  // Look for bundled server first, then fall back to development location
  const serverScript = process.platform === 'win32' ? 'gpsl-lsp.bat' : 'gpsl-lsp';
  const bundledServerPath = path.join(context.extensionPath, 'server', 'bin', serverScript);
  const devServerPath = path.join(context.extensionPath, '..', 'gpsl-lsp', 'build', 'install', 'gpsl-lsp', 'bin', serverScript);
  
  let serverPath: string;
  if (fs.existsSync(bundledServerPath)) {
    serverPath = bundledServerPath;
    console.log(`Using bundled LSP server: ${serverPath}`);
  } else if (fs.existsSync(devServerPath)) {
    serverPath = devServerPath;
    console.log(`Using development LSP server: ${serverPath}`);
  } else {
    const message = 'GPSL LSP server not found.\n\n' +
                   'If you are developing the extension, please build it first:\n' +
                   './gradlew :gpsl-lsp:installDist';
    vscode.window.showErrorMessage(message);
    console.error('LSP server not found at:', bundledServerPath, 'or', devServerPath);
    return;
  }

  // Prepare environment with correct JAVA_HOME if we found a specific Java installation
  const serverEnv = { ...process.env };
  
  // If javaExecutable is a full path (not just "java"), set JAVA_HOME
  if (javaExecutable.includes(path.sep)) {
    // Extract JAVA_HOME from the executable path (remove /bin/java)
    const javaHome = path.dirname(path.dirname(javaExecutable));
    serverEnv.JAVA_HOME = javaHome;
    console.log(`Setting JAVA_HOME for LSP server: ${javaHome}`);
  }

  const serverOptions: ServerOptions = {
    command: serverPath,
    args: [],
    options: {
      env: serverEnv
    }
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
