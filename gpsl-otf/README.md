# GPSL OtF

On the Fly support for GPSL.

## Overview

This module aims at providing an **on the fly** (read: syntactic interpretation, not automata generation) 
execution engine for GPSL. Technically, it consists of the following components:

- **Hashconsing Factory**: a factory for creating immutable GPSL expressions from their string representation.

## Features

### Language Support

## Dependencies

- ANTLR 4.13.1 (parser generation)
- OBP3 Core (reader infrastructure)
- JUnit 5 (testing)

## Building

```bash
# From repository root
./gradlew :gpsl-otf:build

# Run tests
./gradlew :gpsl-otf:test
```

## Usage Example



## Architecture

## Testing

## See Also

- [Main README](../README.md) - Project overview
- [GPSL Syntax Reference](../gpsl-lsp-vscode/docs/SYNTAX_REFERENCE.md) - Language syntax guide
