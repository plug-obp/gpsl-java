# VS Code Extension Syntax Highlighting Implementation

## Summary

Implemented comprehensive syntax highlighting for the GPSL language based on the ANTLR grammar (`GPSL.g4`) and language semantics.

## What Was Added

### 1. TextMate Grammar (`syntaxes/gpsl.tmLanguage.json`)

A complete TextMate grammar providing syntax highlighting for:

- **Keywords**:
  - Control flow: `let`, `in`
  - Automaton types: `nfa`, `buchi`
  - Automaton sections: `states`, `initial`, `accept`

- **Temporal Operators**:
  - Unary: `next`, `N`, `X`, `o`, `()`, `◯`, `globally`, `always`, `G`, `[]`, `☐`, `eventually`, `F`, `<>`, `♢`
  - Binary: `until`, `U`, `SU`, `strong-until`, `W`, `WU`, `weak-until`, `M`, `SR`, `strong-release`, `R`, `WR`, `weak-release`

- **Logical Operators**:
  - Negation: `not`, `!`, `~`
  - Conjunction: `and`, `&`, `&&`, `/\`, `*`, `∧`
  - Disjunction: `or`, `|`, `||`, `\/`, `+`, `∨`
  - XOR: `xor`, `^`, `⊻`, `⊕`
  - Implication: `implies`, `->`, `=>`, `→`, `⟹`
  - Equivalence: `iff`, `<->`, `<=>`, `⟺`, `↔`

- **Literals**: `true`, `false`, `0`, `1`

- **Atoms**: Pipe-delimited `|...|` and quoted `"..."` with escape sequences

- **Comments**: Line (`//`) and block (`/* */`)

- **Declarations**: Named formulas with `=` (internal) and `*=` (external) operators

- **Numbers**: Natural numbers for priorities

- **Identifiers**: Variable references

### 2. Language Configuration (`language-configuration.json`)

Enhanced with:
- Auto-closing pairs for `()`, `[]`, `{}`, `||`, `""`
- Surrounding pairs
- Word pattern for identifiers
- Indentation rules for `let`/`in` blocks

### 3. Code Snippets (`snippets/gpsl.json`)

Predefined snippets for common patterns:
- Let expressions (single and multiple bindings)
- Büchi and NFA automata templates
- Automata with priorities
- Temporal patterns (always eventually, eventually always)
- Safety and liveness properties
- Common operators (until, weak until)
- Atoms and declarations
- Comment headers

### 4. Example File (`examples/sample.gpsl`)

Comprehensive example showcasing:
- All operator variants (ASCII, Unicode, symbolic)
- Boolean literals
- Atoms with escaping
- Let bindings (simple, multiple, nested)
- Temporal formulas
- Automata (NFA, Büchi, with priorities)
- Complex real-world patterns

### 5. Documentation

- **README.md**: Updated with feature list, language overview, and setup instructions
- **SYNTAX_REFERENCE.md**: Quick reference guide with all operators, patterns, and examples

### 6. Package Metadata (`package.json`)

Added:
- Grammar registration
- Snippet registration
- Keywords for marketplace discovery
- Repository information
- Improved description

## Language Features Highlighted

Based on the GPSL.g4 grammar, the highlighting correctly identifies:

1. **Propositional Logic**: All standard boolean operators with multiple syntactic variants
2. **Linear Temporal Logic (LTL)**: Standard LTL operators (next, globally, eventually, until, release)
3. **Let Bindings**: Local variable declarations with scope
4. **Automata**: 
   - NFA (Nondeterministic Finite Automaton)
   - Büchi automata (default if no prefix)
   - State declarations, initial/accept states
   - Transitions with optional priorities
   - Guard expressions on transitions

## Scopes Used

The grammar uses semantic scopes that work with most VS Code themes:

- `keyword.control.*` - Language keywords
- `keyword.operator.*` - Operators (subdivided by type)
- `entity.name.function` - Declaration names
- `constant.language.boolean` - Boolean literals
- `constant.numeric` - Numbers
- `string.quoted.*` - Atoms
- `variable.other` - Identifiers
- `comment.*` - Comments
- `punctuation.*` - Delimiters

## Testing

The syntax highlighting can be tested by:
1. Opening `examples/sample.gpsl` in VS Code
2. Verifying colors match operator types
3. Testing auto-completion with snippets (type prefix + Tab)
4. Verifying auto-closing pairs work correctly

## Integration with LSP

The syntax highlighting works independently but complements the LSP server:
- Syntax highlighting provides immediate visual feedback
- LSP provides semantic highlighting and error diagnostics
- Together they provide a complete IDE experience

## Technical Challenges Resolved

### Inline vs Multiline Let Expressions

GPSL supports both compact inline and structured multiline `let` expressions:
- **Inline**: `let x = true in x and y` (all on one line)
- **Multiline**: 
  ```gpsl
  let
      x = true,
      y = false
  in x and y
  ```

**Solution**:
- Two separate patterns with lookahead to detect style
- Multiline pattern: `\\b(let|\\\\)\\b(?=\\s*$)` - matches `let` followed by end of line
- Inline pattern: `\\b(let|\\\\)\\b` - matches `let` on same line as bindings
- Different scopes for different styling:
  - `keyword.control.let.multiline.gpsl` - Bold style for structural visibility
  - `keyword.control.let.inline.gpsl` - Italic style for compact expressions

This allows themes to distinguish between:
- Compact single-line expressions (italic, subtle)
- Multi-line structured blocks (bold, prominent)

### Atom vs Disjunction Operator Ambiguity

The pipe character `|` serves dual purposes in GPSL:
1. As a disjunction operator: `p | q`
2. As atom delimiters: `|{Alice}@CS|`

**Solution**: 
- Placed atoms pattern before operators in the matching order
- Used negative lookahead `\\|(?![|\\s])` in the atom begin pattern
  - Matches `|` only if NOT followed by another `|` (which would be `||`)
  - Matches `|` only if NOT followed by whitespace (which would be operator `|`)
  
This ensures:
- `|{Alice}@CS|` → matched as atom (no whitespace after opening `|`)
- `p | q` → single `|` with spaces matches as disjunction operator
- `p || q` → double `||` matches as disjunction operator
- `|atom1| | |atom2|` → two atoms with operator between them
