package gpsl.lsp;

import gpsl.syntax.PositionMap;
import gpsl.syntax.model.*;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;
import rege.reader.infra.Position;
import rege.reader.infra.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visitor that extracts document symbols for LSP outline view.
 * This visitor walks the GPSL syntax tree and creates a hierarchical symbol structure.
 */
public class DocumentSymbolVisitor implements Visitor<PositionMap, List<DocumentSymbol>> {

    @Override
    public List<DocumentSymbol> visitDeclarations(Declarations element, PositionMap positionMap) {
        List<DocumentSymbol> symbols = new ArrayList<>();
        for (ExpressionDeclaration decl : element.declarations()) {
            DocumentSymbol symbol = createSymbolForDeclaration(decl, positionMap);
            if (symbol != null) {
                symbols.add(symbol);
            }
        }
        return symbols;
    }

    @Override
    public List<DocumentSymbol> visitExpressionDeclaration(ExpressionDeclaration element, PositionMap positionMap) {
        DocumentSymbol symbol = createSymbolForDeclaration(element, positionMap);
        return symbol != null ? List.of(symbol) : Collections.emptyList();
    }

    /**
     * Creates a document symbol for an expression declaration.
     * Recursively extracts children for let expressions and automata.
     */
    private DocumentSymbol createSymbolForDeclaration(ExpressionDeclaration decl, PositionMap positionMap) {
        // Get the range for the entire declaration
        var declRange = positionMap.get(decl);
        if (declRange.isEmpty()) {
            return null; // Skip if no position info
        }

        // Create selection range for just the declaration name
        // The name appears at the start of the declaration, before the '='
        Range fullRange = declRange.get();
        Range nameRange = new Range(
            fullRange.start(),
            new Position(
                fullRange.start().line(),
                fullRange.start().column() + decl.name().length(),
                fullRange.start().offset() + decl.name().length()
            )
        );

        // Create the symbol
        String detail = !decl.isInternal() ? "external" : null;
        DocumentSymbol symbol = new DocumentSymbol(
            decl.name(),
            SymbolKind.Variable,
            toLspRange(fullRange),
            toLspRange(nameRange),
            detail
        );

        // Extract children from the expression (let bindings, automaton elements, etc.)
        ChildSymbolExtractor childExtractor = new ChildSymbolExtractor();
        List<DocumentSymbol> children;
        
        // The expression can be an Expression or an Automaton (via LetExpression)
        // Since Automaton is not an Expression, we need to handle it separately
        if (decl.expression() instanceof LetExpression let && 
            let.expression() instanceof Automaton automaton) {
            // Handle let expression with automaton body
            children = childExtractor.visitAutomaton(automaton, positionMap);
        } else {
            // Handle regular expressions (including LetExpression with Expression body)
            children = decl.expression().accept(childExtractor, positionMap);
        }
        
        if (!children.isEmpty()) {
            symbol.setChildren(children);
        }

        return symbol;
    }

    /**
     * Converts a Range to LSP Range.
     * Converts from 1-based (parser) to 0-based (LSP) line and column numbers.
     */
    private org.eclipse.lsp4j.Range toLspRange(Range range) {
        var start = range.start();
        var end = range.end();
        return new org.eclipse.lsp4j.Range(
            new org.eclipse.lsp4j.Position(Math.max(0, start.line() - 1), Math.max(0, start.column() - 1)),
            new org.eclipse.lsp4j.Position(Math.max(0, end.line() - 1), Math.max(0, end.column() - 1))
        );
    }

    /**
     * Inner visitor that extracts child symbols from expressions.
     * Used for let bindings and automaton components.
     */
    private class ChildSymbolExtractor implements Visitor<PositionMap, List<DocumentSymbol>> {

        @Override
        public List<DocumentSymbol> visitLetExpression(LetExpression element, PositionMap positionMap) {
            List<DocumentSymbol> children = new ArrayList<>();
            
            // Add each let binding as a child symbol
            for (ExpressionDeclaration binding : element.declarations().declarations()) {
                var bindingRange = positionMap.get(binding);
                if (bindingRange.isEmpty()) {
                    continue;
                }

                // Create selection range for just the binding name (like top-level declarations)
                Range fullRange = bindingRange.get();
                Range nameRange = new Range(
                    fullRange.start(),
                    new Position(
                        fullRange.start().line(),
                        fullRange.start().column() + binding.name().length(),
                        fullRange.start().offset() + binding.name().length()
                    )
                );

                DocumentSymbol bindingSymbol = new DocumentSymbol(
                    binding.name(),
                    SymbolKind.Variable,
                    toLspRange(fullRange),
                    toLspRange(nameRange)
                );
                
                // Recursively extract children if this binding's expression is a let
                if (binding.expression() instanceof LetExpression nestedLet) {
                    List<DocumentSymbol> nestedChildren = visitLetExpression(nestedLet, positionMap);
                    if (!nestedChildren.isEmpty()) {
                        bindingSymbol.setChildren(nestedChildren);
                    }
                }
                
                children.add(bindingSymbol);
            }
            
            // If the body of this let expression is another let expression,
            // create an "in" symbol to represent the nested context
            if (element.expression() instanceof LetExpression bodyLet) {
                var bodyRange = positionMap.get(bodyLet);
                if (bodyRange.isPresent()) {
                    Range fullRange = bodyRange.get();
                    
                    // Create an "in" symbol with the nested let's bindings as children
                    DocumentSymbol inSymbol = new DocumentSymbol(
                        "in",
                        SymbolKind.Namespace,
                        toLspRange(fullRange),
                        toLspRange(fullRange)  // Selection range same as full range
                    );
                    
                    // Get the nested let's children
                    List<DocumentSymbol> nestedChildren = visitLetExpression(bodyLet, positionMap);
                    if (!nestedChildren.isEmpty()) {
                        inSymbol.setChildren(nestedChildren);
                    }
                    
                    children.add(inSymbol);
                }
            }
            
            return children;
        }

        @Override
        public List<DocumentSymbol> visitAutomaton(Automaton element, PositionMap positionMap) {
            List<DocumentSymbol> children = new ArrayList<>();

            // Add transitions as children
            // Note: We don't add states as children because State objects are created 
            // during mapping and don't have positions in the position map
            for (Transition transition : element.transitions()) {
                var transRange = positionMap.get(transition);
                if (transRange.isEmpty()) {
                    continue;
                }

                // Format transition name as "source → target"
                String name = transition.source().name() + " → " + transition.target().name();
                
                // Add priority info if non-zero
                String detail = transition.priority() > 0 
                    ? "priority " + transition.priority() 
                    : null;

                DocumentSymbol transSymbol = new DocumentSymbol(
                    name,
                    SymbolKind.Method,
                    toLspRange(transRange.get()),
                    toLspRange(transRange.get()),
                    detail
                );
                children.add(transSymbol);
            }

            return children;
        }

        // Default implementation: no children for other expression types
        @Override
        public List<DocumentSymbol> visitExpression(Expression element, PositionMap positionMap) {
            return Collections.emptyList();
        }

        @Override
        public List<DocumentSymbol> visitBinaryExpression(BinaryExpression element, PositionMap positionMap) {
            return Collections.emptyList();
        }

        @Override
        public List<DocumentSymbol> visitUnaryExpression(UnaryExpression element, PositionMap positionMap) {
            return Collections.emptyList();
        }
    }
}
