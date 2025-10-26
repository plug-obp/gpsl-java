package gpsl.syntax.model;

/**
 * Represents logical equivalence ({@code <->}).
 */
public record Equivalence(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitEquivalence(this, input);
    }
}
