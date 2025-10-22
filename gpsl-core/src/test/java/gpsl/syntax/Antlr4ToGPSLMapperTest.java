package gpsl.syntax;

import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Antlr4ToGPSLMapper that builds GPSL syntax models from ANTLR4 parse trees.
 */
class Antlr4ToGPSLMapperTest {

    @Test
    void testLiteral() {
        assertEquals(new True(), Reader.readExpression("true"));
        assertEquals(new False(), Reader.readExpression("false"));
    }

    @Test
    void testReference() {
        Reference ref1 = (Reference) Reader.readExpression("x");
        assertEquals("x", ref1.name());
        
        Reference ref2 = (Reference) Reader.readExpression("zm");
        assertEquals("zm", ref2.name());
    }

    @Test
    void testParen() {
        assertEquals(new True(), Reader.readExpression("(true)"));
        
        Expression parenRef = Reader.readExpression("(zm)");
        assertInstanceOf(Reference.class, parenRef);
        assertEquals("zm", ((Reference) parenRef).name());
    }

    @Test
    void testAtom() {
        Atom atom1 = (Atom) Reader.readExpression("|x|");
        assertEquals("x", atom1.value());
        assertEquals("|", atom1.delimiter());

        Atom atom2 = (Atom) Reader.readExpression("|a-b|");
        assertEquals("a-b", atom2.value());
        assertEquals("|", atom2.delimiter());

        Atom atom3 = (Atom) Reader.readExpression("\"a>2\"");
        assertEquals("a>2", atom3.value());
        assertEquals("\"", atom3.delimiter());
    }

    @Test
    void testAtomEscaping() {
        Atom atom1 = (Atom) Reader.readExpression("|to\\|to|");
        assertEquals("to|to", atom1.value());
        assertEquals("|", atom1.delimiter());

        Atom atom2 = (Atom) Reader.readExpression("\"to\\\"to\"");
        assertEquals("to\"to", atom2.value());
        assertEquals("\"", atom2.delimiter());
    }

    @Test
    void testUnaryNegation() {
        Negation neg1 = (Negation) Reader.readExpression("!true");
        assertEquals("!", neg1.operator());
        assertInstanceOf(True.class, neg1.expression());

        Negation neg2 = (Negation) Reader.readExpression("!false");
        assertEquals("!", neg2.operator());
        assertInstanceOf(False.class, neg2.expression());

        Negation neg3 = (Negation) Reader.readExpression("!x");
        assertEquals("!", neg3.operator());
        assertInstanceOf(Reference.class, neg3.expression());
        assertEquals("x", ((Reference) neg3.expression()).name());

        Negation neg4 = (Negation) Reader.readExpression("!!true");
        assertEquals("!", neg4.operator());
        assertInstanceOf(Negation.class, neg4.expression());
        Negation innerNeg = (Negation) neg4.expression();
        assertEquals("!", innerNeg.operator());
        assertInstanceOf(True.class, innerNeg.expression());
    }

    @Test
    void testUnaryNext() {
        Next next1 = (Next) Reader.readExpression("N true");
        assertEquals("N", next1.operator());
        assertInstanceOf(True.class, next1.expression());

        Next next2 = (Next) Reader.readExpression("◯ false");
        assertEquals("◯", next2.operator());
        assertInstanceOf(False.class, next2.expression());

        Next next3 = (Next) Reader.readExpression("next x");
        assertEquals("next", next3.operator());
        assertInstanceOf(Reference.class, next3.expression());

        Next next4 = (Next) Reader.readExpression("◯◯ true");
        assertEquals("◯", next4.operator());
        assertInstanceOf(Next.class, next4.expression());
    }

    @Test
    void testUnaryEventually() {
        Eventually ev1 = (Eventually) Reader.readExpression("F true");
        assertEquals("F", ev1.operator());
        assertInstanceOf(True.class, ev1.expression());

        Eventually ev2 = (Eventually) Reader.readExpression("<> false");
        assertEquals("<>", ev2.operator());
        assertInstanceOf(False.class, ev2.expression());
    }

