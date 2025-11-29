package gpsl.syntax.ite;

import gpsl.syntax.hashcons.HashConsingFactory;
import gpsl.syntax.model.Atom;
import gpsl.syntax.model.Conditional;
import gpsl.syntax.model.Expression;
import gpsl.syntax.model.SyntaxTreeElement;
import obp3.hashcons.HashConsed;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Inspired by:
 * W. K. C. Lam and R. K. Brayton,
 * "On relationship between ITE and BDD,"
 * Proceedings 1992 IEEE International Conference on Computer Design: VLSI in Computers and Processors,
 * Cambridge, MA, USA, 1992, pp. 448-451, doi: 10.1109/ICCD.1992.276312.
 */
public class ITEFactory extends HashConsingFactory {
    public ITEFactory() {
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

        return super.conditional(condition, thenClause, elseClause);
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

        throw new IllegalStateException(
                "Variable ordering violation: cofactor var order %d > node var order %d".formatted(var.tag(), nodeOrder)
        );
    }

    int orderIndex(Expression var) {
        return intern.map().get(var).tag();
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
        return equivalence(left, right);
    }
}
