package gpsl.ltl3ba;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LTL3BA class that converts LTL formulas to Büchi automata.
 */
class LTL3BATest {
    
    private static LTL3BA ltl3ba;
    
    @BeforeAll
    static void setUp() throws Exception {
        ltl3ba = LTL3BA.getInstance();
    }
    
    @Test
    void testSingletonPattern() throws Exception {
        LTL3BA instance1 = LTL3BA.getInstance();
        LTL3BA instance2 = LTL3BA.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }
    
    @Test
    void testConvertSimpleAtom() throws Exception {
        String result = ltl3ba.convert("p");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("acc =") || result.contains("accept"), 
            "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertNegation() throws Exception {
        String result = ltl3ba.convert("!p");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertConjunction() throws Exception {
        String result = ltl3ba.convert("p && q");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertDisjunction() throws Exception {
        String result = ltl3ba.convert("p || q");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertImplication() throws Exception {
        String result = ltl3ba.convert("p -> q");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertNext() throws Exception {
        String result = ltl3ba.convert("X p");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertEventually() throws Exception {
        String result = ltl3ba.convert("<> p");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertGlobally() throws Exception {
        String result = ltl3ba.convert("[] p");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertUntil() throws Exception {
        String result = ltl3ba.convert("p U q");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertRelease() throws Exception {
        String result = ltl3ba.convert("p R q");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertComplexFormula() throws Exception {
        // Test: [] (p -> <> q)
        String result = ltl3ba.convert("[] (p -> <> q)");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertNestedTemporalOperators() throws Exception {
        // Test: [] (<> p && <> q)
        String result = ltl3ba.convert("[] (<> p && <> q)");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertWithMultipleAtoms() throws Exception {
        String result = ltl3ba.convert("(p && q) U (r || s)");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertTrue() throws Exception {
        String result = ltl3ba.convert("true");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertFalse() throws Exception {
        String result = ltl3ba.convert("false");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertEquivalence() throws Exception {
        String result = ltl3ba.convert("p <-> q");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertWithParentheses() throws Exception {
        String result = ltl3ba.convert("((p && q) || (r && s))");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertSafetyProperty() throws Exception {
        // Safety: [] (critical1 -> !critical2)
        String result = ltl3ba.convert("[] (critical1 -> !critical2)");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertLivenessProperty() throws Exception {
        // Liveness: [] (request -> <> grant)
        String result = ltl3ba.convert("[] (request -> <> grant)");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testConvertFairnessProperty() throws Exception {
        // Fairness: [] <> enabled
        String result = ltl3ba.convert("[] <> enabled");
        assertNotNull(result);
        assertTrue(result.contains("acc =") || result.contains("accept"), "Output should contain Büchi automaton format");
    }
    
    @Test
    void testOutputContainsStates() throws Exception {
        String result = ltl3ba.convert("p U q");
        assertNotNull(result);
        // Büchi automaton output should contain state declarations
        assertTrue(result.contains("T") || result.contains("accept") || result.contains("init"), 
            "Output should contain state identifiers");
    }
    
    @Test
    void testOutputContainsTransitions() throws Exception {
        String result = ltl3ba.convert("<> p");
        assertNotNull(result);
        // Büchi automaton output should contain goto statements (transitions)
        assertTrue(result.contains(";") || result.contains(","), 
            "Output should contain transition statements");
    }
    
    @Test
    void testInvalidFormulaThrowsException() {
        // Test with invalid syntax
        assertThrows(RuntimeException.class, () -> {
            ltl3ba.convert("p && && q");
        }, "Invalid formula should throw RuntimeException");
    }
    
    @Test
    void testEmptyFormulaThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            ltl3ba.convert("");
        }, "Empty formula should throw RuntimeException");
    }
    
    @Test
    void testNullFormulaThrowsException() {
        assertThrows(Exception.class, () -> {
            ltl3ba.convert(null);
        }, "Null formula should throw an exception");
    }
}
