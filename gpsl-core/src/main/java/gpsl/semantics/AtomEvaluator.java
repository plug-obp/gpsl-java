package gpsl.semantics;

/**
 * Functional interface for evaluating atomic propositions.
 * Implementations should evaluate the atom value in the given context.
 * 
 * @param <T> the type of input context passed to the evaluator
 */
@FunctionalInterface
public interface AtomEvaluator<T> {
    /**
     * Evaluates an atomic proposition with the given value in the provided context.
     * 
     * @param atomValue the value of the atom to evaluate
     * @param input the input context for evaluation
     * @return true if the atom holds in the given context, false otherwise
     */
    boolean evaluate(String atomValue, T input);
}
