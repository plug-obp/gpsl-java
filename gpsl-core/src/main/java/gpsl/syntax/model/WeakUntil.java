package gpsl.syntax.model;

/**
 * Represents the temporal Weak Until operator (W).
 */
public record WeakUntil(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitWeakUntil(this, input);
    }
}
