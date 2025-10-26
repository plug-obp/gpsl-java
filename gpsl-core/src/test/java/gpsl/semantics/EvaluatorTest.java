package gpsl.semantics;
import gpsl.syntax.TestHelpers;

import static gpsl.syntax.TestHelpers.parseDeclarationsOrFail;

import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GPSL Evaluator that evaluates boolean expressions.
 */
class EvaluatorTest {

    // Simple atom evaluator that checks if atom value equals "true"
    private static final AtomEvaluator<Void> SIMPLE_ATOM_EVALUATOR = 
        (atomValue, input) -> atomValue.equals("true");
    
    // Context-aware atom evaluator that looks up values in a map
    private static final AtomEvaluator<Map<String, Boolean>> MAP_ATOM_EVALUATOR = 
        (atomValue, context) -> context.getOrDefault(atomValue, false);
    
    @Test
    void testEvaluateLiteralTrue() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        Expression expr = TestHelpers.parseExpressionOrFail("true");
        
        assertTrue(evaluator.visitTrue((True) expr, null));
    }
    
    @Test
    void testEvaluateLiteralFalse() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        Expression expr = TestHelpers.parseExpressionOrFail("false");
        
        assertFalse(evaluator.visitFalse((False) expr, null));
    }
    
    @Test
    void testEvaluateAtomTrue() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        Expression expr = TestHelpers.parseExpressionOrFail("|true|");
        
        assertTrue(expr.accept(evaluator, null));
    }
    
    @Test
    void testEvaluateAtomFalse() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        Expression expr = TestHelpers.parseExpressionOrFail("|false|");
        
        assertFalse(expr.accept(evaluator, null));
    }
    
    @Test
    void testEvaluateAtomWithContext() {
        Evaluator<Map<String, Boolean>> evaluator = new Evaluator<>(MAP_ATOM_EVALUATOR);
        
        Map<String, Boolean> context = new HashMap<>();
        context.put("x > 0", true);
        context.put("y < 10", false);
        
        Expression expr1 = TestHelpers.parseExpressionOrFail("|x > 0|");
        assertTrue(expr1.accept(evaluator, context));
        
        Expression expr2 = TestHelpers.parseExpressionOrFail("|y < 10|");
        assertFalse(expr2.accept(evaluator, context));
    }
    
    @Test
    void testEvaluateNegation() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        Expression expr1 = TestHelpers.parseExpressionOrFail("!true");
        assertFalse(expr1.accept(evaluator, null));
        
        Expression expr2 = TestHelpers.parseExpressionOrFail("!false");
        assertTrue(expr2.accept(evaluator, null));
        
        Expression expr3 = TestHelpers.parseExpressionOrFail("!!true");
        assertTrue(expr3.accept(evaluator, null));
    }
    
    @Test
    void testEvaluateConjunction() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        assertTrue(TestHelpers.parseExpressionOrFail("true and true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("true and false").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("false and true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("false and false").accept(evaluator, null));
    }
    
    @Test
    void testEvaluateDisjunction() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        assertTrue(TestHelpers.parseExpressionOrFail("true or true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true or false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("false or true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("false or false").accept(evaluator, null));
    }
    
    @Test
    void testEvaluateExclusiveDisjunction() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        assertFalse(TestHelpers.parseExpressionOrFail("true xor true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true xor false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("false xor true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("false xor false").accept(evaluator, null));
    }
    
    @Test
    void testEvaluateImplication() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        assertTrue(TestHelpers.parseExpressionOrFail("true -> true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("true -> false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("false -> true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("false -> false").accept(evaluator, null));
    }
    
    @Test
    void testEvaluateEquivalence() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        assertTrue(TestHelpers.parseExpressionOrFail("true <-> true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("true <-> false").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("false <-> true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("false <-> false").accept(evaluator, null));
    }
    
    @Test
    void testEvaluateComplexExpression() {
        Evaluator<Map<String, Boolean>> evaluator = new Evaluator<>(MAP_ATOM_EVALUATOR);
        
        Map<String, Boolean> context = new HashMap<>();
        context.put("a", true);
        context.put("b", false);
        context.put("c", true);
        
        // (a and b) or c
        Expression expr1 = TestHelpers.parseExpressionOrFail("(|a| and |b|) or |c|");
        assertTrue(expr1.accept(evaluator, context));
        
        // a and (b or c)
        Expression expr2 = TestHelpers.parseExpressionOrFail("|a| and (|b| or |c|)");
        assertTrue(expr2.accept(evaluator, context));
        
        // !(a and !b)
        Expression expr3 = TestHelpers.parseExpressionOrFail("!(|a| and !|b|)");
        assertFalse(expr3.accept(evaluator, context));
    }
    
    @Test
    void testEvaluateWithReferences() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        String input = "x = |true| result = x and !x";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration resultDecl = decls.declarations().get(1);
        assertFalse(resultDecl.expression().accept(evaluator, null));
    }
    
    @Test
    void testEvaluateWithLetExpression() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        // Let expressions need to be wrapped in a declaration and linked
        String input = "result = let x = |true| in x and true";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration resultDecl = decls.declarations().get(0);
        assertTrue(resultDecl.expression().accept(evaluator, null));
    }
    
    @Test
    void testEvaluateLetExpressionWithReferences() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        String input = "result = let x = |true|, y = |false| in x or y";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration resultDecl = decls.declarations().get(0);
        assertTrue(resultDecl.expression().accept(evaluator, null));
    }
    
    @Test
    void testEvaluateUnresolvedReferenceThrows() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        Reference unresolved = new Reference("unresolved");
        assertThrows(Evaluator.EvaluationException.class, 
            () -> evaluator.visitReference(unresolved, null));
    }
    
    @Test
    void testEvaluateExpressionDeclaration() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        String input = "x = true and false";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        assertFalse(decl.accept(evaluator, null));
    }
    
    @Test
    void testTemporalOperatorsThrowUnsupportedOperation() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("N true").accept(evaluator, null));
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("F true").accept(evaluator, null));
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("G true").accept(evaluator, null));
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("true U false").accept(evaluator, null));
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("true W false").accept(evaluator, null));
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("true M false").accept(evaluator, null));
        
        assertThrows(UnsupportedOperationException.class,
            () -> TestHelpers.parseExpressionOrFail("true R false").accept(evaluator, null));
    }
    
    @Test
    void testAutomatonThrowsUnsupportedOperation() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        String input = "a = states s0; initial s0; accept s0; s0 [true] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        
        assertThrows(UnsupportedOperationException.class,
            () -> letExpr.expression().accept(evaluator, null));
    }
    
    @Test
    void testDeclarationsThrowUnsupportedOperation() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        Declarations decls = TestHelpers.parseDeclarationsOrFail("x = true y = false");
        
        assertThrows(UnsupportedOperationException.class,
            () -> decls.accept(evaluator, null));
    }
    
    @Test
    void testShortCircuitEvaluationConjunction() {
        // Create an atom evaluator that tracks which atoms are evaluated
        Map<String, Boolean> evaluatedAtoms = new HashMap<>();
        AtomEvaluator<Void> trackingEvaluator = (atomValue, input) -> {
            evaluatedAtoms.put(atomValue, true);
            return atomValue.equals("true");
        };
        
        Evaluator<Void> evaluator = new Evaluator<>(trackingEvaluator);
        
        // false and |never-evaluated| should short-circuit
        evaluatedAtoms.clear();
        Expression expr = TestHelpers.parseExpressionOrFail("false and |never-evaluated|");
        assertFalse(expr.accept(evaluator, null));
        assertFalse(evaluatedAtoms.containsKey("never-evaluated"));
    }
    
    @Test
    void testShortCircuitEvaluationDisjunction() {
        Map<String, Boolean> evaluatedAtoms = new HashMap<>();
        AtomEvaluator<Void> trackingEvaluator = (atomValue, input) -> {
            evaluatedAtoms.put(atomValue, true);
            return atomValue.equals("true");
        };
        
        Evaluator<Void> evaluator = new Evaluator<>(trackingEvaluator);
        
        // true or |never-evaluated| should short-circuit
        evaluatedAtoms.clear();
        Expression expr = TestHelpers.parseExpressionOrFail("true or |never-evaluated|");
        assertTrue(expr.accept(evaluator, null));
        assertFalse(evaluatedAtoms.containsKey("never-evaluated"));
    }
    
    @Test
    void testRealWorldExample() {
        // Evaluate a safety property: mutual exclusion
        Evaluator<Map<String, Boolean>> evaluator = new Evaluator<>(MAP_ATOM_EVALUATOR);
        
        String input = """
            alice_cs = |alice_in_critical_section|
            bob_cs = |bob_in_critical_section|
            mutex = !(alice_cs and bob_cs)
            """;
        
        Declarations decls = parseDeclarationsOrFail(input);
        
        // Test case 1: Both in critical section (violation)
        Map<String, Boolean> state1 = new HashMap<>();
        state1.put("alice_in_critical_section", true);
        state1.put("bob_in_critical_section", true);
        
        ExpressionDeclaration mutexDecl = decls.declarations().get(2);
        assertFalse(mutexDecl.expression().accept(evaluator, state1));
        
        // Test case 2: Only Alice in critical section (ok)
        Map<String, Boolean> state2 = new HashMap<>();
        state2.put("alice_in_critical_section", true);
        state2.put("bob_in_critical_section", false);
        
        assertTrue(mutexDecl.expression().accept(evaluator, state2));
        
        // Test case 3: Neither in critical section (ok)
        Map<String, Boolean> state3 = new HashMap<>();
        state3.put("alice_in_critical_section", false);
        state3.put("bob_in_critical_section", false);
        
        assertTrue(mutexDecl.expression().accept(evaluator, state3));
    }
    
    @Test
    void testAllOperatorVariants() {
        Evaluator<Void> evaluator = new Evaluator<>(SIMPLE_ATOM_EVALUATOR);
        
        // Test all conjunction variants
        assertTrue(TestHelpers.parseExpressionOrFail("true & true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true && true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true /\\ true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true * true").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true ∧ true").accept(evaluator, null));
        
        // Test all disjunction variants
        assertTrue(TestHelpers.parseExpressionOrFail("true | false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true || false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true \\/ false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true + false").accept(evaluator, null));
        assertTrue(TestHelpers.parseExpressionOrFail("true ∨ false").accept(evaluator, null));
        
        // Test all negation variants
        assertFalse(TestHelpers.parseExpressionOrFail("!true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("~true").accept(evaluator, null));
        assertFalse(TestHelpers.parseExpressionOrFail("not true").accept(evaluator, null));
    }
}
