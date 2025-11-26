package gpsl.syntax.model;

/**
 * Represents the temporal Strong Until operator (U).
 */
public record StrongUntil(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
