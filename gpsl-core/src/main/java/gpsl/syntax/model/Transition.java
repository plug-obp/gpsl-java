package gpsl.syntax.model;

/**
 * Represents a transition in an automaton.
 */
public record Transition(
    State source,
    int priority,
    Expression guard,
    State target
) implements SyntaxTreeElement {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
