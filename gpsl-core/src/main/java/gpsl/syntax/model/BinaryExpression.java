package gpsl.syntax.model;

/**
 * Base interface for binary expressions.
 */
public sealed interface BinaryExpression extends Expression
    permits Conjunction, Disjunction, ExclusiveDisjunction,
            Implication, Equivalence, StrongUntil, WeakUntil,
            StrongRelease, WeakRelease {
    
    String operator();
    Expression left();
    Expression right();
    
    @Override
    default <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
