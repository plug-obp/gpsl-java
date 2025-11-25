package gpsl.syntax.model;

public record Conditional(Expression condition, Expression trueBranch, Expression falseBranch) implements Expression {
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitConditional(this, input);
    }
}
