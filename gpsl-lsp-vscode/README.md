# GPSL Language Support for Visual Studio Code

[![License](https://img.shields.io/github/license/plug-obp/gpsl-java)](https://github.com/plug-obp/gpsl-java/blob/main/LICENSE)

Full language support for **GPSL** (General Purpose Specification Language) - a temporal logic specification language for formal verification and automata generation.

## Documentation

- **[SYNTAX_REFERENCE.md](docs/SYNTAX_REFERENCE.md)** - Complete GPSL syntax and operator reference with examples
- **[TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and release notes

For developers:
- **[VERSION_MANAGEMENT.md](docs/VERSION_MANAGEMENT.md)** - Release process and versioning guidelines

## Features

### Syntax Highlighting

Full syntax highlighting for all GPSL constructs including:
- Propositional logic operators (and, or, not, implies, iff, xor)
- Temporal logic operators (next, eventually, globally, until, release)
- Boolean literals and atoms (pipe-delimited `|...|` and quoted `"..."`)
- Let expressions with bindings
- NFA and Büchi automata declarations
- Comments (line `//` and block `/* */`)

> **Note**: Single `|` is reserved for atom delimiters. Use `||` or `or` for disjunction.

See [SYNTAX_REFERENCE.md](docs/SYNTAX_REFERENCE.md) for complete operator list and examples.

### Language Server Protocol (LSP)

Advanced language features powered by a Java-based language server:

- **Diagnostics** - Real-time error checking and validation
- **Go to Definition** - Navigate to symbol definitions (Ctrl+Click or F12)
- **Document Symbols** - Outline view showing document structure (Ctrl+Shift+O)

Future planned features:
- Find References
- Code Completion
- Hover Information

### Editor Features

- Auto-closing pairs for `()`, `[]`, `{}`, `||`, `""`
- Comment toggling (Ctrl+/ for line, Shift+Alt+A for block)
- Bracket matching and highlighting
- Smart indentation

## Requirements

**Java 23 or higher** is required to run the GPSL Language Server.

The extension automatically searches for Java in this order:
1. `gpsl.javaHome` setting in VS Code
2. `JAVA_HOME` environment variable  
3. System `PATH`

> For Java installation instructions and troubleshooting, see [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md#java-installation).

## Installation

### From VS Code Marketplace

1. Open VS Code Extensions (Ctrl+Shift+X / Cmd+Shift+X)
2. Search for "GPSL Language Support"
3. Click Install

### From VSIX File

1. Download `.vsix` from [releases page](https://github.com/plug-obp/gpsl-java/releases)
2. In VS Code: Extensions → "..." menu → "Install from VSIX..."
3. Select the downloaded file

## Configuration

Configure the extension via VS Code settings:

```json
{
  "gpsl.javaHome": "/path/to/your/java/installation"
}
```

| Setting | Type | Default | Description |
|---------|------|---------|-------------|
| `gpsl.javaHome` | string | `""` | Path to Java installation (Java 23+). If empty, uses JAVA_HOME or PATH. |


## GPSL Language Examples

### Basic Temporal Logic

```gpsl
// Define propositions
p = |request|;
q = |grant|;

// Temporal properties
safety = always (p -> eventually q);
liveness = always eventually p;
mutual_exclusion = always !(p && q);
```

### Automaton Example

```gpsl
myAutomaton = states s0, s1;
    initial s0;
    accept s1;
    s0 [p] s1;
    s1 [q] s0
```

For more examples and complete syntax reference, see [SYNTAX_REFERENCE.md](docs/SYNTAX_REFERENCE.md).

## Troubleshooting

See [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) for common issues and solutions.

Quick checks:

- **Extension not activating?** Open a `.gpsl` file to trigger activation
- **Java errors?** See [Java installation guide](docs/TROUBLESHOOTING.md#java-installation)
- **Server not starting?** Check Output panel: View → Output → "GPSL Language Server"

## Development

### Building from Source

```bash
# Clone repository
git clone https://github.com/plug-obp/gpsl-java.git
cd gpsl-java

# Build Java components
./gradlew build

# Build extension
cd gpsl-lsp-vscode
npm install
npm run bundle-server  # Bundles the LSP server
npm run compile        # Compiles TypeScript
npm run package        # Creates .vsix file
```

See [VERSION_MANAGEMENT.md](docs/VERSION_MANAGEMENT.md) for release process.

## Contributing

Contributions welcome! See the [main repository](https://github.com/plug-obp/gpsl-java) for guidelines.

## License

MIT License - see [LICENSE](https://github.com/plug-obp/gpsl-java/blob/main/LICENSE) file.

## Links

- [GitHub Repository](https://github.com/plug-obp/gpsl-java)
- [Issue Tracker](https://github.com/plug-obp/gpsl-java/issues)
- [Releases](https://github.com/plug-obp/gpsl-java/releases)
