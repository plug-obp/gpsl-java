package gpsl.semantics;

import gpsl.syntax.TestHelpers;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the IsPropositional visitor that determines if an expression
 * contains only propositional logic (no temporal operators).
 */
class IsPropositionalTest {

    private IsPropositional visitor;

    @BeforeEach
    void setUp() {
        visitor = new IsPropositional();
    }

    @Test
    void testLiteralTrue() {
        Expression expr = new True();
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testLiteralFalse() {
        Expression expr = new False();
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testAtom() {
        Atom atom = new Atom("p", "|");
        assertTrue(atom.accept(visitor, null));
    }

    @Test
    void testNegation() {
        Expression expr = TestHelpers.parseExpressionOrFail("!|p|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testConjunction() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| && |q|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testDisjunction() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| || |q|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testXor() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| xor |q|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testImplication() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| -> |q|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testEquivalence() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| <-> |q|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testConditionalPropositional() {
        // Test: |p| ? |q| : |r| - all propositional, should return true
        Expression expr = TestHelpers.parseExpressionOrFail("|p| ? |q| : |r|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testConditionalWithLiterals() {
        // Test: true ? false : true - all propositional
        Expression expr = TestHelpers.parseExpressionOrFail("true ? false : true");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testConditionalNested() {
        // Test: |p| ? (|q| ? |r| : |s|) : |t| - all propositional
        Expression expr = TestHelpers.parseExpressionOrFail("|p| ? (|q| ? |r| : |s|) : |t|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testConditionalWithLogicalOperators() {
        // Test: (|p| && |q|) ? (|r| || |s|) : |t| - all propositional
        Expression expr = TestHelpers.parseExpressionOrFail("(|p| && |q|) ? (|r| || |s|) : |t|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testConditionalWithTemporalInCondition() {
        // Test: [] |p| ? |q| : |r| - temporal in condition, should return false
        Expression expr = TestHelpers.parseExpressionOrFail("[] |p| ? |q| : |r|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testConditionalWithTemporalInTrueBranch() {
        // Test: |p| ? <> |q| : |r| - temporal in true branch, should return false
        Expression expr = TestHelpers.parseExpressionOrFail("|p| ? <> |q| : |r|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testConditionalWithTemporalInFalseBranch() {
        // Test: |p| ? |q| : X |r| - temporal in false branch, should return false
        Expression expr = TestHelpers.parseExpressionOrFail("|p| ? |q| : X |r|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testConditionalWithTemporalInMultipleBranches() {
        // Test: [] |p| ? <> |q| : X |r| - temporal in all branches, should return false
        Expression expr = TestHelpers.parseExpressionOrFail("[] |p| ? <> |q| : X |r|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testComplexPropositional() {
        // Test: (|p| && |q|) || (|r| -> |s|)
        Expression expr = TestHelpers.parseExpressionOrFail("(|p| && |q|) || (|r| -> |s|)");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testNext() {
        Expression expr = TestHelpers.parseExpressionOrFail("N |p|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testEventually() {
        Expression expr = TestHelpers.parseExpressionOrFail("<> |p|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testGlobally() {
        Expression expr = TestHelpers.parseExpressionOrFail("[] |p|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testStrongUntil() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| U |q|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testWeakUntil() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| W |q|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testStrongRelease() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| M |q|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testWeakRelease() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| R |q|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testPropositionalWithNestedTemporal() {
        // Test: |p| && ([] |q|) - propositional AND temporal
        Expression expr = TestHelpers.parseExpressionOrFail("|p| && ([] |q|)");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testReference() {
        // Test reference to propositional expression
        Expression targetExpr = new Atom("p", "|");
        Reference ref = new Reference("myRef");
        ref.setExpression(targetExpr);

        assertTrue(ref.accept(visitor, null));
    }

    @Test
    void testReferenceToTemporal() {
        // Test reference to temporal expression
        Expression targetExpr = TestHelpers.parseExpressionOrFail("[] |p|");
        Reference ref = new Reference("myRef");
        ref.setExpression(targetExpr);

        assertFalse(ref.accept(visitor, null));
    }

    @Test
    void testLetExpression() {
        // Test: let x = |p| in x && |q|
        Expression expr = TestHelpers.parseExpressionOrFail("let x = |p| in x && |q|");
        assertTrue(expr.accept(visitor, null));
    }

    @Test
    void testLetExpressionWithTemporal() {
        // Test: let x = [] |p| in x && |q|
        Expression expr = TestHelpers.parseExpressionOrFail("let x = [] |p| in x && |q|");
        assertFalse(expr.accept(visitor, null));
    }

    @Test
    void testExpressionDeclaration() {
        // Test expression declaration with propositional
        Expression expr = new Atom("p", "|");
        ExpressionDeclaration decl = new ExpressionDeclaration("myDecl", expr);

        assertTrue(decl.accept(visitor, null));
    }

    @Test
    void testExpressionDeclarationWithTemporal() {
        // Test expression declaration with temporal
        Expression expr = TestHelpers.parseExpressionOrFail("[] |p|");
        ExpressionDeclaration decl = new ExpressionDeclaration("myDecl", expr);

        assertFalse(decl.accept(visitor, null));
    }
}

