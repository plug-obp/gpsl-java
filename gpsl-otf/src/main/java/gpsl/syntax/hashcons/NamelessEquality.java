package gpsl.syntax.hashcons;

import gpsl.syntax.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NamelessEquality implements Visitor<SyntaxTreeElement, Boolean> {
    static NamelessEquality INSTANCE = new NamelessEquality();
    public static <T extends SyntaxTreeElement> boolean same(T o1, T o2) {
        return INSTANCE.equals(o1, o2);
    }

    <T extends SyntaxTreeElement> boolean equals(T o1, T o2) {
        return o1.accept(this, o2);
    }

    @Override
    public Boolean visitSyntaxTreeElement(SyntaxTreeElement element, SyntaxTreeElement input) {
        return element.equals(input);
    }

    @Override
    public Boolean visitTrue(True element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof True) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean visitFalse(False element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof False) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean visitAtom(Atom element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Atom other) {
            return element.value().equals(other.value());
        }
        return false;
    }

    @Override
    public Boolean visitReference(Reference element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Reference other) {
            if (element.expression() == null || other.expression() == null) return element.name().equals(other.name());
            return element.expression().accept(this, other.expression());
        }
        return false;
    }

    @Override
    public Boolean visitNegation(Negation element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Negation other) {
            return element.expression().accept(this, other.expression());
        }
        return false;
    }

    @Override
    public Boolean visitNext(Next element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Next other) {
            return element.expression().accept(this, other.expression());
        }
        return false;
    }

    @Override
    public Boolean visitGlobally(Globally element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Globally other) {
            return element.expression().accept(this, other.expression());
        }
        return false;
    }

    @Override
    public Boolean visitEventually(Eventually element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Eventually other) {
            return element.expression().accept(this, other.expression());
        }
        return false;
    }

    @Override
    public Boolean visitConjunction(Conjunction element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Conjunction other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitDisjunction(Disjunction element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Disjunction other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }
    @Override
    public Boolean visitExclusiveDisjunction(ExclusiveDisjunction element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof ExclusiveDisjunction other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitImplication(Implication element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Implication other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitEquivalence(Equivalence element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Equivalence other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitStrongRelease(StrongRelease element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof StrongRelease other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitWeakRelease(WeakRelease element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof WeakRelease other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitStrongUntil(StrongUntil element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof StrongUntil other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitWeakUntil(WeakUntil element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof WeakUntil other) {
            return element.left().accept(this, other.left()) && element.right().accept(this, other.right());
        }
        return false;
    }

    @Override
    public Boolean visitExpressionDeclaration(ExpressionDeclaration element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof ExpressionDeclaration other) {
            return element.name().equals(other.name()) &&
                    element.expression().accept(this, other.expression());
        }
        return false;
    }

    @Override
    public Boolean visitState(State element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof State(String name)) {
            return element.name().equals(name);
        }
        return false;
    }

    @Override
    public Boolean visitLetExpression(LetExpression element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof LetExpression(Declarations declarations, SyntaxTreeElement expression)) {
            return element.declarations().accept(this, declarations) && element.expression().accept(this, expression);
        }
        return false;
    }

    @Override
    public Boolean visitDeclarations(Declarations element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Declarations(List<ExpressionDeclaration> declarations)) {
            return element.declarations().size() == declarations.size() &&
                    element.declarations().stream().allMatch(d ->
                            declarations.stream().anyMatch(d2 -> d.accept(this, d2)));
        }
        return false;
    }

    @Override
    public Boolean visitTransition(Transition element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Transition(State source, int priority, Expression guard, State target)) {
            return element.source().accept(this, source) &&
            element.target().accept(this, target) &&
            element.guard().accept(this, guard) &&
            element.priority() == priority;
        }
        return false;
    }

    @Override
    public Boolean visitAutomaton(Automaton element, SyntaxTreeElement input) {
        if (element == input) return true;
        if (input instanceof Automaton(
                AutomatonSemanticsKind semanticsKind, Set<State> states, Set<State> initialStates,
                Set<State> acceptStates, List<Transition> transitions
        )) {
            return element.semanticsKind().equals(semanticsKind) &&
                    equals(element.states(), states) &&
                    equals(element.initialStates(), initialStates) &&
                    equals(element.acceptStates(), acceptStates) &&
                    element.transitions().size() == transitions.size() &&
                    new HashSet<>(element.transitions()).containsAll(element.transitions());
        }
        return false;
    }
    <T extends SyntaxTreeElement> boolean equals(Set<T> a, Set<T> b) {
        return a.size() == b.size() &&
                a.stream().allMatch(e -> b.stream().anyMatch(e2 -> e.accept(this, e2)));
    }
}
