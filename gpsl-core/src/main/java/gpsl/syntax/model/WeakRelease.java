package gpsl.syntax.model;

/**
 * Represents the temporal Weak Release operator (R).
 */
public record WeakRelease(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
