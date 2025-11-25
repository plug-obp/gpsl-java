package gpsl.syntax.model;

/**
 * Represents a conditional (ternary) expression ({@code condition ? trueBranch : falseBranch}).
 * <p>
 * The conditional operator evaluates the condition and returns the trueBranch if the condition
 * is true, otherwise returns the falseBranch. This is also known as the ternary operator.
 * </p>
 * <p>
 * The conditional operator is right-associative, meaning {@code a ? b : c ? d : e}
 * is parsed as {@code a ? b : (c ? d : e)}.
 * </p>
 * <p>
 * Examples:
 * <pre>
 * p ? q : r                    // If p then q else r
 * p ? (q ? r : s) : t          // Nested conditional
 * a ? b : c ? d : e            // Chained (right-associative)
 * (p and q) ? r : s            // Complex condition
 * p ? (&lt;&gt; q) : ([] r)          // Temporal operators in branches
 * </pre>
 * </p>
 *
 * @param condition the condition expression to evaluate
 * @param trueBranch the expression to return if condition is true
 * @param falseBranch the expression to return if condition is false
 */
public record Conditional(Expression condition, Expression trueBranch, Expression falseBranch) implements Expression {
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visitConditional(this, input);
    }
}
