package gpsl.syntax.model;

/**
 * Represents the temporal Globally operator (G).
 */
public record Globally(String operator, Expression expression) 
    implements UnaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
