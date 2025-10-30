package gpsl.semantics;

import gpsl.toBuchi.Expression2BuchiAutomaton;
import gpsl.syntax.model.*;
import obp3.runtime.sli.DependentSemanticRelation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides operational semantics for any GPSL syntax tree element.
 * 
 * <p>This class delegates to {@link AutomatonSemantics} for execution:
 * <ul>
 *   <li>If the element is an {@link Automaton}, it uses it directly</li>
 *   <li>If the element is an {@link Expression}, it converts it to an automaton first using {@link Expression2BuchiAutomaton}</li>
 * </ul>
 * 
 * <p>This allows uniform treatment of both automata and expressions in verification and execution.
 * 
 * @param <T> the type of input context passed to guard evaluators
 */
public class Semantics<T> implements DependentSemanticRelation<T, Transition, State> {
    
    private final AutomatonSemantics<T> automatonSemantics;
    
    /**
     * Creates a semantics instance for a syntax tree element.
     * 
     * @param element the syntax tree element (Automaton or Expression)
     * @param atomEvaluator the evaluator for atomic propositions
     * @throws IllegalArgumentException if element is not an Automaton or Expression
     */
    public Semantics(SyntaxTreeElement element, AtomEvaluator<T> atomEvaluator) {
        Objects.requireNonNull(element, "Element cannot be null");
        Objects.requireNonNull(atomEvaluator, "AtomEvaluator cannot be null");
        
        Automaton automaton = toAutomaton(element);
        this.automatonSemantics = new AutomatonSemantics<>(automaton, atomEvaluator);
    }
    
    /**
     * Converts a syntax tree element to an automaton.
     * 
     * @param element the element to convert
     * @return the automaton
     * @throws IllegalArgumentException if element is not an Automaton or Expression
     */
    private Automaton toAutomaton(SyntaxTreeElement element) {
        if (element instanceof Automaton a) { return a; }

        var exp = PropositionalToNFA.toExpression(element);
        return PropositionalToNFA
                .toNFA(element)
                .or(() -> exp.flatMap(this::toBuchi))
                .orElseThrow(() -> new SemanticConversionException("Cannot convert element to automaton"));
    }

    private Optional<Automaton> toBuchi(Expression expression) {
        var result = Expression2BuchiAutomaton.convert(expression);
        if (result instanceof rege.reader.infra.ParseResult.Success<Automaton>(Automaton value)) {
            return Optional.of(value);
        } else if (result instanceof rege.reader.infra.ParseResult.Failure<Automaton> failure) {
            throw new SemanticConversionException(
                    "Failed to convert expression to automaton:\n" + failure.formatErrors()
            );
        } else {
            throw new SemanticConversionException("Unexpected ParseResult type");
        }
    }
    
    /**
     * Returns the initial states of the automaton.
     * 
     * @return an unmodifiable set of initial states
     */
    public List<State> initial() {
        return automatonSemantics.initial();
    }

    /**
     * Computes the enabled actions (transitions) from a given configuration.
     * Returns transitions with the highest priority whose guards are satisfied.
     * 
     * Priority semantics: Lower numerical values have higher precedence (0 > 1 > 2 > 3...).
     * 
     * @param input the input context for guard evaluation
     * @param configuration the current state
     * @return list of enabled transitions at the highest priority (lowest numerical value)
     */
    public List<Transition> actions(T input, State configuration) {
        return automatonSemantics.actions(input, configuration);
    }

    /**
     * Executes a transition from the given configuration.
     * 
     * @param transition the transition to execute (action)
     * @param input the input context
     * @param configuration the current state
     * @return set containing the target state
     */
    public List<State> execute(Transition transition, T input, State configuration) {
        return automatonSemantics.execute(transition, input, configuration);
    }
    
    /**
     * Checks if a given state is an accepting state.
     * 
     * @param state the state to check
     * @return true if the state is an accepting state, false otherwise
     */
    public boolean isAccepting(State state) {
        return automatonSemantics.isAccepting(state);
    }
    
    /**
     * Gets the underlying automaton used for semantics.
     * 
     * @return the automaton
     */
    public Automaton getAutomaton() {
        return automatonSemantics.getAutomaton();
    }
    
    /**
     * Gets the automaton semantics instance used by this semantics.
     * 
     * @return the automaton semantics
     */
    public AutomatonSemantics<T> getAutomatonSemantics() {
        return automatonSemantics;
    }
    
    /**
     * Exception thrown when conversion to automaton fails.
     */
    public static class SemanticConversionException extends RuntimeException {
        public SemanticConversionException(String message) {
            super(message);
        }
        
        public SemanticConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
