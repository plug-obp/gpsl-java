package gpsl.syntax.ite;

import gpsl.syntax.hashcons.HashConsingFactory;
import gpsl.syntax.model.Atom;
import gpsl.syntax.model.Conditional;
import gpsl.syntax.model.Expression;

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
        int min = Integer.MAX_VALUE;
        Atom top = null;
        if (condition instanceof Conditional f) {
            var vF = (Atom) f.condition();
            var tF = orderIndex(vF);
            min = tF;
            top = vF;
        }

        if (thenClause instanceof Conditional g) {
            var vG = (Atom) g.condition();
            var tG = orderIndex(vG);
            if (tG < min) {
                min = tG;
                top = vG;
            }
        }

        if (elseClause instanceof Conditional h) {
            var vH = (Atom) h.condition();
            var tH = orderIndex(vH);
            if (tH < min) {
                min = tH;
                top = vH;
            }
        }

        var f1 = cofactor(condition, min, true);
        var f0 = cofactor(condition, min, false);

        var g1 = cofactor(thenClause, min, true);
        var g0 = cofactor(thenClause, min, false);

        var h1 = cofactor(elseClause, min, true);
        var h0 = cofactor(elseClause, min, false);

        //Shannon expansion
        var t = conditional(f1, g1, h1);
        var e = conditional(f0, g0, h0);
        if (t == e) return t;

        return super.conditional(top, t, e);
    }

    int orderIndex(Atom var) {
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
