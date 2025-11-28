package gpsl.syntax.ite;

import gpsl.syntax.hashcons.HashConsingFactory;
import gpsl.syntax.model.Atom;
import gpsl.syntax.model.Conditional;
import gpsl.syntax.model.Expression;
import gpsl.syntax.model.SyntaxTreeElement;
import obp3.hashcons.HashConsed;

import java.util.Comparator;
import java.util.stream.Stream;

public class FastPathIteFactory extends HashConsingFactory {
    public FastPathIteFactory() {
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
        // ite(f, f, g) = f ∨ g = ite(f, ⊤, g)
        if (condition == thenClause) {
            return conditional(condition, t(), elseClause);
        }
        // ite(f, g, f) = f ∧ g = ite(f, g, ⊥)
        if (condition == elseClause) {
            return conditional(condition, thenClause, f());
        }

        //I suppose that condition, then and else are oneOf(ITE | t | f)

        //find the top variable, here we use hashcons order
        HashConsed<SyntaxTreeElement> top = Stream.of(condition, thenClause, elseClause)
                .filter(e -> e instanceof Conditional)
                .map(e -> (Conditional) e)
                .map(c -> (Atom) c.condition())
                .map(intern.map()::get)
                .min(Comparator.comparingInt(HashConsed::tag))
                .orElseThrow(()-> new RuntimeException("should not happen"));

        var f1 = cofactor(condition, top.tag(), true);
        var f0 = cofactor(condition, top.tag(), false);

        var g1 = cofactor(thenClause, top.tag(), true);
        var g0 = cofactor(thenClause, top.tag(), false);

        var h1 = cofactor(elseClause, top.tag(), true);
        var h0 = cofactor(elseClause, top.tag(), false);

        //Shannon expansion
        var t = conditional(f1, g1, h1);
        var e = conditional(f0, g0, h0);
        if (t == e) return t;

        return super.conditional((Expression) top.node(), t, e);
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

    int orderIndex(Expression var) {
        return intern.map().get(var).tag();
    }

    @Override
    public Expression conjunction(String op, Expression left, Expression right) {
        // ⊤ ∧ m = m
        if (left == t()) return right;
        // m ∧ ⊤ = m
        if (right == t()) return left;
        // ⊥ ∧ ⊥ = ⊥, ⊥ ∧ m = ⊥, m ∧ ⊥ = ⊥
        if (left == f() || right == f()) return f();
        // m ∧ m = m
        if (left == right) return left;
        // m ∧ n -> n ∧ m, where m.tag > n.tag -- normalize commutativity
        if (orderIndex(right) < orderIndex(left)) {
            return conjunction(op, right, left);
        }
        // m ∧ n = ite(m, n, ⊥)
        return conditional(left, right, f());
    }

    @Override
    public Expression disjunction(String op, Expression left, Expression right) {
        // ⊤ ∨ ⊤ = ⊤, m ∨ ⊤ = ⊤, ⊤ ∨ m = ⊤
        if (left == t() || right == t()) return t();
        // ⊥ ∨ m = m
        if (left == f()) return right;
        // m ∨ ⊥ = m
        if (right == f()) return left;
        // m ∨ m = m
        if (left == right) return left;
        // m ∨ n -> n ∨ m, where m.tag > n.tag -- normalize commutativity
        if (orderIndex(right) < orderIndex(left)) {
            return disjunction(op, right, left);
        }
        // m ∨ n = ite(m, ⊤, n)
        return conditional(left, t(), right);
    }

    @Override
    public Expression negation(String op, Expression expression) {
        //¬⊤ = ⊥
        if (expression == t()) return f();
        //¬⊥ = ⊤
        if (expression == f()) return t();
        // ¬¬m = m -- double negation elimination
        if (expression instanceof Conditional(Expression condition, Expression l, Expression r) && l == f() && r == t()) {
            return condition;
        }

        //¬f = ite(f, ⊥, ⊤)
        return conditional(expression, f(), t());
    }

    @Override
    public Expression exclusiveDisjunction(String op, Expression left, Expression right) {
        // ⊥ ⊕ g = g
        if (left == f()) return right;
        // f ⊕ ⊥ = f
        if (right == f()) return left;
        // ⊤ ⊕ g = ¬g
        if (left == t()) return not(right);
        // f ⊕ ⊤ = ¬f
        if (right == t()) return not(left);
        // f ⊕ f = ⊥
        if (left == right) return f();
        // m ⊕ n -> n ⊕ m, where m.tag > n.tag -- normalize commutativity
        if (orderIndex(right) < orderIndex(left)) {
            return exclusiveDisjunction(op, right, left);
        }

        // f ⊕ g = (f ∧ ¬g) ∨ (¬f ∧ g) = ite(f, ¬g, g)
        return conditional(left, not(right), right);
    }

    @Override
    public Expression implication(String op, Expression left, Expression right) {
        // ⊥ → m = ⊤, m → ⊤ = ⊤
        if (left == f() || right == t()) return t();
        // ⊤ → m = m
        if (left == t()) return right;
        // m → ⊥ = ¬m
        if (right == f()) return not(left);
        // m → m = ⊤
        if (left == right) return t();

        // f → g = ¬f ∨ g = ite(f, g, ⊤)
        return conditional(left, right, t());
    }

    @Override
    public Expression equivalence(String op, Expression left, Expression right) {
        // ⊤ ↔ g = g
        if (left == t()) return right;
        // f ↔ ⊤ = f
        if (right == t()) return left;
        // ⊥ ↔ g = ¬g
        if (left == f()) return not(right);
        // f ↔ ⊥ = ¬f
        if (right == f()) return not(left);
        // f ↔ f = ⊤
        if (left == right) return t();
        // m ↔ n -> n ↔ m, where m.tag > n.tag -- normalize commutativity
        if (orderIndex(right) < orderIndex(left)) {
            return equivalence(op, right, left);
        }

        // f ↔ g = (f → g) ∧ (g → f) = ite(f, g, ¬g) # Same as XNOR
        return conditional(left, right, not(right));
    }

    public Expression nand(Expression left, Expression right) {
        // f ⊼ ⊥ = ⊤, ⊥ ⊼ g = ⊤
        if (left == f() || right == f()) return t();
        // ⊤ ⊼ g = ¬g
        if (left == t()) return not(right);
        // f ⊼ ⊤ = ¬f
        if (right == t()) return not(left);
        // f ⊼ f = ¬f
        if (left == right) return not(left);
        // m ⊼ n -> n ⊼ m, where m.tag > n.tag -- normalize commutativity
        if (orderIndex(right) < orderIndex(left)) {
            return nand(right, left);
        }

        // f ⊼ g = ¬(f ∧ g) = ite(f, ¬g, true)
        return conditional(left, not(right), t());
    }

    public Expression nor(Expression left, Expression right) {
        // ⊤ ⊽ m = ⊥, m ⊽ ⊤ = ⊥
        if (left == t() || right == t()) return f();
        // ⊥ ⊽ g = ¬g
        if (left == f()) return not(right);
        // f ⊽ ⊥ = ¬f
        if (right == f()) return not(left);
        // f ⊽ f = ¬f
        if (left == right) return not(left);
        // m ⊽ n -> n ⊽ m, where m.tag > n.tag -- normalize commutativity
        if (orderIndex(right) < orderIndex(left)) {
            return nor(right, left);
        }

        // f ⊽ g = ¬(f ∨ g) = ite(f, false, ¬g)
        return conditional(left, f(), not(right));
    }

    public Expression xnor(Expression left, Expression right) {
        // f ⊙ g = ¬(f ⊕ g) = ite(f, g, ¬g)  # Same as equiv!
        return equivalence(left, right);
    }
}
