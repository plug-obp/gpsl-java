package gpsl.syntax;

import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static gpsl.syntax.TestHelpers.*;

/**
 * Tests for the Antlr4ToGPSLMapper that builds GPSL syntax models from ANTLR4 parse trees.
 */
class Antlr4ToGPSLMapperTest {

    @Test
    void testLiteral() {
        assertEquals(new True(), parseExpressionOrFail("true"));
        assertEquals(new False(), parseExpressionOrFail("false"));
    }

    @Test
    void testReference() {
        Reference ref1 = (Reference) parseExpressionWithoutResolution("x");
        assertEquals("x", ref1.name());
        
        Reference ref2 = (Reference) parseExpressionWithoutResolution("zm");
        assertEquals("zm", ref2.name());
    }

    @Test
    void testParen() {
        assertEquals(new True(), parseExpressionOrFail("(true)"));
        
        Expression parenRef = parseExpressionWithoutResolution("(zm)");
        assertInstanceOf(Reference.class, parenRef);
        assertEquals("zm", ((Reference) parenRef).name());
    }

    @Test
    void testAtom() {
        Atom atom1 = (Atom) parseExpressionOrFail("|x|");
        assertEquals("x", atom1.value());
        assertEquals("|", atom1.delimiter());

        Atom atom2 = (Atom) parseExpressionOrFail("|a-b|");
        assertEquals("a-b", atom2.value());
        assertEquals("|", atom2.delimiter());

        Atom atom3 = (Atom) parseExpressionOrFail("\"a>2\"");
        assertEquals("a>2", atom3.value());
        assertEquals("\"", atom3.delimiter());
    }

    @Test
    void testAtomEscaping() {
        Atom atom1 = (Atom) parseExpressionOrFail("|to\\|to|");
        assertEquals("to|to", atom1.value());
        assertEquals("|", atom1.delimiter());

        Atom atom2 = (Atom) parseExpressionOrFail("\"to\\\"to\"");
        assertEquals("to\"to", atom2.value());
        assertEquals("\"", atom2.delimiter());
    }

    @Test
    void testUnaryNegation() {
        Negation neg1 = (Negation) parseExpressionOrFail("!true");
        assertEquals("!", neg1.operator());
        assertInstanceOf(True.class, neg1.expression());

        Negation neg2 = (Negation) parseExpressionOrFail("!false");
        assertEquals("!", neg2.operator());
        assertInstanceOf(False.class, neg2.expression());

        Negation neg3 = (Negation) parseExpressionWithoutResolution("!x");
        assertEquals("!", neg3.operator());
        assertInstanceOf(Reference.class, neg3.expression());
        assertEquals("x", ((Reference) neg3.expression()).name());

        Negation neg4 = (Negation) parseExpressionOrFail("!!true");
        assertEquals("!", neg4.operator());
        assertInstanceOf(Negation.class, neg4.expression());
        Negation innerNeg = (Negation) neg4.expression();
        assertEquals("!", innerNeg.operator());
        assertInstanceOf(True.class, innerNeg.expression());
    }

    @Test
    void testUnaryNext() {
        Next next1 = (Next) parseExpressionOrFail("N true");
        assertEquals("N", next1.operator());
        assertInstanceOf(True.class, next1.expression());

        Next next2 = (Next) parseExpressionOrFail("◯ false");
        assertEquals("◯", next2.operator());
        assertInstanceOf(False.class, next2.expression());

        Next next3 = (Next) parseExpressionWithoutResolution("next x");
        assertEquals("next", next3.operator());
        assertInstanceOf(Reference.class, next3.expression());

        Next next4 = (Next) parseExpressionOrFail("◯◯ true");
        assertEquals("◯", next4.operator());
        assertInstanceOf(Next.class, next4.expression());

        Next next5 = (Next) parseExpressionWithoutResolution("X x");
        assertEquals("X", next5.operator());
        assertInstanceOf(Reference.class, next5.expression());
    }

    @Test
    void testUnaryEventually() {
        Eventually ev1 = (Eventually) parseExpressionOrFail("F true");
        assertEquals("F", ev1.operator());
        assertInstanceOf(True.class, ev1.expression());

        Eventually ev2 = (Eventually) parseExpressionOrFail("<> false");
        assertEquals("<>", ev2.operator());
        assertInstanceOf(False.class, ev2.expression());
    }

    @Test
    void testUnaryGlobally() {
        Globally glob1 = (Globally) parseExpressionOrFail("G true");
        assertEquals("G", glob1.operator());
        assertInstanceOf(True.class, glob1.expression());

        Globally glob2 = (Globally) parseExpressionOrFail("[] false");
        assertEquals("[]", glob2.operator());
        assertInstanceOf(False.class, glob2.expression());
    }

