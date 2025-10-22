package gpsl.syntax.model;

/**
 * Represents logical conjunction (AND).
 */
public record Conjunction(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitConjunction(this, input);
    }
}
