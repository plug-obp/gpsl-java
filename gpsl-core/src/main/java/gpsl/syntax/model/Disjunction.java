package gpsl.syntax.model;

/**
 * Represents logical disjunction (OR).
 */
public record Disjunction(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitDisjunction(this, input);
    }
}