    @Test
    void testBinaryConjunction() {
        Conjunction conj = (Conjunction) parseExpressionOrFail("true and false");
        assertEquals("and", conj.operator());
        assertInstanceOf(True.class, conj.left());
        assertInstanceOf(False.class, conj.right());
    }

    @Test
    void testBinaryDisjunction() {
        Disjunction disj = (Disjunction) parseExpressionOrFail("true or false");
        assertEquals("or", disj.operator());
        assertInstanceOf(True.class, disj.left());
        assertInstanceOf(False.class, disj.right());
    }

    @Test
    void testBinaryXor() {
        ExclusiveDisjunction xor = (ExclusiveDisjunction) parseExpressionOrFail("true xor false");
        assertEquals("xor", xor.operator());
        assertInstanceOf(True.class, xor.left());
        assertInstanceOf(False.class, xor.right());
    }

    @Test
    void testBinaryImplication() {
        Implication impl = (Implication) parseExpressionOrFail("true -> false");
        assertEquals("->", impl.operator());
        assertInstanceOf(True.class, impl.left());
        assertInstanceOf(False.class, impl.right());
    }

    @Test
    void testBinaryEquivalence() {
        Equivalence equiv = (Equivalence) parseExpressionOrFail("true <-> false");
        assertEquals("<->", equiv.operator());
        assertInstanceOf(True.class, equiv.left());
        assertInstanceOf(False.class, equiv.right());
    }

    @Test
    void testBinaryStrongUntil() {
        StrongUntil until = (StrongUntil) parseExpressionOrFail("true U false");
        assertEquals("U", until.operator());
        assertInstanceOf(True.class, until.left());
        assertInstanceOf(False.class, until.right());
    }

    @Test
    void testBinaryWeakUntil() {
        WeakUntil wuntil = (WeakUntil) parseExpressionOrFail("true W false");
        assertEquals("W", wuntil.operator());
        assertInstanceOf(True.class, wuntil.left());
        assertInstanceOf(False.class, wuntil.right());
    }

    @Test
    void testBinaryStrongRelease() {
        StrongRelease rel = (StrongRelease) parseExpressionOrFail("true M false");
        assertEquals("M", rel.operator());
        assertInstanceOf(True.class, rel.left());
        assertInstanceOf(False.class, rel.right());
    }

    @Test
    void testBinaryWeakRelease() {
        WeakRelease wrel = (WeakRelease) parseExpressionOrFail("true R false");
        assertEquals("R", wrel.operator());
        assertInstanceOf(True.class, wrel.left());
        assertInstanceOf(False.class, wrel.right());
    }

    @Test
    void testLetExpression() {
        LetExpression let = (LetExpression) parseExpressionOrFail("let x = true in x");
        assertNotNull(let.declarations());
        assertEquals(1, let.declarations().declarations().size());
        
        ExpressionDeclaration decl = let.declarations().declarations().get(0);
        assertEquals("x", decl.name());
        assertInstanceOf(True.class, decl.expression());
        
        assertInstanceOf(Reference.class, let.expression());
        assertEquals("x", ((Reference) let.expression()).name());
    }

    @Test
    void testDeclarations() {
        Declarations decls = parseDeclarationsOrFail("a = true b = false");
        assertEquals(2, decls.declarations().size());
        
        ExpressionDeclaration decl1 = decls.declarations().get(0);
        assertEquals("a", decl1.name());
        assertInstanceOf(True.class, decl1.expression());
        assertTrue(decl1.isInternal());
        
        ExpressionDeclaration decl2 = decls.declarations().get(1);
        assertEquals("b", decl2.name());
        assertInstanceOf(False.class, decl2.expression());
        assertTrue(decl2.isInternal());
    }

    @Test
    void testExternalDeclaration() {
        Declarations decls = parseDeclarationsOrFail("a *= true");
        assertEquals(1, decls.declarations().size());
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        assertEquals("a", decl.name());
        assertInstanceOf(True.class, decl.expression());
        assertFalse(decl.isInternal());
    }

    @Test
    void testComplexExpression() {
        Expression expr = parseExpressionWithoutResolution("!x and (y or z)");
        assertInstanceOf(Conjunction.class, expr);
        
        Conjunction conj = (Conjunction) expr;
        assertInstanceOf(Negation.class, conj.left());
        assertInstanceOf(Disjunction.class, conj.right());
    }
}
