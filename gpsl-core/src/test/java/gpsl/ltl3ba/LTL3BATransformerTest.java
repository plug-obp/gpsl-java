package gpsl.ltl3ba;
import gpsl.syntax.TestHelpers;

import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LTL3BATransformer that converts GPSL expressions to LTL3BA format.
 */
class LTL3BATransformerTest {
    
    private LTL3BATransformer transformer;
    
    @BeforeEach
    void setUp() {
        transformer = new LTL3BATransformer();
    }
    
    @Test
    void testTransformTrue() {
        Expression expr = new True();
        String result = expr.accept(transformer, null);
        assertEquals("true", result);
    }
    
    @Test
    void testTransformFalse() {
        Expression expr = new False();
        String result = expr.accept(transformer, null);
        assertEquals("false", result);
    }
    
    @Test
    void testTransformAtom() {
        Atom atom = new Atom("p", "|");
        String result = atom.accept(transformer, null);
        assertEquals("atom0", result);
        
        // Verify mapping
        Map<String, Atom> nameToAtom = transformer.getNameToAtomMap();
        assertTrue(nameToAtom.containsKey("atom0"));
        assertEquals(atom, nameToAtom.get("atom0"));
        
        Map<Atom, String> atomToName = transformer.getAtomToNameMap();
        assertEquals("atom0", atomToName.get(atom));
    }
    
    @Test
    void testTransformMultipleAtoms() {
        Atom atom1 = new Atom("p", "|");
        Atom atom2 = new Atom("q", "|");
        Atom atom3 = new Atom("r", "|");
        
        String result1 = atom1.accept(transformer, null);
        String result2 = atom2.accept(transformer, null);
        String result3 = atom3.accept(transformer, null);
        
        assertEquals("atom0", result1);
        assertEquals("atom1", result2);
        assertEquals("atom2", result3);
        
        // Verify all mappings
        Map<String, Atom> nameToAtom = transformer.getNameToAtomMap();
        assertEquals(3, nameToAtom.size());
        assertEquals(atom1, nameToAtom.get("atom0"));
        assertEquals(atom2, nameToAtom.get("atom1"));
        assertEquals(atom3, nameToAtom.get("atom2"));
    }
    
    @Test
    void testTransformSameAtomTwice() {
        Atom atom = new Atom("p", "|");
        
        String result1 = atom.accept(transformer, null);
        String result2 = atom.accept(transformer, null);
        
        assertEquals("atom0", result1);
        assertEquals("atom0", result2);
        
        // Should only have one mapping
        Map<String, Atom> nameToAtom = transformer.getNameToAtomMap();
        assertEquals(1, nameToAtom.size());
    }
    
    @Test
    void testTransformNegation() {
        Expression expr = TestHelpers.parseExpressionOrFail("!|p|");
        String result = expr.accept(transformer, null);
        assertEquals("(!atom0)", result);
    }
    
    @Test
    void testTransformNext() {
        Expression expr = TestHelpers.parseExpressionOrFail("N |p|");
        String result = expr.accept(transformer, null);
        assertEquals("(X atom0)", result);
    }

    @Test
    void testTransformX() {
        Expression expr = TestHelpers.parseExpressionOrFail("X |p|");
        String result = expr.accept(transformer, null);
        assertEquals("(X atom0)", result);
    }
    
    @Test
    void testTransformEventually() {
        Expression expr = TestHelpers.parseExpressionOrFail("<> |p|");
        String result = expr.accept(transformer, null);
        assertEquals("(<> atom0)", result);
    }
    
    @Test
    void testTransformGlobally() {
        Expression expr = TestHelpers.parseExpressionOrFail("[] |p|");
        String result = expr.accept(transformer, null);
        assertEquals("([] atom0)", result);
    }
    
