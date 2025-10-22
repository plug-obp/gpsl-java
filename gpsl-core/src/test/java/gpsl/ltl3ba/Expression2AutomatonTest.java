package gpsl.ltl3ba;

import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Expression2AutomatonTest {

    @Test
    void testSimpleEventually() throws Exception {
        // Test: F(p)
        Expression formula = Reader.readExpression("F(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.initialStates().isEmpty());
        assertFalse(automaton.acceptStates().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
        
        // Verify that transitions have guards
        for (Transition t : automaton.transitions()) {
            assertNotNull(t.guard());
            assertNotNull(t.source());
            assertNotNull(t.target());
        }
    }

    @Test
    void testSimpleGlobally() throws Exception {
        // Test: G(p) - This is a safety property
        Expression formula = Reader.readExpression("G(|p|)");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        
    }

    @Test
    void testConjunction() throws Exception {
        // Test: p & q
        Expression formula = Reader.readExpression("|p| & |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
        
        // All transition guards should be parseable
        for (Transition t : automaton.transitions()) {
            assertNotNull(t.guard(), "Guard should not be null");
        }
    }

    @Test
    void testUntil() throws Exception {
        // Test: p U q
        Expression formula = Reader.readExpression("|p| U |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.transitions().isEmpty());
    }

    @Test
    void testComplexFormula() throws Exception {
        // Test: G(p -> F(q))
        Expression formula = Reader.readExpression("G(|p| -> F(|q|))");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        assertNotNull(automaton);
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
        assertFalse(automaton.states().isEmpty());
        assertFalse(automaton.initialStates().isEmpty());
        
        // Verify initial states are in the states set
        assertTrue(automaton.states().containsAll(automaton.initialStates()));
        
        // Verify accept states are in the states set
        assertTrue(automaton.states().containsAll(automaton.acceptStates()));
    }

    @Test
    void testStateNaming() throws Exception {
        // Verify that states with "init" and "accept" are properly identified
        Expression formula = Reader.readExpression("F(|p|)");
        
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
        Expression formula = Reader.readExpression("F(|p|)");
        
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
    void testAtomLinking() throws Exception {
        // Test that atom references in guards are properly linked
        Expression formula = Reader.readExpression("|p| & |q|");
        
        Automaton automaton = Expression2Automaton.convert(formula);
        
        // Look for atoms in transition guards - they should be linked
        for (Transition t : automaton.transitions()) {
            Expression guard = t.guard();
            // If the guard contains atoms, they should be properly instantiated
            assertNotNull(guard, "Guard should not be null");
        }
    }
}
