package gpsl.semantics;

import com.ibm.icu.text.PersonNameFormatter;
import gpsl.syntax.model.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PropositionalToNFA {
    //returns an automaton if the expression is a propositional formula, empty otherwise
    public static Optional<Automaton> toNFA(SyntaxTreeElement element) {
        return toExpression(element)
                .filter(e -> e.accept(new IsPropositional(), null))
                . map(PropositionalToNFA::toNFA);
    }

    public static Optional<Expression> toExpression(SyntaxTreeElement element) {
        return switch (element) {
            case ExpressionDeclaration ed -> Optional.of(ed.expression());
            case LetExpression le -> (le.expression() instanceof Expression expr) ? Optional.of(expr) : Optional.empty();
            case Expression expr -> Optional.of(expr);
            default -> Optional.empty();
        };
    }

    private static Automaton toNFA(Expression expression) {
        State initialState = new State("s");
        State finalState = new State("x");
        return new Automaton(
                AutomatonSemanticsKind.NFA,
                Set.of(initialState, finalState),
                Set.of(initialState),
                Set.of(finalState),
                List.of(
                        new Transition(initialState, 1, new Negation("!", expression), finalState),
                        new Transition(initialState, 1, expression, initialState)
                )
        );
    }
}
