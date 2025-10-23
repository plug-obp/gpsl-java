package gpsl.semantics;

import gpsl.syntax.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AutomatonSemantics that validates automaton execution semantics.
 */
class AutomatonSemanticsTest {
    
    private State s0, s1, s2;
    private Expression trueExpr, falseExpr, atomP, atomQ;
    
    // Simple atom evaluator that checks predefined values
    private static final AtomEvaluator<Map<String, Boolean>> MAP_EVALUATOR = 
        (atomValue, context) -> context.getOrDefault(atomValue, false);
    
    @BeforeEach
    void setUp() {
        s0 = new State("s0");
        s1 = new State("s1");
        s2 = new State("s2");
        
        trueExpr = new True();
        falseExpr = new False();
        atomP = new Atom("p", "|");
        atomQ = new Atom("q", "|");
    }
    
    @Test
    void testInitialStates() {
        Set<State> initialStates = Set.of(s0, s1);
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            initialStates,
            Set.of(s2),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertEquals(initialStates.stream().toList(), semantics.initial());
    }
    
    @Test
    void testInitialStatesSingleState() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertEquals(List.of(s0), semantics.initial());
    }
    
    @Test
    void testActionsWithTrueGuard() {
        Transition t1 = new Transition(s0, 1, trueExpr, s1);
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        Map<String, Boolean> input = Map.of();
        List<Transition> actions = semantics.actions(input, s0);
        
        assertEquals(1, actions.size());
        assertTrue(actions.contains(t1));
    }
    
    @Test
    void testActionsWithFalseGuard() {
        Transition t1 = new Transition(s0, 1, falseExpr, s1);
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        Map<String, Boolean> input = Map.of();
        List<Transition> actions = semantics.actions(input, s0);
        
        assertTrue(actions.isEmpty());
    }
    
    @Test
    void testActionsWithAtomGuard() {
        Transition t1 = new Transition(s0, 1, atomP, s1);
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        // Test with p=true
        Map<String, Boolean> inputTrue = Map.of("p", true);
        List<Transition> actionsTrue = semantics.actions(inputTrue, s0);
        assertEquals(1, actionsTrue.size());
        assertTrue(actionsTrue.contains(t1));
        
        // Test with p=false
        Map<String, Boolean> inputFalse = Map.of("p", false);
        List<Transition> actionsFalse = semantics.actions(inputFalse, s0);
        assertTrue(actionsFalse.isEmpty());
    }
    
    @Test
    void testActionsWithMultipleTransitionsSamePriority() {
        Transition t1 = new Transition(s0, 1, atomP, s1);
        Transition t2 = new Transition(s0, 1, atomQ, s2);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s1, s2),
            List.of(t1, t2)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        // Both guards enabled
        Map<String, Boolean> input = Map.of("p", true, "q", true);
        List<Transition> actions = semantics.actions(input, s0);
        assertEquals(2, actions.size());
        assertTrue(actions.contains(t1));
        assertTrue(actions.contains(t2));
    }
    
    @Test
    void testActionsWithPrioritySelection() {
        // Lower priority value processed first (selected)
        Transition t1 = new Transition(s0, 1, atomP, s1);
        // Higher priority value processed second (should be ignored if t1 enabled)
        Transition t2 = new Transition(s0, 2, atomQ, s2);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s1, s2),
            List.of(t1, t2) // Must be in increasing priority order
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        // Both guards enabled, but only first enabled priority should fire
        Map<String, Boolean> input = Map.of("p", true, "q", true);
        List<Transition> actions = semantics.actions(input, s0);
        assertEquals(1, actions.size());
        assertTrue(actions.contains(t1));
        assertFalse(actions.contains(t2));
    }
    
    @Test
    void testActionsWithPriorityFallback() {
        // Higher priority transition with false guard
        Transition t1 = new Transition(s0, 2, atomP, s1);
        // Lower priority transition with true guard (should be selected)
        Transition t2 = new Transition(s0, 1, atomQ, s2);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s1, s2),
            List.of(t2, t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        // Higher priority disabled, lower priority enabled
        Map<String, Boolean> input = Map.of("p", false, "q", true);
        List<Transition> actions = semantics.actions(input, s0);
        assertEquals(1, actions.size());
        assertTrue(actions.contains(t2));
        assertFalse(actions.contains(t1));
    }
    
    @Test
    void testActionsWithComplexGuard() {
        // Guard: p && q
        Expression complexGuard = new Conjunction("&&", atomP, atomQ);
        Transition t1 = new Transition(s0, 1, complexGuard, s1);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        // Both true - should be enabled
        Map<String, Boolean> input1 = Map.of("p", true, "q", true);
        assertEquals(1, semantics.actions(input1, s0).size());
        
        // One false - should be disabled
        Map<String, Boolean> input2 = Map.of("p", true, "q", false);
        assertEquals(0, semantics.actions(input2, s0).size());
    }
    
    @Test
    void testActionsFromDifferentStates() {
        Transition t1 = new Transition(s0, 1, trueExpr, s1);
        Transition t2 = new Transition(s1, 1, trueExpr, s2);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s2),
            List.of(t1, t2)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        Map<String, Boolean> input = Map.of();
        
        // From s0, only t1 should be available
        List<Transition> actionsFromS0 = semantics.actions(input, s0);
        assertEquals(1, actionsFromS0.size());
        assertTrue(actionsFromS0.contains(t1));
        
        // From s1, only t2 should be available
        List<Transition> actionsFromS1 = semantics.actions(input, s1);
        assertEquals(1, actionsFromS1.size());
        assertTrue(actionsFromS1.contains(t2));
    }
    
    @Test
    void testActionsFromStateWithNoTransitions() {
        Transition t1 = new Transition(s0, 1, trueExpr, s1);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s2),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        Map<String, Boolean> input = Map.of();
        List<Transition> actions = semantics.actions(input, s2);
        
        assertTrue(actions.isEmpty());
    }
    
    @Test
    void testActionsThrowsOnUnsortedPriorities() {
        // Incorrectly ordered transitions (higher priority before lower)
        Transition t1 = new Transition(s0, 2, trueExpr, s1);
        Transition t2 = new Transition(s0, 1, trueExpr, s2);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s1, s2),
            List.of(t1, t2) // Wrong order!
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        Map<String, Boolean> input = Map.of();
        
        assertThrows(IllegalStateException.class, () -> {
            semantics.actions(input, s0);
        });
    }
    
    @Test
    void testExecute() {
        Transition t1 = new Transition(s0, 1, trueExpr, s1);
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        Map<String, Boolean> input = Map.of();
        List<State> result = semantics.execute(t1, input, s0);
        
        assertEquals(List.of(s1), result);
    }
    
    @Test
    void testIsAcceptingTrue() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s1, s2),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertTrue(semantics.isAccepting(s1));
        assertTrue(semantics.isAccepting(s2));
    }
    
    @Test
    void testIsAcceptingFalse() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s2),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertFalse(semantics.isAccepting(s0));
        assertFalse(semantics.isAccepting(s1));
    }
    
    @Test
    void testGetAutomaton() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertSame(automaton, semantics.getAutomaton());
    }
    
    @Test
    void testNullAutomatonThrows() {
        assertThrows(NullPointerException.class, () -> {
            new AutomatonSemantics<>(null, MAP_EVALUATOR);
        });
    }
    
    @Test
    void testNullConfigurationThrows() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertThrows(NullPointerException.class, () -> {
            semantics.actions(Map.of(), null);
        });
    }
    
    @Test
    void testNullTransitionThrows() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertThrows(NullPointerException.class, () -> {
            semantics.execute(null, Map.of(), s0);
        });
    }
    
    @Test
    void testNullStateInIsAcceptingThrows() {
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of()
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertThrows(NullPointerException.class, () -> {
            semantics.isAccepting(null);
        });
    }
    
    @Test
    void testBuchiAutomaton() {
        // Test that BUCHI semantics kind works the same as NFA
        Transition t1 = new Transition(s0, 1, trueExpr, s1);
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.BUCHI,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t1)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        assertEquals(List.of(s0), semantics.initial());
        assertTrue(semantics.isAccepting(s1));
        assertEquals(1, semantics.actions(Map.of(), s0).size());
    }
    
    @Test
    void testCompleteScenario() {
        // Create a simple automaton: s0 -[p]-> s1 -[q]-> s2 (accepting)
        Transition t1 = new Transition(s0, 1, atomP, s1);
        Transition t2 = new Transition(s1, 1, atomQ, s2);
        
        Automaton automaton = new Automaton(
            AutomatonSemanticsKind.NFA,
            Set.of(s0, s1, s2),
            Set.of(s0),
            Set.of(s2),
            List.of(t1, t2)
        );
        
        AutomatonSemantics<Map<String, Boolean>> semantics = 
            new AutomatonSemantics<>(automaton, MAP_EVALUATOR);
        
        // Start from initial
        State current = semantics.initial().iterator().next();
        assertEquals(s0, current);
        assertFalse(semantics.isAccepting(current));
        
        // Execute first transition with p=true
        Map<String, Boolean> input1 = Map.of("p", true);
        List<Transition> actions1 = semantics.actions(input1, current);
        assertEquals(1, actions1.size());
        current = semantics.execute(actions1.get(0), input1, current).iterator().next();
        assertEquals(s1, current);
        assertFalse(semantics.isAccepting(current));
        
        // Execute second transition with q=true
        Map<String, Boolean> input2 = Map.of("q", true);
        List<Transition> actions2 = semantics.actions(input2, current);
        assertEquals(1, actions2.size());
        current = semantics.execute(actions2.get(0), input2, current).iterator().next();
        assertEquals(s2, current);
        assertTrue(semantics.isAccepting(current));
    }
}
