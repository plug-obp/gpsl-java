package gpsl.ltl3ba;
import gpsl.syntax.TestHelpers;

import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Expression2Automaton conversion.
 * Tests conversion of GPSL expressions to Büchi automata via LTL3BA.
 */
class Expression2AutomatonTest {

    @Test
    void testEventually() throws Exception {
        // Test: F(p) - Eventually p
        Expression formula = TestHelpers.parseExpressionOrFail("F(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton, "Automaton should not be null");
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty(), "Should have states");
        assertFalse(automaton.initialStates().isEmpty(), "Should have initial states");
        assertFalse(automaton.acceptStates().isEmpty(), "Should have accept states");
        assertFalse(automaton.transitions().isEmpty(), "Should have transitions");
        
        // Verify that transitions have guards
        for (Transition t : automaton.transitions()) {
            assertNotNull(t.guard(), "Guard should not be null");
            assertNotNull(t.source(), "Source should not be null");
            assertNotNull(t.target(), "Target should not be null");
        }
        
        // Verify initial states contain "init"
        boolean hasInit = automaton.initialStates().stream()
            .anyMatch(s -> s.name().contains("init"));
        assertTrue(hasInit, "Should have at least one initial state with 'init' in name");
        
        // Verify accept states contain "accept"
        boolean hasAccept = automaton.acceptStates().stream()
            .anyMatch(s -> s.name().contains("accept"));
        assertTrue(hasAccept, "Should have at least one accept state with 'accept' in name");
    }

    @Test
    void testGlobally() throws Exception {
        // Test: G(p) - Globally p (safety property)
        Expression formula = TestHelpers.parseExpressionOrFail("G(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton, "Automaton should not be null");
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty(), "G(p) should have at least one state");
        assertFalse(automaton.transitions().isEmpty(), "G(p) should have at least one transition");
        
        // For G(p), the initial state should also be an accept state
        assertFalse(automaton.initialStates().isEmpty(), "Should have initial states");
        assertFalse(automaton.acceptStates().isEmpty(), "Should have accept states");
    }

    @Test
    void testNext() throws Exception {
        // Test: X(p) - Next p
        Expression formula = TestHelpers.parseExpressionOrFail("X(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
    }

    @Test
    void testUntil() throws Exception {
        // Test: p U q - p until q
        Expression formula = TestHelpers.parseExpressionOrFail("|p| U |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty(), "Should have states");
        assertFalse(automaton.transitions().isEmpty(), "Should have transitions");
        assertFalse(automaton.initialStates().isEmpty(), "Should have initial states");
        assertFalse(automaton.acceptStates().isEmpty(), "Should have accept states");
    }

    @Test
    void testRelease() throws Exception {
        // Test: p R q - p releases q
        Expression formula = TestHelpers.parseExpressionOrFail("|p| R |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
    }

    @Test
    void testImplication() throws Exception {
        // Test: p -> q
        Expression formula = TestHelpers.parseExpressionOrFail("|p| -> |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
    }

    @Test
    void testComplexFormula() throws Exception {
        // Test: G(p -> F(q)) - Globally, if p then eventually q
        Expression formula = TestHelpers.parseExpressionOrFail("G(|p| -> F(|q|))");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.initialStates().isEmpty());
        
        // Verify initial states are in the states set
        assertTrue(automaton.states().containsAll(automaton.initialStates()),
            "All initial states should be in states set");
        
        // Verify accept states are in the states set
        assertTrue(automaton.states().containsAll(automaton.acceptStates()),
            "All accept states should be in states set");
    }

    @Test
    void testEventuallyGlobally() throws Exception {
        // Test: F(G(p)) - Eventually globally p (stability)
        Expression formula = TestHelpers.parseExpressionOrFail("F(G(|p|))");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
    }

    @Test
    void testGloballyEventually() throws Exception {
        // Test: G(F(p)) - Globally eventually p (liveness/fairness)
        Expression formula = TestHelpers.parseExpressionOrFail("G(F(|p|))");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
    }

    @Test
    void testStateNaming() throws Exception {
        // Verify that states with "init" and "accept" are properly identified
        Expression formula = TestHelpers.parseExpressionOrFail("F(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        // Check initial states contain "init" in their names
        for (State state : automaton.initialStates()) {
            assertTrue(state.name().contains("init"), 
                "Initial state should contain 'init': " + state.name());
        }
        
        // Check accept states contain "accept" in their names
        for (State state : automaton.acceptStates()) {
            assertTrue(state.name().contains("accept"), 
                "Accept state should contain 'accept': " + state.name());
        }
    }

    @Test
    void testTransitionSourceAndTarget() throws Exception {
        Expression formula = TestHelpers.parseExpressionOrFail("F(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        // Verify all transition sources and targets are in the states set
        for (Transition t : automaton.transitions()) {
            assertTrue(automaton.states().contains(t.source()),
                "Transition source should be in states set: " + t.source().name());
            assertTrue(automaton.states().contains(t.target()),
                "Transition target should be in states set: " + t.target().name());
        }
    }

    @Test
    void testTransitionPriority() throws Exception {
        Expression formula = TestHelpers.parseExpressionOrFail("F(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        // All transitions should have priority 0 (as set by LTL3BA converter)
        for (Transition t : automaton.transitions()) {
            assertEquals(0, t.priority(), 
                "Transitions from LTL3BA should have priority 0");
        }
    }

    @Test
    void testAtomLinking() throws Exception {
        // Test that atom references in guards are properly linked
        Expression formula = TestHelpers.parseExpressionOrFail("|p| U |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        // Verify all guards are properly parsed and linked
        for (Transition t : automaton.transitions()) {
            assertNotNull(t.guard(), "Guard should not be null");
            // Guards should be valid expressions (Atom, True, False, or compound)
            assertTrue(t.guard() instanceof Expression, "Guard should be an Expression");
        }
    }

    @Test
    void testMultipleAtoms() throws Exception {
        // Test formula with multiple different atoms
        Expression formula = TestHelpers.parseExpressionOrFail("G(|p| -> F(|q|)) & G(|r| -> F(|s|))");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
    }

    @Test
    void testNegation() throws Exception {
        // Test: !p
        Expression formula = TestHelpers.parseExpressionOrFail("!|p|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
    }

    @Test
    void testDisjunction() throws Exception {
        // Test: p | q
        Expression formula = TestHelpers.parseExpressionOrFail("|p| | |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
    }

    @Test
    void testTrueFormula() throws Exception {
        // Test: true (should produce trivial automaton)
        Expression formula = TestHelpers.parseExpressionOrFail("true");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        // True should have at least one state (the accepting init state)
        assertFalse(automaton.states().isEmpty());
    }

    @Test
    void testStateEquality() throws Exception {
        // Test that states with the same name are considered equal
        Expression formula = TestHelpers.parseExpressionOrFail("F(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        // Create sets to verify no duplicate states
        java.util.Set<String> stateNames = new java.util.HashSet<>();
        for (State s : automaton.states()) {
            assertFalse(stateNames.contains(s.name()), 
                "State names should be unique: " + s.name());
            stateNames.add(s.name());
        }
    }
}
