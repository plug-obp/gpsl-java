package gpsl.syntax.model;

/**
 * Represents logical implication (->).
 */
public record Implication(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitImplication(this, input);
    }
}
