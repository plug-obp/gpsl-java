# GPSL Language Server (LSP)

Language Server Protocol implementation for GPSL.

## Overview

This module provides a complete LSP server for GPSL, enabling rich IDE features in any editor that supports LSP.

## Features

### Implemented

- **Real-time Diagnostics**: Syntax and semantic error checking as you type
- **Go to Definition**: Navigate to symbol declarations (F12 or Ctrl+Click)
- **Document Symbols**: Outline view showing document structure
  - Declarations (internal and external)
  - Let bindings with hierarchical nesting
  - Automaton components (states, transitions)

### Planned

- Find References
- Code Completion
- Hover Information
- Rename Symbol

## Architecture

Built on Eclipse LSP4J, the server provides:

- `GPSLLanguageServer` - Main LSP server implementation
- `GPSLTextDocumentService` - Text document operations (diagnostics, navigation, symbols)
- `GPSLWorkspaceService` - Workspace-level operations
- `DocumentSymbolVisitor` - AST visitor for extracting document symbols

## Building

```bash
# From repository root
./gradlew :gpsl-lsp:build

# Create distribution
./gradlew :gpsl-lsp:installDist

# Run server
./gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp
```

The server communicates via stdin/stdout using the LSP protocol.

## Usage

The LSP server is primarily used through editor integrations:

### VS Code Extension

See [gpsl-lsp-vscode](../gpsl-lsp-vscode/) for the VS Code extension that uses this server.

### Manual Integration

Any LSP-compatible editor can use this server:

```json
{
  "command": "/path/to/gpsl-lsp/bin/gpsl-lsp",
  "args": [],
  "filetypes": ["gpsl"]
}
```

## Dependencies

- `gpsl-core` - Core language parser and AST
- Eclipse LSP4J 0.21.1 - LSP framework
- JUnit 5 (testing)

## Testing

Comprehensive test suite covering:
- Document symbol extraction for all constructs
- Go to definition accuracy
- Multiline atom handling
- Nested structure representation

Tests: 90+ across various LSP features

## Development

### Running in Development

```bash
# Build and run
./gradlew :gpsl-lsp:installDist
./gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp
```

### Testing

```bash
./gradlew :gpsl-lsp:test
```

## See Also

- [Main README](../README.md) - Project overview
- [VS Code Extension](../gpsl-lsp-vscode/README.md) - Editor integration
- [LSP Specification](https://microsoft.github.io/language-server-protocol/) - Protocol reference
