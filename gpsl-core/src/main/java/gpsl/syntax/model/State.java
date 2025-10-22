package gpsl.syntax.model;

import java.util.Objects;

/**
 * Represents a state in an automaton.
 */
public record State(String name) implements SyntaxTreeElement {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitState(this, input);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        return Objects.equals(name, other.name);
    }
}
