package gpsl.syntax.model;

/**
 * Represents logical exclusive disjunction (XOR).
 */
public record ExclusiveDisjunction(String operator, Expression left, Expression right) 
    implements BinaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
