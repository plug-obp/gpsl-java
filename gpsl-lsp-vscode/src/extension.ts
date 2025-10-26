import * as vscode from 'vscode';
import * as path from 'node:path';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient/node';

let client: LanguageClient | undefined;

export function activate(context: vscode.ExtensionContext) {
  const serverScript = process.platform === 'win32' ? 'gpsl-lsp.bat' : 'gpsl-lsp';
  const serverPath = path.join(context.asAbsolutePath('..'), 'gpsl-lsp', 'build', 'install', 'gpsl-lsp', 'bin', serverScript);

  const serverOptions: ServerOptions = {
    command: serverPath,
    args: [],
    options: { env: process.env }
  };

  const clientOptions: LanguageClientOptions = {
    documentSelector: [{ language: 'gpsl', scheme: 'file' }],
    synchronize: {},
    outputChannel: vscode.window.createOutputChannel('GPSL LSP')
  };

  client = new LanguageClient('gpsl-lsp', 'GPSL Language Server', serverOptions, clientOptions);
  context.subscriptions.push(client.start());
}

export function deactivate(): Thenable<void> | undefined {
  return client?.stop();
}
