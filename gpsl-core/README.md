# GPSL Core

Core components of the GPSL (Generic Property Specification Language) implementation.

## Overview

This module provides the fundamental language infrastructure for GPSL:

- **ANTLR4 Parser**: Complete grammar definition and parser for GPSL syntax
- **Abstract Syntax Tree (AST)**: Typed representations of all GPSL constructs
- **Semantic Analysis**: Symbol resolution, type checking, and validation
- **Position Tracking**: Precise source location mapping for error reporting and IDE features
- **Error Reporting**: Comprehensive error messages with context

## Features

### Language Support

- **Temporal Logic**: LTL operators (next, eventually, globally, until, release)
- **Propositional Logic**: Boolean operators with multiple syntax variants
- **Conditional Operator**: Ternary conditional (`condition ? trueBranch : falseBranch`)
- **Automata**: Büchi and NFA specifications with optional priorities
- **Let Expressions**: Scoped variable bindings
- **Atoms**: Pipe-delimited (`|...|`) and quoted (`"..."`) with escape sequences
- **Comments**: Line (`//`) and block (`/* */`)

### Developer Features

- Position-aware AST nodes for all constructs
- Visitor pattern for AST traversal
- Symbol table with scope management
- Multiline token support
- Robust error recovery

## Dependencies

- ANTLR 4.13.1 (parser generation)
- OBP3 Core (reader infrastructure)
- JUnit 5 (testing)

## Building

```bash
# From repository root
./gradlew :gpsl-core:build

# Run tests
./gradlew :gpsl-core:test

# Generate parser from grammar
./gradlew :gpsl-core:generateGrammarSource
```

## Usage Example

```java
import gpsl.syntax.Reader;
import gpsl.syntax.ParseResultWithPositions;

// Parse GPSL source
String source = "safety = always (p -> eventually q);";
ParseResultWithPositions result = Reader.read(source);

if (result.errors().isEmpty()) {
    // Access parsed declarations
    result.declarations().declarations().forEach(decl -> {
        System.out.println("Found: " + decl.name());
    });
}
```

## Architecture

- `gpsl.parser` - ANTLR grammar and generated parser
- `gpsl.syntax` - AST classes and parsing infrastructure
- `gpsl.semantics` - Semantic analysis and evaluation

## Testing

Over 200 tests covering:
- Parser correctness for all language constructs
- Error reporting and recovery
- Multiline atoms and position tracking
- Symbol resolution
- Edge cases and robustness

## See Also

- [Main README](../README.md) - Project overview
- [GPSL Syntax Reference](../gpsl-lsp-vscode/docs/SYNTAX_REFERENCE.md) - Language syntax guide
