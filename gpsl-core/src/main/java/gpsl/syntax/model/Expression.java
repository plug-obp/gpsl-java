package gpsl.syntax.model;

/**
 * Base interface for all GPSL expressions.
 */
public sealed interface Expression extends SyntaxTreeElement 
    permits True, False, Atom, Reference,
            UnaryExpression, BinaryExpression, Conditional, LetExpression {
    
    @Override
    <T, R> R accept(Visitor<T, R> visitor, T input);
}
