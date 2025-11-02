# Changelog

All notable changes to the GPSL project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-11-02

### Added

#### New Module: gpsl-modelchecker
- **Model Checker Integration**: New `StepModelChecker` class for verifying temporal properties against operational models
- Integrates with OBP3 model checking infrastructure
- Supports Büchi emptiness checking with configurable algorithms (GS09_CDLP05_SEPARATED)
- Configurable depth-first traversal strategies
- Depth-bounded model checking option

#### Language Enhancements
- **Unicode Negation Support**: Added support for Unicode negation symbol `¬` (U+00AC) as an alternative to `!`, `~`, and `not`
- **Propositional to NFA Conversion**: New `PropositionalToNFA` utility for converting propositional formulas to NFAs
- **Propositional Formula Detection**: New `IsPropositional` visitor for determining if expressions are purely propositional

#### Examples and Documentation
- **Peterson's Algorithm Example**: Added comprehensive `petterson.gpsl` example demonstrating mutual exclusion properties
  - Exclusion property
  - Deadlock freedom
  - Livelock freedom
  - Recurrence properties
  - Liveness guarantees
- Updated syntax reference with Unicode negation operator

### Changed
- **Package Reorganization**: Moved `Expression2BuchiAutomaton` from `gpsl.ltl3ba` to `gpsl.toBuchi` package
- **Module System**: Added `module-info.java` to gpsl-core and gpsl-ltl3ba for JPMS compatibility
- **Version Metadata**: Git hash now included only as build metadata, local builds tagged with `.local` suffix
- **Symbol Resolution**: Fixed to use last declaration instead of first when multiple declarations exist
- **Test Suite**: Expanded to 324 tests (up from 322)

### Fixed
- **NFA Extraction**: Improved NFA extraction logic to properly handle Büchi automata
- **Automaton Detection**: Enhanced logic to check for both NFA and Büchi automata presence
- **Peterson Properties**: Corrected property specifications in test cases
- **Parentheses**: Removed extraneous parentheses in generated code

### Technical
- All modules remain compatible with Java 23
- Gradle wrapper 9.1.0
- Dependencies updated to latest OBP3 runtime (1.0.+) and algorithms (1.0.+)

## [1.0.3] - 2025-10-27

### Fixed
- VS Code extension release workflow VSIX file path resolution
- Replaced deprecated `set-output` commands with environment files in GitHub Actions

## [1.0.2] - 2025-01-29

### Fixed
- VS Code extension release workflow now properly handles version extraction from tags
- Workflow supports both tag-based and manual builds

## [1.0.1] - 2025-01-29

### Changed
- **Java Requirement**: Updated minimum Java version from 17 to 23
- **Gradle Version**: Updated documentation to reflect Gradle 9.1 wrapper
- **Priority Semantics**: Fixed and clarified transition priority ordering (0 > 1 > 2 > 3)

### Fixed
- Transition priority sorting now correctly uses ascending order (lower number = higher priority)
- All documentation and examples updated for consistent priority semantics

## [1.0.0] - 2025-01-29

### Added

#### Language Features
- **Document Symbols**: LSP outline view showing declarations, let bindings, and automaton components
- **Multiline Atom Support**: Atoms can now span multiple lines with proper syntax highlighting and position tracking
- **Enhanced Error Reporting**: Improved error messages with precise position information
- **Position Tracking**: Complete position mapping for all AST nodes supporting IDE features

#### Developer Tools
- **VS Code Extension**: Full-featured extension with syntax highlighting, LSP integration, and code snippets
  - Real-time diagnostics and error checking
  - Go to Definition (Ctrl+Click or F12)
  - Document Symbols / Outline view (Ctrl+Shift+O)
  - Auto-closing pairs and smart indentation
  - Code snippets for common patterns
- **Language Server Protocol**: Eclipse LSP4J-based implementation
- **CI/CD Pipeline**: GitHub Actions workflow for automated builds and releases
- **Comprehensive Test Suite**: 322 tests across all components

#### Grammar Improvements
- Fixed ANTLR grammar to support multiline atoms using character sets (`~[|]` instead of `~'|'`)
- Improved position tracking for multiline tokens
- Better error recovery and reporting

### Changed

- **BREAKING**: Removed single `|` from disjunction operators
  - Single `|` is now reserved exclusively for atom delimiters
  - Use `||` or `or` for logical disjunction
- Updated diamond Unicode support to include both ◇ (U+25C7) and ♢ (U+2662)
- Reorganized documentation into `docs/` folder for better structure
- Enhanced test coverage with dedicated test helpers and utilities

### Fixed

- Multiline atom position tracking now correctly calculates end line and column
- Nested let expressions now properly show hierarchical structure in outline
- Selection ranges in document symbols now point to declaration names only
- ANTLR lexer properly handles newlines in pipe and quote atoms

## [0.1.0] - 2025-10-27

### Added

- Initial release of GPSL language components
- Core parser with ANTLR4 grammar
- LTL to Büchi automata conversion (ltl3ba integration)
- Basic language server implementation
- Support for:
  - Linear Temporal Logic operators
  - Propositional logic operators
  - Büchi and NFA automata specifications
  - Let expressions with scoped bindings
  - Pipe-delimited and quoted atoms

### Infrastructure

- Gradle multi-project build system
- JUnit 5 test framework
- GitHub Actions CI/CD
- Maven package publishing

## Component-Specific Changes

See also:
- [gpsl-lsp-vscode/CHANGELOG.md](gpsl-lsp-vscode/CHANGELOG.md) - VS Code extension changes
- Individual module build.gradle files for dependency versions

[1.0.2]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.2
[1.0.1]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.1
[1.0.0]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.0
