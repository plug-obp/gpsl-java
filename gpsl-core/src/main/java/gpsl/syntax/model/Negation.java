package gpsl.syntax.model;

/**
 * Represents logical negation (NOT).
 */
public record Negation(String operator, Expression expression) 
    implements UnaryExpression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