    @Test
    void testUnaryGlobally() {
        Globally glob1 = (Globally) Reader.readExpression("G true");
        assertEquals("G", glob1.operator());
        assertInstanceOf(True.class, glob1.expression());

        Globally glob2 = (Globally) Reader.readExpression("[] false");
        assertEquals("[]", glob2.operator());
        assertInstanceOf(False.class, glob2.expression());
    }

    @Test
    void testBinaryConjunction() {
        Conjunction conj = (Conjunction) Reader.readExpression("true and false");
        assertEquals("and", conj.operator());
        assertInstanceOf(True.class, conj.left());
        assertInstanceOf(False.class, conj.right());
    }

    @Test
    void testBinaryDisjunction() {
        Disjunction disj = (Disjunction) Reader.readExpression("true or false");
        assertEquals("or", disj.operator());
        assertInstanceOf(True.class, disj.left());
        assertInstanceOf(False.class, disj.right());
    }

    @Test
    void testBinaryXor() {
        ExclusiveDisjunction xor = (ExclusiveDisjunction) Reader.readExpression("true xor false");
        assertEquals("xor", xor.operator());
        assertInstanceOf(True.class, xor.left());
        assertInstanceOf(False.class, xor.right());
    }

    @Test
    void testBinaryImplication() {
        Implication impl = (Implication) Reader.readExpression("true -> false");
        assertEquals("->", impl.operator());
        assertInstanceOf(True.class, impl.left());
        assertInstanceOf(False.class, impl.right());
    }

    @Test
    void testBinaryEquivalence() {
        Equivalence equiv = (Equivalence) Reader.readExpression("true <-> false");
        assertEquals("<->", equiv.operator());
        assertInstanceOf(True.class, equiv.left());
        assertInstanceOf(False.class, equiv.right());
    }

    @Test
    void testBinaryStrongUntil() {
        StrongUntil until = (StrongUntil) Reader.readExpression("true U false");
        assertEquals("U", until.operator());
        assertInstanceOf(True.class, until.left());
        assertInstanceOf(False.class, until.right());
    }

    @Test
    void testBinaryWeakUntil() {
        WeakUntil wuntil = (WeakUntil) Reader.readExpression("true W false");
        assertEquals("W", wuntil.operator());
        assertInstanceOf(True.class, wuntil.left());
        assertInstanceOf(False.class, wuntil.right());
    }

    @Test
    void testBinaryStrongRelease() {
        StrongRelease rel = (StrongRelease) Reader.readExpression("true M false");
        assertEquals("M", rel.operator());
        assertInstanceOf(True.class, rel.left());
        assertInstanceOf(False.class, rel.right());
    }

    @Test
    void testBinaryWeakRelease() {
        WeakRelease wrel = (WeakRelease) Reader.readExpression("true R false");
        assertEquals("R", wrel.operator());
        assertInstanceOf(True.class, wrel.left());
        assertInstanceOf(False.class, wrel.right());
    }

    @Test
    void testLetExpression() {
        LetExpression let = (LetExpression) Reader.readExpression("let x = true in x");
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
        Declarations decls = Reader.readDeclarations("a = true b = false");
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
        Declarations decls = Reader.readDeclarations("a *= true");
        assertEquals(1, decls.declarations().size());
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        assertEquals("a", decl.name());
        assertInstanceOf(True.class, decl.expression());
        assertFalse(decl.isInternal());
    }

    @Test
    void testComplexExpression() {
        Expression expr = Reader.readExpression("!x and (y or z)");
        assertInstanceOf(Conjunction.class, expr);
        
        Conjunction conj = (Conjunction) expr;
        assertInstanceOf(Negation.class, conj.left());
        assertInstanceOf(Disjunction.class, conj.right());
    }
}
