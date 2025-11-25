# Changelog

All notable changes to the GPSL VS Code extension.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **Conditional Operator**: Support for ternary conditional expressions `condition ? trueBranch : falseBranch`
  - Syntax highlighting for `?` and `:` operators
  - Code snippets: `cond` (simple) and `condn` (nested)
  - Comprehensive example file: `examples/sampleConditional.gpsl`
  - Documentation in SYNTAX_REFERENCE.md with examples
  - Right-associative parsing: `a ? b : c ? d : e` parses as `a ? b : (c ? d : e)`

## [1.1.0] - 2025-11-02

### Added

- **Unicode Negation Support**: Syntax highlighting for Unicode negation symbol `¬` (U+00AC)
- **Peterson's Algorithm Example**: Added comprehensive example file demonstrating mutual exclusion properties
- **Documentation Updates**: Corrected installation instructions to reflect GitHub Releases distribution

### Changed

- **Installation Method**: Updated documentation to clarify extension is distributed via GitHub Releases, not VS Code Marketplace
- **Symbol Resolution**: Improved language server to use last declaration when multiple exist

### Fixed

- **Syntax Highlighting**: Updated TextMate grammar to recognize Unicode negation operator

## [1.0.3] - 2025-10-27

### Fixed

- Release workflow VSIX file path resolution
- Replaced deprecated GitHub Actions `set-output` commands with environment files

## [1.0.2] - 2025-01-29

### Fixed

- Release workflow now properly handles version extraction from tags

## [1.0.1] - 2025-01-29

### Changed

- Updated minimum Java requirement from 17 to 23
- Updated all documentation and error messages to reflect Java 23 requirement

## [1.0.0] - 2025-01-29

### Added

- Full syntax highlighting for GPSL specifications
- Language Server Protocol (LSP) integration:
  - Real-time diagnostics and error checking
  - Go to Definition (Ctrl+Click or F12)
  - Document Symbols / Outline view (Ctrl+Shift+O)
- Configurable Java path detection (`gpsl.javaHome` setting)
- Automatic Java detection (setting → JAVA_HOME → system PATH)
- Auto-closing pairs for brackets, quotes, and pipes
- Smart indentation and comment toggling
- Code snippets for common GPSL patterns
- Comprehensive documentation with troubleshooting guide

### Changed

- **BREAKING**: Removed single `|` from disjunction operators
  - Single `|` is now reserved exclusively for atom delimiters: `|atom|`
  - Use `||` or `or` for logical disjunction instead
- Added support for both diamond Unicode characters (◇ U+25C7 and ♢ U+2662)

### Planned (Future Releases)

- Find References
- Code Completion
- Hover Information

[1.0.2]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.2
[1.0.1]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.1
[1.0.0]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.0
