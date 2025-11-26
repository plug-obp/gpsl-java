package gpsl.syntax.model;

/**
 * Represents the temporal Eventually operator (F).
 */
public record Eventually(String operator, Expression expression) 
    implements UnaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
