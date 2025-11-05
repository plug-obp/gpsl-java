package gpsl.semantics;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

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

    static <T> AtomEvaluator<T> from(BiPredicate<String, T> func) {
        return func::test;
    }

    static <T> AtomEvaluator<T> from(BiFunction<String, T, Boolean> func) {
        return func::apply;
    }

    default BiFunction<String, T, Boolean> toBiFunction() {
        return this::evaluate;
    }

    default BiPredicate<String, T> toBiPredicate() {
        return this::evaluate;
    }
}
