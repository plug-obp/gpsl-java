# GPSL Language

![Build Status](https://github.com/plug-obp/gpsl-java/actions/workflows/build-and-publish.yml/badge.svg)
![Tests](https://img.shields.io/badge/tests-322%20passing-success)
![Java](https://img.shields.io/badge/java-23-blue)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fplug-obp%2Fgpsl-java.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fplug-obp%2Fgpsl-java?ref=badge_shield&issueType=license)

**Generic Property Specification Language** (GPSL) is a property specification language for formal verification and model checking. It supports Linear Temporal Logic (LTL), Büchi Automata, and NFA specifications.

## Overview

GPSL is **only** a *property-specification* language. Methodologically it is orthogonal to the formalisms used for *capturing the operational environment* (xGDL scenarios) and for *taming the state-space explosion* problem during model-checking (state-space decomposition, pruning through state-constraints - TLA, etc.).

The main characteristic of GPSL is its independence from the formalism used for model specification. To achieve this independence relation, the GPSL language delegates the evaluation of the atomic properties to the *verification model* semantics. Thus, from the perspective of the specification language, the atomic propositions are simply a mapping of names to booleans. In other words, the GPSL semantics binds the *property* to the *verification model* through a semantics-driven evaluation function.

In GPSL each property is associated with a named variable. All these variables form the *property set*. Any *property* can be verified during an analysis run.

More details on GPSL are available at <http://www.obpcdl.org/gpsl/>

## Project Structure

This repository contains multiple components:

### Core Components

- **[gpsl-core](gpsl-core/)** - Core GPSL language parser, AST, and semantics
  - ANTLR4-based parser for GPSL syntax
  - Abstract Syntax Tree (AST) representations
  - Symbol resolution and semantic analysis
  - Position tracking for error reporting and IDE features

- **[gpsl-ltl3ba](gpsl-ltl3ba/)** - LTL to Büchi Automata converter
  - Converts Linear Temporal Logic formulas to Büchi automata
  - Wraps the native LTL3BA tool
  - Platform-specific binaries for Linux, macOS, and Windows

- **[gpsl-lsp](gpsl-lsp/)** - Language Server Protocol implementation
  - LSP server for IDE integration
  - Real-time diagnostics and error checking
  - Go to Definition, Document Symbols
  - Based on Eclipse LSP4J

### Editor Support

- **[gpsl-lsp-vscode](gpsl-lsp-vscode/)** - Visual Studio Code extension
  - Full syntax highlighting for GPSL
  - Language Server integration (diagnostics, navigation, outline)
  - Code snippets and auto-completion
  - Available on VS Code Marketplace

## Features

- **Temporal Logic**: Full support for LTL operators (next, eventually, globally, until, release)
- **Propositional Logic**: Standard boolean operators with multiple syntax variants
- **Automata**: Büchi and NFA specifications with priorities
- **Let Expressions**: Scoped variable bindings for complex formulas
- **Atoms**: Flexible atom notation with pipe (`|...|`) and quote (`"..."`) delimiters
- **IDE Support**: Full language server with diagnostics, navigation, and outline

## Getting Started

### Prerequisites

- Java 17 or higher (JDK 23 recommended)
- Gradle 8.x (wrapper included)

### Building

```bash
# Clone the repository
git clone https://github.com/plug-obp/gpsl-java.git
cd gpsl-java

# Build all components
./gradlew build

# Run tests
./gradlew test
```

### Using the VS Code Extension

Install from VS Code Marketplace:
1. Open VS Code Extensions (Ctrl+Shift+X / Cmd+Shift+X)
2. Search for "GPSL Language Support"
3. Click Install

Or install from source:
```bash
cd gpsl-lsp-vscode
npm install
npm run bundle-server
npm run compile
npm run package
code --install-extension gpsl-lsp-vscode-*.vsix
```

See [gpsl-lsp-vscode/README.md](gpsl-lsp-vscode/README.md) for details.

## Documentation

- **[CHANGELOG.md](CHANGELOG.md)** - Version history and release notes
- **[GPSL Syntax Reference](gpsl-lsp-vscode/docs/SYNTAX_REFERENCE.md)** - Complete language syntax guide
- **[VS Code Extension Guide](gpsl-lsp-vscode/README.md)** - Editor integration documentation

## Examples

```gpsl
// Temporal properties
safety = always (request -> eventually grant);
liveness = always eventually ready;

// Büchi automaton
mutex = states s0, s1;
    initial s0;
    accept s1;
    s0 [!critical1 && !critical2] s0;
    s0 [critical1 && !critical2] s1;
    s1 [!critical1] s1
```

More examples in [gpsl-lsp-vscode/examples/](gpsl-lsp-vscode/examples/).

## Contributing

Contributions are welcome! Please see the issue tracker for areas that need help.

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fplug-obp%2Fgpsl-java.svg?type=large&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fplug-obp%2Fgpsl-java?ref=badge_large&issueType=license)

## Links

- [GPSL Website](http://www.obpcdl.org/gpsl/)
- [Issue Tracker](https://github.com/plug-obp/gpsl-java/issues)
- [VS Code Extension](https://marketplace.visualstudio.com/items?itemName=obp3.gpsl-lsp-vscode)