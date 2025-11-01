# GPSL Syntax Quick Reference

## Comments
```gpsl
// Line comment
/* Block comment */
```

## Declarations
```gpsl
internal = formula          // Internal declaration
external *= formula         // External declaration (exported)
```

## Boolean Literals
```gpsl
true, false, 1, 0
```

## Atoms
```gpsl
|{Alice}1@CS|              // Pipe-delimited atom
"simple atom"              // Quoted atom
|escaped \| pipe|          // Escaped pipe
"escaped \" quote"         // Escaped quote
|{Process}1@State
  with multiple lines
  and \| escaped pipes|.   // Multiline escaped pipe
```

## Propositional Operators

### Negation
```gpsl
not p, !p, ~p, ¬p
```

### Conjunction (AND)
```gpsl
p and q, p & q, p && q, p /\ q, p * q, p ∧ q
```

### Disjunction (OR)
```gpsl
p or q, p || q, p \/ q, p + q, p ∨ q
```

### Exclusive OR (XOR)
```gpsl
p xor q, p ^ q, p ⊻ q, p ⊕ q
```

### Implication
```gpsl
p implies q, p -> q, p => q, p → q, p ⟹ q
```

### Equivalence (IFF)
```gpsl
p iff q, p <-> q, p <=> q, p ⟺ q, p ↔ q
```

## Temporal Operators

### Next (○)
```gpsl
next p, N p, X p, o p, () p, ◯ p
```

### Eventually (◇) - Future
```gpsl
eventually p, F p, <> p, ♢ p
```

### Globally (□) - Always
```gpsl
globally p, always p, G p, [] p, ☐ p
```

### Strong Until (U)
```gpsl
p until q, p U q, p SU q, p strong-until q
```

### Weak Until (W)
```gpsl
p W q, p WU q, p weak-until q
```

### Strong Release (M)
```gpsl
p M q, p SR q, p strong-release q
```

### Weak Release (R)
```gpsl
p R q, p WR q, p weak-release q
```

## Common Patterns

### Safety (something bad never happens)
```gpsl
[] (critical1 -> !critical2)   // Mutual exclusion
[] (p -> q)                     // Always: p implies q
```

### Liveness (something good eventually happens)
```gpsl
[] (request -> (<> grant))      // Every request gets a grant
<> p                            // Eventually p
```

### Fairness (infinitely often)
```gpsl
[] (<> p)                       // Infinitely often p
```

### Stabilization
```gpsl
<> ([] p)                       // Eventually always p
```

## Let Expressions

### Single binding
```gpsl
let x = true in x and y
```

### Multiple bindings
```gpsl
let 
    x = p, 
    y = q,
    z = r
in x and y and z
```

### Nested let
```gpsl
let x = p in 
    let y = x and q in 
        y or r
```

## Automata

### Büchi Automaton (default)
```gpsl
myBuchi = states s0, s1;
    initial s0;
    accept s1;
    s0 [p] s1;
    s1 [q] s0
```

### Explicit Büchi
```gpsl
myBuchi = buchi
    states s0, s1;
    initial s0;
    accept s1;
    s0 [guard1] s1;
    s1 [guard2] s0
```

### NFA (Nondeterministic Finite Automaton)
```gpsl
myNFA = nfa
    states s0, s1, s2;
    initial s0;
    accept s2;
    s0 [true] s1;
    s1 [p and q] s2;
    s2 [false] s0
```

### Automaton with Priorities

Priorities determine which transitions are evaluated first. Lower numbers indicate higher priority (0 > 1 > 2 > 3...).

```gpsl
priorityAuto = states s0, s1;
    initial s0;
    accept s1;
    s0 1 [guard1] s1;     // Priority 1 (higher precedence, evaluated first)
    s0 5 [guard2] s1;     // Priority 5 (lower precedence, evaluated only if priority 1 fails)
    s1 10 [true] s0       // Priority 10 (lowest precedence)
```

### Automaton with Let
```gpsl
autoWithLet = let
    guard1 = p and q,
    guard2 = r or s
in states s0, s1;
    initial s0;
    accept s1;
    s0 [guard1] s1;
    s1 [guard2] s0
```

## Operator Precedence (highest to lowest)

1. Atoms, Literals, Identifiers, Parentheses
2. Unary operators: `not`, `next`, `eventually`, `globally`
3. Binary temporal: `until`, `weak-until`, `release`, `weak-release` (right-associative)
4. `and` (conjunction)
5. `or` (disjunction)
6. `xor` (exclusive or)
7. `implies`, `iff` (implication, equivalence) (right-associative)

## Examples

### Simple specification
```gpsl
safety = [] (critical1 -> !critical2)
liveness = [] (request -> (<> grant))
spec = safety and liveness
```

### With atoms
```gpsl
aliceCS = |{Alice}1@CS|
bobCS = |{Bob}1@CS|
mutexProperty = [] (aliceCS -> !bobCS)
```

### Complex formula
```gpsl
fairness *= let
    aliceFlagUp = |{sys}1:flags[0] = true|,
    bobFlagUp = |{sys}1:flags[1] = true|
in
    [] ((aliceFlagUp -> (<> aliceCS)) && (bobFlagUp -> (<> bobCS)))
```
