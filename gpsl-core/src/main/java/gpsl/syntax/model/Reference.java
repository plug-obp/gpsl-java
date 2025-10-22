package gpsl.syntax.model;

/**
 * Represents a reference to a named expression.
 * The expression field can be set after construction for lazy resolution.
 */
public final class Reference implements Expression {
    private final String name;
    private Expression expression;
    
    public Reference(String name) {
        this.name = name;
        this.expression = null;
    }
    
    public String name() {
        return name;
    }
    
    public Expression expression() {
        return expression;
    }
    
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitReference(this, input);
    }
}
