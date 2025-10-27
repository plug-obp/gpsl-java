# Changelog

All notable changes to the GPSL VS Code extension will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial release of GPSL Language Support extension
- Full syntax highlighting for GPSL specifications
- Language Server Protocol integration with advanced features:
  - Go to Definition
  - Find References
  - Code Completion
  - Real-time diagnostics
  - Hover information
  - Document symbols
- Configurable Java home path setting
- Automatic Java detection (user setting → JAVA_HOME → system PATH)
- Auto-closing pairs for brackets and quotes
- Smart indentation
- Comment toggling support

### Changed
- Single `|` operator removed from disjunction (breaking change)
  - Use `||` or `or` for logical disjunction
  - Single `|` is now reserved exclusively for atom delimiters
- Added support for both diamond Unicode characters (◇ U+25C7 and ♢ U+2662)

## [0.1.0] - TBD

### Added
- First public release
- Basic GPSL language support
- LSP integration for advanced IDE features
- Comprehensive documentation

[Unreleased]: https://github.com/plug-obp/gpsl-java/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/plug-obp/gpsl-java/releases/tag/v0.1.0
