# Changelog

All notable changes to the GPSL VS Code extension.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

## [0.1.0] - TBD

First public release.

[1.0.1]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.1
[1.0.0]: https://github.com/plug-obp/gpsl-java/releases/tag/v1.0.0
