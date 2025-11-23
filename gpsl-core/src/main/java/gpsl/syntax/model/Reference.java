package gpsl.syntax.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference reference)) return false;
        return Objects.equals(name, reference.name) && Objects.equals(expression, reference.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expression);
    }

    @Override
    public String toString() {
        return "Ref@"+ System.identityHashCode(this) +"{" +
                "name='" + name + '\'' +
                ", expression=" + expression +
                '}';
    }
}
