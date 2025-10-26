# Recommended Theme Settings for GPSL

To get optimal syntax highlighting for GPSL, you can customize your VS Code color theme.

## Option 1: Use Your Favorite Theme

The GPSL syntax highlighting uses standard TextMate scopes that work with most popular themes:
- Dark+ (default dark)
- Light+ (default light)
- Monokai
- Solarized Dark/Light
- One Dark Pro
- GitHub Theme

## Option 2: Customize Colors

Add these to your `settings.json` to customize GPSL highlighting:

```json
{
  "editor.tokenColorCustomizations": {
    "textMateRules": [
      {
        "scope": "keyword.control.let.inline.gpsl",
        "settings": {
          "foreground": "#C586C0",
          "fontStyle": "italic"
        }
      },
      {
        "scope": "keyword.control.let.multiline.gpsl",
        "settings": {
          "foreground": "#C586C0",
          "fontStyle": "bold"
        }
      },
      {
        "scope": "keyword.control.in.inline.gpsl",
        "settings": {
          "foreground": "#C586C0",
          "fontStyle": "italic"
        }
      },
      {
        "scope": "keyword.control.in.multiline.gpsl",
        "settings": {
          "foreground": "#C586C0",
          "fontStyle": "bold"
        }
      },
      {
        "scope": "keyword.operator.temporal.unary.gpsl",
        "settings": {
          "foreground": "#569CD6",
          "fontStyle": "italic"
        }
      },
      {
        "scope": "keyword.operator.temporal.binary.gpsl",
        "settings": {
          "foreground": "#4EC9B0",
          "fontStyle": "italic"
        }
      },
      {
        "scope": "keyword.operator.logical.conjunction.gpsl",
        "settings": {
          "foreground": "#CE9178"
        }
      },
      {
        "scope": "keyword.operator.logical.disjunction.gpsl",
        "settings": {
          "foreground": "#CE9178"
        }
      },
      {
        "scope": "keyword.operator.logical.negation.gpsl",
        "settings": {
          "foreground": "#D16969",
          "fontStyle": "bold"
        }
      },
      {
        "scope": "keyword.operator.logical.implication.gpsl",
        "settings": {
          "foreground": "#4FC1FF"
        }
      },
      {
        "scope": "keyword.control.automaton.type.gpsl",
        "settings": {
          "foreground": "#C586C0",
          "fontStyle": "bold"
        }
      },
      {
        "scope": "keyword.control.automaton.gpsl",
        "settings": {
          "foreground": "#569CD6"
        }
      },
      {
        "scope": "entity.name.function.gpsl",
        "settings": {
          "foreground": "#DCDCAA",
          "fontStyle": "bold"
        }
      },
      {
        "scope": "string.quoted.pipe.gpsl",
        "settings": {
          "foreground": "#D7BA7D"
        }
      },
      {
        "scope": "constant.language.boolean.gpsl",
        "settings": {
          "foreground": "#569CD6",
          "fontStyle": "bold"
        }
      }
    ]
  }
}
```

## Color Meanings (with default customization)

- **Purple/Magenta** (`#C586C0`): Control keywords
  - Inline `let`/`in`: Italic style
  - Multiline `let`/`in`: Bold style (for better visibility in structured code)
  - Automaton keywords: `nfa`, `buchi`
- **Blue** (`#569CD6`): Temporal operators, automaton keywords, booleans
- **Cyan** (`#4EC9B0`): Binary temporal operators
- **Orange** (`#CE9178`): Logical operators (and, or)
- **Red** (`#D16969`): Negation operator
- **Light Blue** (`#4FC1FF`): Implication operator
- **Yellow** (`#DCDCAA`): Declaration names
- **Gold** (`#D7BA7D`): Atoms

## Semantic Highlighting

When the LSP server is running, you'll also get semantic highlighting which can highlight:
- Undefined symbols (errors)
- References vs declarations
- Scoped variables in let expressions

This works in addition to syntax highlighting and is controlled by:

```json
{
  "editor.semanticHighlighting.enabled": true
}
```

## Font Recommendations

For Unicode operators (∧, ∨, →, ⟹, ◯, ☐, ♢, etc.) to display correctly:

1. Use a font that supports these symbols:
   - Fira Code
   - JetBrains Mono
   - Cascadia Code
   - Iosevka

2. Enable font ligatures (optional):
   ```json
   {
     "editor.fontFamily": "Fira Code, Menlo, Monaco, 'Courier New', monospace",
     "editor.fontLigatures": true
   }
   ```

## Testing Your Theme

Open `examples/sample.gpsl` to see all syntax highlighting features:
- All operators should have distinct colors
- Keywords should stand out
- Atoms should be visually distinct from other strings
- Comments should be dimmed
- Declaration names should be highlighted
