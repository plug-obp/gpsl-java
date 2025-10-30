package gpsl.semantics;
import gpsl.syntax.TestHelpers;

import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the general Semantics class.
 * Tests semantics for both Automaton and Expression inputs.
 */
class SemanticsTest {

    // Simple atom evaluator for testing
    private static class TestAtomEvaluator implements AtomEvaluator<Map<String, Boolean>> {
        @Override
        public boolean evaluate(String atomValue, Map<String, Boolean> input) {
            return input.getOrDefault(atomValue, false);
        }
    }

    @Test
    void testSemanticsWithExpression() throws Exception {
        // Test: Create semantics from an expression F(p)
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);

        assertNotNull(semantics);
        assertNotNull(semantics.getAutomaton());
        assertFalse(semantics.initial().isEmpty(), "Should have initial states");
    }

    @Test
    void testSemanticsWithAutomaton() {
        // Test: Create semantics from an automaton directly
        State s0 = new State("s0");
        State s1 = new State("s1");

        Atom p = new Atom("p", "|");
        Transition t = new Transition(s0, 0, p, s1);

        Automaton automaton = new Automaton(
                AutomatonSemanticsKind.BUCHI,
                Set.of(s0, s1),
                Set.of(s0),
                Set.of(s1),
                List.of(t)
        );

        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(automaton, evaluator);

        assertNotNull(semantics);
        assertEquals(automaton, semantics.getAutomaton());
        assertEquals(List.of(s0), semantics.initial());
    }

    @Test
    void testInitialStatesFromExpression() throws Exception {
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);

        List<State> initialStates = semantics.initial();
        assertFalse(initialStates.isEmpty());

        // Initial states should contain "init" in their names
        for (State state : initialStates) {
            assertTrue(state.name().contains("init"));
        }
    }

    @Test
    void testActionsFromExpression() throws Exception {
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);

        // Get an initial state
        State initialState = semantics.initial().iterator().next();

        // Get enabled actions with p=false
        Map<String, Boolean> input1 = Map.of("p", false);
        List<Transition> actions1 = semantics.actions(input1, initialState);
        assertNotNull(actions1);

        // Get enabled actions with p=true
        Map<String, Boolean> input2 = Map.of("p", true);
        List<Transition> actions2 = semantics.actions(input2, initialState);
        assertNotNull(actions2);
    }

    @Test
    void testExecuteFromExpression() throws Exception {
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);

        // Get an initial state
        State initialState = semantics.initial().iterator().next();

        // Get enabled actions
        Map<String, Boolean> input = Map.of("p", false);
        List<Transition> actions = semantics.actions(input, initialState);

        if (!actions.isEmpty()) {
            Transition action = actions.get(0);
            List<State> nextStates = semantics.execute(action, input, initialState);

            assertNotNull(nextStates);
            assertFalse(nextStates.isEmpty());
            assertEquals(1, nextStates.size());
            assertEquals(action.target(), nextStates.iterator().next());
        }
    }

    @Test
    void testIsAcceptingFromExpression() throws Exception {
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);

        // Check that accept states are recognized
        Automaton automaton = semantics.getAutomaton();
        for (State acceptState : automaton.acceptStates()) {
            assertTrue(semantics.isAccepting(acceptState),
                    "Accept state should be recognized: " + acceptState.name());
        }

        // Check that non-accept states are not recognized as accepting
        for (State state : automaton.states()) {
            if (!automaton.acceptStates().contains(state)) {
                assertFalse(semantics.isAccepting(state),
                        "Non-accept state should not be recognized as accepting: " + state.name());
            }
        }
    }

    @Test
    void testActionsFromAutomaton() {
        State s0 = new State("s0");
        State s1 = new State("s1");

        Atom p = new Atom("p", "|");
        True trueExpr = new True();

        Transition t1 = new Transition(s0, 0, p, s1);
        Transition t2 = new Transition(s0, 0, trueExpr, s0);

        Automaton automaton = new Automaton(
                AutomatonSemanticsKind.BUCHI,
                Set.of(s0, s1),
                Set.of(s0),
                Set.of(s1),
                List.of(t1, t2)
        );

        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(automaton, evaluator);

        // With p=true, both transitions should be enabled
        Map<String, Boolean> input1 = Map.of("p", true);
        List<Transition> actions1 = semantics.actions(input1, s0);
        assertEquals(2, actions1.size());

        // With p=false, only t2 (true guard) should be enabled
        Map<String, Boolean> input2 = Map.of("p", false);
        List<Transition> actions2 = semantics.actions(input2, s0);
        assertEquals(1, actions2.size());
        assertTrue(actions2.contains(t2));
    }

    @Test
    void testExecuteFromAutomaton() {
        State s0 = new State("s0");
        State s1 = new State("s1");

        True trueExpr = new True();
        Transition t = new Transition(s0, 0, trueExpr, s1);

        Automaton automaton = new Automaton(
                AutomatonSemanticsKind.BUCHI,
                Set.of(s0, s1),
                Set.of(s0),
                Set.of(s1),
                List.of(t)
        );

        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(automaton, evaluator);

        List<State> nextStates = semantics.execute(t, Map.of(), s0);

        assertEquals(List.of(s1), nextStates);
    }

    @Test
    void testComplexExpressionSemantics() throws Exception {
        // Test: G(p -> F(q))
        Expression expr = TestHelpers.parseExpressionOrFail("G(|p| -> F(|q|))");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);

        assertNotNull(semantics.getAutomaton());
        assertFalse(semantics.initial().isEmpty());

        // Should be able to execute transitions
        State initialState = semantics.initial().iterator().next();
        Map<String, Boolean> input = Map.of("p", false, "q", false);
        List<Transition> actions = semantics.actions(input, initialState);
        assertNotNull(actions);
    }

    @Test
    void testNullElementThrowsException() {
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        assertThrows(NullPointerException.class, () -> {
            new Semantics<>(null, evaluator);
        });
    }

    @Test
    void testNullEvaluatorThrowsException() throws Exception {
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");

        assertThrows(NullPointerException.class, () -> {
            new Semantics<>(expr, null);
        });
    }

    @Test
    void testInvalidElementTypeThrowsException() {
        // Create a State (not an Automaton or Expression)
        State state = new State("test");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();

        assertThrows(Semantics.SemanticConversionException.class, () -> {
            new Semantics<>(state, evaluator);
        }, "Should throw IllegalArgumentException for State");
    }
    
    @Test
    void testInvalidElementTypeWithTransition() {
        // Create a Transition (not an Automaton or Expression)
        State s0 = new State("s0");
        State s1 = new State("s1");
        Transition transition = new Transition(s0, 0, new True(), s1);
        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        
        assertThrows(Semantics.SemanticConversionException.class, () -> {
            new Semantics<>(transition, evaluator);
        }, "Should throw IllegalArgumentException for Transition");
    }

    @Test
    void testNFAFromPropositional() {
        Expression expr = TestHelpers.parseExpressionOrFail("(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);
        AutomatonSemantics<Map<String, Boolean>> automatonSemantics = semantics.getAutomatonSemantics();
        assertNotNull(automatonSemantics);

        // Should delegate correctly
        assertEquals(semantics.initial(), automatonSemantics.initial());
        assertEquals(semantics.getAutomaton(), automatonSemantics.getAutomaton());
    }
    
    @Test
    void testGetAutomatonSemantics() throws Exception {
        Expression expr = TestHelpers.parseExpressionOrFail("F(|p|)");
        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(expr, evaluator);
        
        AutomatonSemantics<Map<String, Boolean>> automatonSemantics = semantics.getAutomatonSemantics();
        assertNotNull(automatonSemantics);
        
        // Should delegate correctly
        assertEquals(semantics.initial(), automatonSemantics.initial());
        assertEquals(semantics.getAutomaton(), automatonSemantics.getAutomaton());
    }
    
    @Test
    void testSemanticsPreservesAutomatonProperties() {
        State s0 = new State("init");
        State s1 = new State("accept");
        
        True trueExpr = new True();
        Transition t = new Transition(s0, 0, trueExpr, s1);
        
        Automaton originalAutomaton = new Automaton(
            AutomatonSemanticsKind.BUCHI,
            Set.of(s0, s1),
            Set.of(s0),
            Set.of(s1),
            List.of(t)
        );
        
        TestAtomEvaluator evaluator = new TestAtomEvaluator();
        Semantics<Map<String, Boolean>> semantics = new Semantics<>(originalAutomaton, evaluator);
        
        Automaton retrievedAutomaton = semantics.getAutomaton();
        
        // Verify all properties are preserved
        assertEquals(originalAutomaton.semanticsKind(), retrievedAutomaton.semanticsKind());
        assertEquals(originalAutomaton.states(), retrievedAutomaton.states());
        assertEquals(originalAutomaton.initialStates(), retrievedAutomaton.initialStates());
        assertEquals(originalAutomaton.acceptStates(), retrievedAutomaton.acceptStates());
        assertEquals(originalAutomaton.transitions(), retrievedAutomaton.transitions());
    }
}
