package gpsl.syntax.model;

/**
 * Represents the boolean true literal.
 */
public record True() implements Expression {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitTrue(this, input);
    }
}
