package gpsl.syntax.model;

/**
 * Represents the boolean false literal.
 */
public record False() implements Expression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
