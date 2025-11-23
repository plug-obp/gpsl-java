package gpsl.syntax.hashcons;

import gpsl.syntax.model.*;

import java.util.Objects;
import java.util.stream.Stream;

public class NamelessHash implements Visitor<Void, Integer> {
    static NamelessHash INSTANCE = new NamelessHash();
    public static <T extends SyntaxTreeElement> int hashCode(T o) {
        return INSTANCE.hash(o);
    }

    <T extends SyntaxTreeElement> int hash(T o) {
        return o.accept(this, null);
    }

    @Override
    public Integer visitTrue(True element, Void input) {
        return Objects.hash(true);
    }

    @Override
    public Integer visitFalse(False element, Void input) {
        return Objects.hash(false);
    }

    @Override
    public Integer visitAtom(Atom element, Void input) {
        return Objects.hash(element.value());
    }

    @Override
    public Integer visitReference(Reference element, Void input) {
        if (element.expression() == null) {
            return Objects.hash(element.name());
        }
        return element.expression().accept(this, input);
    }

    @Override
    public Integer visitUnaryExpression(UnaryExpression element, Void input) {
        var he = element.expression().accept(this, input);
        return Objects.hash(element.getClass().hashCode(), he);
    }

    @Override
    public Integer visitBinaryExpression(BinaryExpression element, Void input) {
        var hel = element.left().accept(this, input);
        var her = element.right().accept(this, input);
        return Objects.hash(element.getClass().hashCode(), hel, her);
    }

    @Override
    public Integer visitExpressionDeclaration(ExpressionDeclaration element, Void input) {
        return element.expression().accept(this, input);
    }

    @Override
    public Integer visitLetExpression(LetExpression element, Void input) {
        var dh = element.declarations().accept(this, input);
        var eh = element.expression().accept(this, input);
        return Objects.hash(dh, eh);
    }

    @Override
    public Integer visitDeclarations(Declarations element, Void input) {
        return hash(element.declarations().stream());
    }

    @Override
    public Integer visitState(State element, Void input) {
        return Objects.hash(element.name());
    }

    @Override
    public Integer visitTransition(Transition element, Void input) {
        var sh = element.source().accept(this, input);
        var th = element.target().accept(this, input);
        var eh = element.guard().accept(this, input);
        return Objects.hash(sh, th, eh, element.priority());
    }

    @Override
    public Integer visitAutomaton(Automaton element, Void input) {
        var hk = element.semanticsKind().hashCode();
        var hs = hash(element.states().stream());
        var hi = hash(element.initialStates().stream());
        var ha = hash(element.acceptStates().stream());
        var ht = hash(element.transitions().stream());
        return Objects.hash(hk, hs, hi, ha, ht);
    }

    <T extends SyntaxTreeElement> int hash(Stream<T> stream) {
        return stream.mapToInt(o -> o.accept(this, null)).reduce(1, (a, b) ->  31 * a + b);
    }
}
