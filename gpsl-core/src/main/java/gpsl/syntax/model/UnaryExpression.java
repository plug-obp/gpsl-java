package gpsl.syntax.model;

/**
 * Base interface for unary expressions.
 */
public sealed interface UnaryExpression extends Expression
    permits Negation, Next, Eventually, Globally {
    
    String operator();
    Expression expression();
    
    @Override
    default <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
