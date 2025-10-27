# GPSL Language Support for Visual Studio Code

[![License](https://img.shields.io/github/license/plug-obp/gpsl-java)](https://github.com/plug-obp/gpsl-java/blob/main/LICENSE)

Full language support for **GPSL** (General Purpose Specification Language) - a temporal logic specification language for formal verification and automata generation.

## Features

### Syntax Highlighting

Full syntax highlighting for all GPSL constructs:

- **Propositional Logic Operators**
  - Conjunction: `and`, `&`, `&&`, `/\`, `*`, `∧`
  - Disjunction: `or`, `||`, `\/`, `+`, `∨` (Note: single `|` is reserved for atom delimiters)
  - Negation: `not`, `!`, `~`
  - Implication: `implies`, `->`, `=>`, `→`, `⟹`
  - Equivalence: `iff`, `<->`, `<=>`, `⟺`, `↔`
  - XOR: `xor`, `^`, `⊻`, `⊕`

- **Temporal Logic Operators**
  - Next: `next`, `N`, `X`, `o`, `()`, `◯`
  - Eventually: `eventually`, `F`, `<>`, `♢`, `◇`
  - Globally: `globally`, `always`, `G`, `[]`, `☐`
  - Until (strong): `until`, `U`, `SU`, `strong-until`
  - Until (weak): `W`, `WU`, `weak-until`
  - Release (strong): `M`, `SR`, `strong-release`
  - Release (weak): `R`, `WR`, `weak-release`

- **Other Language Features**
  - Boolean literals: `true`, `false`, `0`, `1`
  - Atoms: `|...|` (pipe-delimited) and `"..."` (quoted)
  - Let expressions with bindings
  - NFA and Büchi automata declarations
  - Comments: `//` (line) and `/* */` (block)
  - Declaration operators: `=` (internal), `*=` (external)

### Language Server Protocol (LSP)

Advanced language features powered by a Java-based language server:

- **Diagnostics**: Real-time error checking and validation
- **Go to Definition**: Navigate to symbol definitions

**Planned Features**:
- Find References
- Code Completion
- Hover Information
- Document Symbols (outline view)

### Editor Features

- Auto-closing pairs for `()`, `[]`, `{}`, `||`, `""`
- Comment toggling with line and block comments
- Bracket matching and highlighting
- Smart indentation

## Requirements

This extension requires **Java 17 or higher** to run the GPSL Language Server.

The extension will automatically look for Java in the following order:
1. The `gpsl.javaHome` setting in VS Code
2. The `JAVA_HOME` environment variable
3. The system `PATH`

### Installing Java

If you don't have Java installed, you can download it from:
- [Eclipse Temurin](https://adoptium.net/) (recommended)
- [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK](https://openjdk.org/)

To verify Java is installed, run:
```bash
java -version
```

## Installation

### From VS Code Marketplace

1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X / Cmd+Shift+X)
3. Search for "GPSL Language Support"
4. Click Install

### From VSIX File

1. Download the `.vsix` file from the [releases page](https://github.com/plug-obp/gpsl-java/releases)
2. In VS Code, go to Extensions
3. Click the "..." menu → "Install from VSIX..."
4. Select the downloaded file

## Configuration

You can configure the extension through VS Code settings:

```json
{
  "gpsl.javaHome": "/path/to/your/java/installation"
}
```

### Settings

| Setting | Type | Default | Description |
|---------|------|---------|-------------|
| `gpsl.javaHome` | string | `""` | Path to the Java installation. If not set, uses JAVA_HOME or system PATH. |

## GPSL Language Examples

### Basic Temporal Logic

```gpsl
// Define propositions
p = request;
q = grant;

// Temporal properties
safety = always (p -> eventually q);
liveness = always eventually p;
mutual_exclusion = always !(p && q);
```

### Let Expressions

```gpsl
formula = let x = true, y = false in x || y
```

### Büchi Automaton

```gpsl
automaton = buchi
    states s0, s1;
    initial s0;
    accept s1;
    s0 [p] s1;
    s1 [q] s0
```

### Atoms

```gpsl
// Pipe-delimited atoms
atom1 = |my atom|
atom2 = |complex/path/atom|

// Quoted atoms  
atom3 = "another atom"
```

## Troubleshooting

### "Could not find Java executable"

Make sure Java 17 or higher is installed and either:
- Set the `gpsl.javaHome` setting to point to your Java installation directory, or
- Set the `JAVA_HOME` environment variable, or
- Add Java to your system `PATH`

### Language Server Not Starting

1. Check the Output panel in VS Code (View → Output)
2. Select "GPSL Language Server" from the dropdown
3. Look for error messages that might indicate what went wrong

### Extension Not Activating

The extension only activates when you open a `.gpsl` file. Create or open a file with the `.gpsl` extension to trigger activation.

## Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/plug-obp/gpsl-java.git
cd gpsl-java

# Build the Java components
./gradlew build

# Build and package the extension
cd gpsl-lsp-vscode
npm install
npm run bundle-server  # Bundles the LSP server
npm run compile        # Compiles TypeScript
npm run package        # Creates .vsix file
```

The `.vsix` file can be installed in VS Code using "Install from VSIX...".

### Testing During Development

1. Build the LSP server:
   ```bash
   ./gradlew :gpsl-lsp:installDist
   ```

2. Compile the extension:
   ```bash
   cd gpsl-lsp-vscode
   npm install
   npm run compile
   ```

3. In VS Code, run the "Launch Extension" configuration (F5) to open an Extension Development Host

4. Open a `.gpsl` file to test the language features

## Contributing

Contributions are welcome! Please see the [main repository](https://github.com/plug-obp/gpsl-java) for contribution guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/plug-obp/gpsl-java/blob/main/LICENSE) file for details.

## Links

- [GitHub Repository](https://github.com/plug-obp/gpsl-java)
- [Issue Tracker](https://github.com/plug-obp/gpsl-java/issues)
- [Release Notes](https://github.com/plug-obp/gpsl-java/releases)


