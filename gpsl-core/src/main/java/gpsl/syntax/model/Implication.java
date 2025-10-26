package gpsl.syntax.model;

/**
 * Represents logical implication ({@code ->}).
 */
public record Implication(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitImplication(this, input);
    }
}
