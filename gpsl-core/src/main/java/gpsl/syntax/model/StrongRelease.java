package gpsl.syntax.model;

/**
 * Represents the temporal Strong Release operator (M).
 */
public record StrongRelease(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitStrongRelease(this, input);
    }
}
