# GPSL v1.1.0 Release Notes

**Release Date**: November 2, 2025

## Overview

GPSL v1.1.0 introduces a major new feature: **model checking integration** through the new `gpsl-modelchecker` module. This release enables verification of temporal properties against operational models, making GPSL a complete property specification and verification toolkit.

## 🎉 New Features

### Model Checker Module

The new `gpsl-modelchecker` module provides integration with the OBP3 model checking infrastructure:

- **`StepModelChecker<MA, MC>`**: Generic model checker for verifying GPSL properties against step-based operational models
- **Flexible Configuration**: 
  - Multiple Büchi emptiness checking algorithms (default: GS09_CDLP05_SEPARATED)
  - Configurable depth-first traversal strategies
  - Optional depth-bounded model checking
- **Seamless Integration**: Works with any OBP3 `SemanticRelation` and custom atom evaluators

**Example Usage**:
```java
var checker = new StepModelChecker<>(
    modelSemantics,           // Your operational model
    atomEvaluator,           // Evaluates atomic propositions
    "safety = []!|critical|" // GPSL property
);
var result = checker.modelChecker().execute();
```

### Language Enhancements

- **Unicode Negation**: Added support for `¬` (U+00AC) as a negation operator
  - Can now write: `property = ¬|enabled| ∨ ◇|done|`
  - Joins existing alternatives: `!`, `~`, `not`

- **Propositional Formula Utilities**:
  - `IsPropositional`: Visitor to detect purely propositional expressions
  - `PropositionalToNFA`: Converter from propositional formulas to NFAs

### Examples

- **Peterson's Algorithm** (`petterson.gpsl`): Comprehensive example demonstrating:
  - Mutual exclusion
  - Deadlock freedom
  - Livelock freedom
  - Recurrence properties
  - Liveness guarantees

## 🔧 Improvements

### Architecture

- **Package Reorganization**: Moved `Expression2BuchiAutomaton` to `gpsl.toBuchi` package for better modularity
- **JPMS Support**: Added `module-info.java` to core modules for Java Platform Module System compatibility
- **Version Metadata**: Git hash included as build metadata; local builds tagged with `.local`

### Quality

- **Test Suite Expansion**: Now 324 tests (up from 322)
- **Better NFA Extraction**: Improved logic for handling Büchi automata
- **Symbol Resolution**: Fixed to use last declaration when duplicates exist
- **Code Cleanup**: Removed extraneous parentheses and improved code organization

## 📦 Modules

GPSL now consists of five modules:

1. **gpsl-core**: Parser, AST, and semantic analysis
2. **gpsl-ltl3ba**: LTL to Büchi automata conversion
3. **gpsl-lsp**: Language Server Protocol implementation
4. **gpsl-lsp-vscode**: VS Code extension
5. **gpsl-modelchecker**: ⭐ NEW - Model checking integration

## 📊 Statistics

- **Total Tests**: 324 (all passing ✅)
- **Lines of Code**: 615 additions, 172 deletions
- **Files Changed**: 32
- **Java Version**: 23+
- **Gradle**: 9.1.0

## 🔄 Breaking Changes

None. This release is fully backward compatible with v1.0.x.

## 📥 Installation

### Java Library

Add to your Gradle build:

```gradle
dependencies {
    implementation 'org.obpcdl:gpsl-core:1.1.0'
    implementation 'org.obpcdl:gpsl-modelchecker:1.1.0' // For model checking
}
```

### VS Code Extension

1. Download `gpsl-lsp-vscode-1.1.0.vsix` from [GitHub Releases](https://github.com/plug-obp/gpsl-java/releases/tag/v1.1.0)
2. In VS Code: `Extensions: Install from VSIX...` (Ctrl+Shift+P / Cmd+Shift+P)
3. Select the downloaded file

## 🔗 Links

- [GitHub Repository](https://github.com/plug-obp/gpsl-java)
- [Full Changelog](https://github.com/plug-obp/gpsl-java/blob/main/CHANGELOG.md)
- [Documentation](https://github.com/plug-obp/gpsl-java/blob/main/gpsl-lsp-vscode/docs/SYNTAX_REFERENCE.md)
- [Examples](https://github.com/plug-obp/gpsl-java/tree/main/gpsl-lsp-vscode/examples)

## 🙏 Contributors

Thank you to all contributors who made this release possible!

## 🐛 Known Issues

None at this time. Please report issues on [GitHub Issues](https://github.com/plug-obp/gpsl-java/issues).

## 🚀 What's Next

Future releases will focus on:
- Performance optimizations for model checking
- Additional example specifications
- Enhanced IDE features (code completion, hover information)
- Documentation and tutorials

---

**Full Changelog**: [v1.0.3...v1.1.0](https://github.com/plug-obp/gpsl-java/compare/v1.0.3...v1.1.0)
