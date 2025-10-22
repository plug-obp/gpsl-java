package gpsl.syntax.model;

import java.util.Set;
import java.util.List;

/**
 * Represents an automaton (NFA or Büchi).
 * Note: Automaton is NOT an expression in GPSL, but a separate syntax element.
 */
public record Automaton(
    AutomatonSemanticsKind semanticsKind,
    Set<State> states,
    Set<State> initialStates,
    Set<State> acceptStates,
    List<Transition> transitions
) implements SyntaxTreeElement {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitAutomaton(this, input);
    }
}
