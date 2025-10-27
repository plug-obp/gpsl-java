# GPSL LTL3BA

LTL to Büchi Automata converter for the GPSL project.

## Overview

This module provides conversion from Linear Temporal Logic (LTL) formulas to Büchi automata using the LTL3BA tool.

## Features

- Converts GPSL temporal logic expressions to Büchi automata
- Wraps the native LTL3BA executable
- Platform-specific binaries included:
  - Linux x64
  - macOS x64  
  - Windows x64
- Automatic platform detection and binary selection

## LTL3BA Tool

[LTL3BA](https://sourceforge.net/projects/ltl3ba/) is a fast translator from LTL formulas to Büchi automata. This module packages the native executables and provides a Java interface.

## Building

```bash
# From repository root
./gradlew :gpsl-ltl3ba:build

# Run tests
./gradlew :gpsl-ltl3ba:test
```

## Usage Example

```java
import gpsl.ltl3ba.Expression2Automaton;

// Convert LTL expression to Büchi automaton
String ltlFormula = "G(p -> F q)";  // Always: p implies eventually q
String automaton = Expression2Automaton.convert(ltlFormula);
```

## Architecture

- `gpsl.ltl3ba` - Java wrapper for LTL3BA tool
- `native-binaries/ltl3ba/` - Platform-specific executables

## Testing

Tests cover:
- Conversion of various LTL formulas
- Platform detection
- Error handling

## Dependencies

- JUnit 5 (testing)
- Native LTL3BA binaries (included)

## See Also

- [Main README](../README.md) - Project overview
- [LTL3BA Project](https://sourceforge.net/projects/ltl3ba/) - Original tool
