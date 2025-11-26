package gpsl.syntax.model;

/**
 * Represents the temporal Next operator.
 */
public record Next(String operator, Expression expression) 
    implements UnaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
