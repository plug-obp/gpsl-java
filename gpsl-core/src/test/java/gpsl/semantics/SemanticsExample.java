package gpsl.semantics;
import gpsl.syntax.TestHelpers;

import gpsl.syntax.Reader;
import gpsl.syntax.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Example demonstrating the use of the general Semantics class.
 * Shows how to use semantics with both Expressions and Automata.
 */
public class SemanticsExample {
    
    public static void main(String[] args) throws Exception {
        // Example 1: Using Semantics with an Expression
        demonstrateExpressionSemantics();
        
        // Example 2: Using Semantics with an Automaton
        demonstrateAutomatonSemantics();
    }
    
    private static void demonstrateExpressionSemantics() throws Exception {
        System.out.println("=== Expression Semantics Example ===\n");
        
        // Create an LTL expression: F(p) - "eventually p"
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        System.out.println("Expression: F(p)");
        
        // Create an atom evaluator
        AtomEvaluator<Map<String, Boolean>> evaluator = (atomValue, input) -> 
            input.getOrDefault(atomValue, false);
        
        // Create semantics from the expression
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);
        
        // Get initial states
        List<State> initialStates = semantics.initial();
        System.out.println("Initial states: " + initialStates.size());
        
        // Get the underlying automaton
        Automaton automaton = semantics.getAutomaton();
        System.out.println("Automaton states: " + automaton.states().size());
        System.out.println("Automaton transitions: " + automaton.transitions().size());
        
        // Simulate execution
        State currentState = initialStates.iterator().next();
        System.out.println("\nSimulating execution from: " + currentState.name());
        
        // Step 1: p is false
        Map<String, Boolean> input1 = Map.of("p", false);
        List<Transition> actions1 = semantics.actions(input1, currentState);
        System.out.println("  With p=false, enabled actions: " + actions1.size());
        
        if (!actions1.isEmpty()) {
            Transition transition = actions1.get(0);
            List<State> nextStates = semantics.execute(transition, input1, currentState);
            State nextState = nextStates.iterator().next();
            System.out.println("  Executed transition to: " + nextState.name());
            System.out.println("  Is accepting? " + semantics.isAccepting(nextState));
            currentState = nextState;
        }
        
        // Step 2: p is true
        Map<String, Boolean> input2 = Map.of("p", true);
        List<Transition> actions2 = semantics.actions(input2, currentState);
        System.out.println("  With p=true, enabled actions: " + actions2.size());
        
        if (!actions2.isEmpty()) {
            Transition transition = actions2.get(0);
            List<State> nextStates = semantics.execute(transition, input2, currentState);
            State nextState = nextStates.iterator().next();
            System.out.println("  Executed transition to: " + nextState.name());
            System.out.println("  Is accepting? " + semantics.isAccepting(nextState));
        }
        
        System.out.println();
    }
    
    private static void demonstrateAutomatonSemantics() {
        System.out.println("=== Automaton Semantics Example ===\n");
        
        // Manually create a simple automaton
        State s0 = new State("s0_init");
        State s1 = new State("s1_accept");
        State s2 = new State("s2");
        
        // Create transitions with guards
        Atom atomP = new Atom("p", "|");
        Atom atomQ = new Atom("q", "|");
        True trueExpr = new True();
        
        Transition t0 = new Transition(s0, 0, atomP, s1);      // s0 --p--> s1
        Transition t1 = new Transition(s0, 0, trueExpr, s2);   // s0 --true--> s2
        Transition t2 = new Transition(s1, 0, atomQ, s1);      // s1 --q--> s1 (self-loop)
        Transition t3 = new Transition(s2, 0, trueExpr, s0);   // s2 --true--> s0
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.BUCHI,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s1),
            List.of(t0, t1, t2, t3)
        );
        
        System.out.println("Created automaton with " + automaton.states().size() + " states");
        
        // Create atom evaluator
        AtomEvaluator<Map<String, Boolean>> evaluator = (atomValue, input) -> 
            input.getOrDefault(atomValue, false);
        
        // Create semantics from the automaton
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(automaton, evaluator);
        
        // Simulate execution
        State currentState = s0;
        System.out.println("\nSimulating execution from: " + currentState.name());
        
        // Step 1: p=true, q=false
        Map<String, Boolean> input1 = Map.of("p", true, "q", false);
        List<Transition> actions1 = semantics.actions(input1, currentState);
        System.out.println("  With p=true, q=false:");
        System.out.println("    Enabled transitions: " + actions1.size());
        for (Transition t : actions1) {
            System.out.println("      " + t.source().name() + " -> " + t.target().name());
        }
        
        // Execute first transition
        if (!actions1.isEmpty()) {
            Transition transition = actions1.get(0);
            currentState = semantics.execute(transition, input1, currentState).iterator().next();
            System.out.println("    Moved to: " + currentState.name());
            System.out.println("    Is accepting? " + semantics.isAccepting(currentState));
        }
        
        // Step 2: p=false, q=true
        Map<String, Boolean> input2 = Map.of("p", false, "q", true);
        List<Transition> actions2 = semantics.actions(input2, currentState);
        System.out.println("  With p=false, q=true:");
        System.out.println("    Enabled transitions: " + actions2.size());
        for (Transition t : actions2) {
            System.out.println("      " + t.source().name() + " -> " + t.target().name());
        }
        
        System.out.println();
    }
}