    @Test
    void testTransformConjunction() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| && |q|");
        String result = expr.accept(transformer, null);
        assertEquals("(atom0 && atom1)", result);
    }
    
    @Test
    void testTransformDisjunction() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| || |q|");
        String result = expr.accept(transformer, null);
        assertEquals("(atom0 || atom1)", result);
    }
    
    @Test
    void testTransformExclusiveDisjunction() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| xor |q|");
        String result = expr.accept(transformer, null);
        // XOR should be encoded as (!a && b) || (a && !b)
        assertEquals("((!atom0 && atom1) || (atom0 && !atom1))", result);
    }
    
    @Test
    void testTransformImplication() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| -> |q|");
        String result = expr.accept(transformer, null);
        assertEquals("(atom0 -> atom1)", result);
    }
    
    @Test
    void testTransformEquivalence() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| <-> |q|");
        String result = expr.accept(transformer, null);
        assertEquals("(atom0 <-> atom1)", result);
    }
    
    @Test
    void testTransformStrongUntil() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| U |q|");
        String result = expr.accept(transformer, null);
        assertEquals("(atom0 U atom1)", result);
    }
    
    @Test
    void testTransformWeakUntil() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| W |q|");
        String result = expr.accept(transformer, null);
        // W should be encoded as ([] a) || (a U b)
        assertEquals("(([] atom0) || (atom0 U atom1))", result);
    }
    
    @Test
    void testTransformStrongRelease() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| M |q|");
        String result = expr.accept(transformer, null);
        // M should be encoded as b U (a && b)
        assertEquals("((atom1) U (atom0 && atom1))", result);
    }
    
    @Test
    void testTransformWeakRelease() {
        Expression expr = TestHelpers.parseExpressionOrFail("|p| R |q|");
        String result = expr.accept(transformer, null);
        assertEquals("(atom0 R atom1)", result);
    }
    
    @Test
    void testTransformReference() {
        // Create a reference to an expression
        Expression targetExpr = new Atom("p", "|");
        Reference ref = new Reference("myRef");
        ref.setExpression(targetExpr);
        
        String result = ref.accept(transformer, null);
        assertEquals("atom0", result);
    }
    
    @Test
    void testTransformLetExpression() {
        // Let expression should just transform the body expression
        Expression bodyExpr = new Atom("p", "|");
        LetExpression letExpr = new LetExpression(null, bodyExpr);
        
        String result = letExpr.accept(transformer, null);
        assertEquals("atom0", result);
    }
    
    @Test
    void testTransformExpressionDeclaration() {
        // Expression declaration should just transform the expression
        Expression expr = new Atom("p", "|");
        ExpressionDeclaration decl = new ExpressionDeclaration("myDecl", expr);
        
        String result = decl.accept(transformer, null);
        assertEquals("atom0", result);
    }
    
    @Test
    void testTransformComplexExpression() {
        // Test: [] (|p| -> <> |q|)
        Expression expr = TestHelpers.parseExpressionOrFail("[] (|p| -> <> |q|)");
        String result = expr.accept(transformer, null);
        assertEquals("([] (atom0 -> (<> atom1)))", result);
    }
    
    @Test
    void testTransformNestedExpression() {
        // Test: (|p| && |q|) U (|r| || |s|)
        Expression expr = TestHelpers.parseExpressionOrFail("(|p| && |q|) U (|r| || |s|)");
        String result = expr.accept(transformer, null);
        assertEquals("((atom0 && atom1) U (atom2 || atom3))", result);
    }
    
    @Test
    void testTransformComplexTemporalFormula() {
        // Test: [] (<> |p| -> X |q|)
        Expression expr = TestHelpers.parseExpressionOrFail("[] (<> |p| -> N |q|)");
        String result = expr.accept(transformer, null);
        assertEquals("([] ((<> atom0) -> (X atom1)))", result);
    }
    
    @Test
    void testTransformWithMixedOperators() {
        // Test: (|p| xor |q|) && (|r| W |s|)
        Expression expr = TestHelpers.parseExpressionOrFail("(|p| xor |q|) && (|r| W |s|)");
        String result = expr.accept(transformer, null);
        // XOR encoded and W encoded
        assertEquals("(((!atom0 && atom1) || (atom0 && !atom1)) && (([] atom2) || (atom2 U atom3)))", result);
    }
    
    @Test
    void testAtomMappingConsistency() {
        // Test that atoms are mapped consistently across multiple transformations
        Expression expr1 = TestHelpers.parseExpressionOrFail("|p| && |q|");
        Expression expr2 = TestHelpers.parseExpressionOrFail("|p| || |r|");
        
        String result1 = expr1.accept(transformer, null);
        String result2 = expr2.accept(transformer, null);
        
        // |p| should be atom0 in both, |q| should be atom1, |r| should be atom2
        assertEquals("(atom0 && atom1)", result1);
        assertEquals("(atom0 || atom2)", result2);
        
        Map<String, Atom> nameToAtom = transformer.getNameToAtomMap();
        assertEquals(3, nameToAtom.size());
    }
    
    @Test
    void testUnsupportedElement() {
        // Test that unsupported elements throw an exception
        State state = new State("s0");
        
        assertThrows(UnsupportedOperationException.class, () -> {
            state.accept(transformer, null);
        });
    }
    
    @Test
    void testTruthValues() {
        // Test: true && false
        Expression expr = TestHelpers.parseExpressionOrFail("true && false");
        String result = expr.accept(transformer, null);
        assertEquals("(true && false)", result);
    }
    
    @Test
    void testMixedTruthValuesAndAtoms() {
        // Test: true && |p| || false
        Expression expr = TestHelpers.parseExpressionOrFail("(true && |p|) || false");
        String result = expr.accept(transformer, null);
        assertEquals("((true && atom0) || false)", result);
    }
}
