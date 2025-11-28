package gpsl.syntax.ite;

import gpsl.syntax.hashcons.HashConsingFactory;
import gpsl.syntax.model.Atom;
import gpsl.syntax.model.Conditional;
import gpsl.syntax.model.Expression;
import gpsl.syntax.model.SyntaxTreeElement;
import obp3.hashcons.HashConsed;

import java.util.Comparator;
import java.util.stream.Stream;

public class IteFactory extends HashConsingFactory {
    public IteFactory() {
        super();
    }

    @Override
    public Expression atom(String value, String delimiter) {
        return super.conditional(super.atom(value, delimiter), t(), f());
    }

    @Override
    public Expression conditional(Expression condition, Expression thenClause, Expression elseClause) {
        //ite(⊤, m, _) = m  -- true condition
        if (condition == t()) return thenClause;
        //ite(⊥, _, m) = m -- false condition
        if (condition == f()) return elseClause;
        //ite(_, m, m) = m -- identical branches
        if (thenClause == elseClause) return thenClause;
        //ite(c, ⊤, ⊥) = c -- condition identity
        if (thenClause == t() && elseClause == f()) return condition;

        //I suppose that condition, then and else are oneOf(ITE | t | f)

        //find the top variable, here we use hashcons order
        HashConsed<SyntaxTreeElement> top = Stream.of(condition, thenClause, elseClause)
                .filter(e -> e instanceof Conditional)
                .map(e -> (Conditional) e)
                .map(c -> (Atom) c.condition())
                .map(intern.map()::get)
                .min(Comparator.comparingInt(HashConsed::tag))
                .orElseThrow(()-> new RuntimeException("should not happen"));

        var f = cofactors(condition, top);
        var g = cofactors(thenClause, top);
        var h = cofactors(elseClause, top);

        //Shannon expansion
        var t = conditional(f.high, g.high, h.high);
        var e = conditional(f.low, g.low, h.low);
        if (t == e) return t;

        return super.conditional((Expression)top.node(), t, e);
    }

    private record CofactorPair(Expression high, Expression low) {}

    private CofactorPair cofactors(Expression node, HashConsed<SyntaxTreeElement> var) {
        if (node == t() || node == f()) {
            return new CofactorPair(node, node);
        }

        var cond = (Conditional) node;
        var v = (Atom) cond.condition();
        int nodeOrder = orderIndex(v);

        if (var.tag() < nodeOrder) {
            return new CofactorPair(node, node);
        } else if (var.tag() == nodeOrder) {
            return new CofactorPair(cond.trueBranch(), cond.falseBranch());
        }

        throw new IllegalStateException("Variable ordering violation: expected order");
    }

    int orderIndex(Expression var) {
        return intern.map().get(var).tag();
    }

    Expression cofactor(Expression node, int min, boolean polarity) {
        //terminals are their own cofactors
        if (node == t() || node == f()) return node;
        var cond = (Conditional) node;
        var v = (Atom) cond.condition();
        var tag = intern.map().get(v).tag();
        if (polarity) {
            if (min < tag) {
                return node;
            } else if (min == tag) {
                return cond.trueBranch();
            }
            throw new RuntimeException("Impossible");
        }
        if (min < tag) {
            return node;
        } else if (min == tag) {
            return cond.falseBranch();
        }
        throw new RuntimeException("Impossible");
    }

    @Override
    public Expression conjunction(String op, Expression left, Expression right) {
        // f ∧ g = ite(f, g, false)
        return conditional(left, right, f());
    }

    @Override
    public Expression disjunction(String op, Expression left, Expression right) {
        // f ∨ g = ite(f, true, g)
        return conditional(left, t(), right);
    }

    @Override
    public Expression negation(String op, Expression expression) {
        //¬f = ite(f, false, true)
        return conditional(expression, f(), t());
    }

    @Override
    public Expression exclusiveDisjunction(String op, Expression left, Expression right) {
        // f ⊕ g = (f ∧ ¬g) ∨ (¬f ∧ g) = ite(f, ¬g, g)
        return conditional(left, not(right), right);
    }

    @Override
    public Expression implication(String op, Expression left, Expression right) {
        // f → g = ¬f ∨ g = ite(f, g, true)
        return conditional(left, right, t());
    }

    @Override
    public Expression equivalence(String op, Expression left, Expression right) {
        // f ↔ g = (f → g) ∧ (g → f) = ite(f, g, ¬g) # Same as XNOR
        return conditional(left, right, not(right));
    }

    public Expression nand(Expression left, Expression right) {
        // f ⊼ g = ¬(f ∧ g) = ite(f, ¬g, true)
        return conditional(left, not(right), t());
    }

    public Expression nor(Expression left, Expression right) {
        // f ⊽ g = ¬(f ∨ g) = ite(f, false, ¬g)
        return conditional(left, f(), not(right));
    }

    public Expression xnor(Expression left, Expression right) {
        // f ⊙ g = ¬(f ⊕ g) = ite(f, g, ¬g)  # Same as equiv!
        return conditional(left, right, not(right));
    }
}
