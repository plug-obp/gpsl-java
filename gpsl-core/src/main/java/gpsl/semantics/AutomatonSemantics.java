package gpsl.semantics;

import gpsl.syntax.model.*;
import obp3.runtime.sli.ISemanticRelation;

import java.util.*;

/**
 * Provides operational semantics for GPSL automata.
 * This class handles the execution of automata including:
 * - Determining initial states
 * - Computing enabled transitions based on guards and priorities
 * - Executing transitions
 * - Checking accepting states
 * 
 * @param <T> the type of input context passed to guard evaluators
 */
public class AutomatonSemantics<T> implements ISemanticRelation<T, Transition, State> {
    
    private final Automaton automaton;
    private final Evaluator<T> guardEvaluator;
    
    /**
     * Creates an automaton semantics instance.
     * 
     * @param automaton the automaton to provide semantics for
     * @param atomEvaluator the evaluator for atomic propositions in guards
     */
    public AutomatonSemantics(Automaton automaton, AtomEvaluator<T> atomEvaluator) {
        this.automaton = Objects.requireNonNull(automaton, "Automaton cannot be null");
        this.guardEvaluator = new Evaluator<>(atomEvaluator);
    }
    
    /**
     * Returns the initial states of the automaton.
     * 
     * @return an unmodifiable set of initial states
     */
    public List<State> initial() {
        return automaton.initialStates().stream().toList();
    }
    
    /**
     * Computes the enabled actions (transitions) from a given configuration.
     * Returns transitions with the highest priority whose guards are satisfied.
     * 
     * The algorithm:
     * 1. Filters transitions by source state (configuration)
     * 2. Evaluates guards in priority order (highest first)
     * 3. Returns all enabled transitions at the highest priority level
     * 
     * @param input the input context for guard evaluation
     * @param configuration the current state
     * @return list of enabled transitions at the highest priority
     * @throws IllegalStateException if transitions are not sorted by priority
     */
    public List<Transition> actions(T input, State configuration) {
        Objects.requireNonNull(configuration, "Configuration cannot be null");
        
        // Keep only transitions from the current state
        List<Transition> transitionsFromState = automaton.transitions().stream()
            .filter(t -> t.source().equals(configuration))
            .toList();
        
        if (transitionsFromState.isEmpty()) {
            return List.of();
        }
        
        // Find enabled transitions at the highest priority
        List<Transition> enabledTransitions = new ArrayList<>();
        int currentPriority = Integer.MIN_VALUE;
        boolean hasEnabledTransitionAtCurrentPriority = false;
        
        for (Transition transition : transitionsFromState) {
            // Update priority if we haven't found any enabled transitions yet
            if (!hasEnabledTransitionAtCurrentPriority) {
                currentPriority = transition.priority();
            }
            
            // Process transitions at the current priority level
            if (transition.priority() == currentPriority) {
                // Evaluate the guard
                if (evaluateGuard(transition.guard(), input)) {
                    enabledTransitions.add(transition);
                    hasEnabledTransitionAtCurrentPriority = true;
                }
            }
            
            // Verify transitions are sorted in increasing priority order
            if (transition.priority() < currentPriority) {
                throw new IllegalStateException(
                    "Transitions must be sorted in increasing priority order in the automaton. " +
                    "Found priority " + transition.priority() + " after priority " + currentPriority
                );
            }
            
            // If we've found enabled transitions and moved to a lower priority, stop
            if (hasEnabledTransitionAtCurrentPriority && transition.priority() > currentPriority) {
                break;
            }
        }
        
        return Collections.unmodifiableList(enabledTransitions);
    }
    
    /**
     * Executes a transition from the given configuration.
     * 
     * @param transition the transition to execute (action)
     * @param input the input context (not used in basic execution)
     * @param configuration the current state (not used in basic execution)
     * @return set containing the target state
     */
    public List<State> execute(Transition transition, T input, State configuration) {
        Objects.requireNonNull(transition, "Transition cannot be null");
        return List.of(transition.target());
    }
    
    /**
     * Checks if a given state is an accepting state.
     * 
     * @param state the state to check
     * @return true if the state is an accepting state, false otherwise
     */
    public boolean isAccepting(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        return automaton.acceptStates().contains(state);
    }
    
    /**
     * Gets the automaton associated with this semantics instance.
     * 
     * @return the automaton
     */
    public Automaton getAutomaton() {
        return automaton;
    }
    
    /**
     * Evaluates a guard expression in the given context.
     * 
     * @param guard the guard expression to evaluate
     * @param input the input context
     * @return true if the guard is satisfied, false otherwise
     */
    private boolean evaluateGuard(Expression guard, T input) {
        try {
            return guard.accept(guardEvaluator, input);
        } catch (Exception e) {
            throw new GuardEvaluationException(
                "Failed to evaluate guard: " + e.getMessage(), e
            );
        }
    }
    
    /**
     * Exception thrown when guard evaluation fails.
     */
    public static class GuardEvaluationException extends RuntimeException {
        public GuardEvaluationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
