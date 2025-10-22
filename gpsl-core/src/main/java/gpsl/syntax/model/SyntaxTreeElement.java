package gpsl.syntax.model;

/**
 * Base interface for all GPSL syntax tree elements.
 * Uses the Visitor pattern for traversal.
 */
public sealed interface SyntaxTreeElement 
    permits Declarations, Expression, ExpressionDeclaration, 
            State, Transition, Automaton {
    
    <T, R> R accept(Visitor<T, R> visitor, T input);
}
