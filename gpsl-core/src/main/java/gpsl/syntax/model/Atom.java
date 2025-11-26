package gpsl.syntax.model;

/**
 * Represents an atomic proposition.
 */
public record Atom(String value, String delimiter) implements Expression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
