# GPSL VS Code Extension

Language support for GPSL (General Purpose Specification Language) - a temporal logic specification language with LSP integration.

## Features

### Syntax Highlighting

Full syntax highlighting for all GPSL constructs:

- **Propositional Logic Operators**
  - Conjunction: `and`, `&`, `&&`, `/\`, `*`, `∧`
  - Disjunction: `or`, `|`, `||`, `\/`, `+`, `∨`
  - Negation: `not`, `!`, `~`
  - Implication: `implies`, `->`, `=>`, `→`, `⟹`
  - Equivalence: `iff`, `<->`, `<=>`, `⟺`, `↔`
  - XOR: `xor`, `^`, `⊻`, `⊕`

- **Temporal Logic Operators**
  - Next: `next`, `N`, `X`, `o`, `()`, `◯`
  - Eventually: `eventually`, `F`, `<>`, `♢`
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

- Error diagnostics with position information
- Symbol resolution
- Code completion (planned)
- Go to definition (planned)
- Hover information (planned)

### Editor Features

- Auto-closing pairs for `()`, `[]`, `{}`, `||`, `""`
- Comment toggling
- Bracket matching
- Smart indentation

## Example

See `examples/sample.gpsl` for comprehensive examples of all language features.

```gpsl
// Simple propositional formula
safety = p and q implies r

// Temporal logic
liveness = [] (request -> (<> grant))

// Let binding
formula = let x = true, y = false in x or y

// Büchi automaton
automaton = buchi
    states s0, s1;
    initial s0;
    accept s1;
    s0 [p] s1;
    s1 [q] s0
```

## Prerequisites

- Java 21+ (repo uses toolchains 23)
- VS Code 1.90.0 or higher
- Node.js and pnpm

## Setup

1. Build the LSP server:

```bash
./gradlew :gpsl-lsp:installDist
```

This creates the runnable script at:
- macOS/Linux: `gpsl-lsp/build/install/gpsl-lsp/bin/gpsl-lsp`
- Windows: `gpsl-lsp\\build\\install\\gpsl-lsp\\bin\\gpsl-lsp.bat`

2. Install extension dependencies:

```bash
cd gpsl-lsp-vscode
pnpm install
pnpm run compile
```

## Development

In VS Code, run the "Launch Extension" configuration (F5) to open an Extension Development Host.

Open a folder with `.gpsl` files to see:
- Syntax highlighting
- Error diagnostics from the LSP server
- Auto-completion

The extension launches the server script on stdio and communicates via LSP.

## License

See LICENSE file in the root of the repository.

