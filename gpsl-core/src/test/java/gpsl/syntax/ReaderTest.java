package gpsl.syntax;

import gpsl.syntax.model.*;
import rege.reader.infra.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static gpsl.syntax.TestHelpers.*;

/**
 * Tests for the Reader class, including symbol resolution and linking.
 */
class ReaderTest {

    @Test
    void testReadExpression() {
        Expression expr = parseExpressionOrFail("true and false");
        assertInstanceOf(Conjunction.class, expr);
        
        Conjunction conj = (Conjunction) expr;
        assertInstanceOf(True.class, conj.left());
        assertInstanceOf(False.class, conj.right());
    }

    @Test
    void testReadDeclarations() {
        Declarations decls = parseDeclarationsOrFail("a = true b = false");
        assertNotNull(decls);
        assertEquals(2, decls.declarations().size());
        assertEquals("a", decls.declarations().get(0).name());
        assertEquals("b", decls.declarations().get(1).name());
    }

    @Test
    void testLinkSimpleReference() {
        Declarations decls = parseDeclarationsOrFail("a = true b = a");
        
        ExpressionDeclaration declB = decls.declarations().get(1);
        assertInstanceOf(Reference.class, declB.expression());
        
        Reference ref = (Reference) declB.expression();
        assertNotNull(ref.expression());
        assertInstanceOf(True.class, ref.expression());
    }

    @Test
    void testLinkWithLetExpression() {
        String input = "result = let x = true in x and false";
        var resultWithPos = Reader.parseDeclarationsWithPositions(input);
        assertTrue(resultWithPos.result().isSuccess(), "Parse should succeed");
        Declarations decls = ((ParseResult.Success<Declarations>) resultWithPos.result()).value();
        
        var linkResult = Reader.link(decls, resultWithPos.source(), resultWithPos.positionMap());
        assertTrue(linkResult.isSuccess(), "Linking should succeed");
        
        ExpressionDeclaration resultDecl = decls.declarations().get(0);
        assertInstanceOf(LetExpression.class, resultDecl.expression());
        
        LetExpression letExpr = (LetExpression) resultDecl.expression();
        Conjunction body = (Conjunction) letExpr.expression();
        
        assertInstanceOf(Reference.class, body.left());
        Reference xRef = (Reference) body.left();
        assertNotNull(xRef.expression());
        assertInstanceOf(True.class, xRef.expression());
    }

    @Test
    void testLinkNestedLetExpressions() {
        String input = "result = let x = true in let y = x in y and false";
        var resultWithPos = Reader.parseDeclarationsWithPositions(input);
        assertTrue(resultWithPos.result().isSuccess(), "Parse should succeed");
        Declarations decls = ((ParseResult.Success<Declarations>) resultWithPos.result()).value();
        
        var linkResult = Reader.link(decls, resultWithPos.source(), resultWithPos.positionMap());
        assertTrue(linkResult.isSuccess(), "Linking should succeed");
        
        ExpressionDeclaration resultDecl = decls.declarations().get(0);
        assertInstanceOf(LetExpression.class, resultDecl.expression());
    }

    @Test
    void testLinkWithContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("external", new True());
        
        Declarations decls = parseDeclarationsOrFail("a = external", context);
        
        ExpressionDeclaration declA = decls.declarations().get(0);
        assertInstanceOf(Reference.class, declA.expression());
        
        Reference ref = (Reference) declA.expression();
        assertNotNull(ref.expression());
        assertInstanceOf(True.class, ref.expression());
    }

    @Test
    void testReadAndLinkDeclarations() {
        String input = "a = true b = a c = b and a";
        Declarations decls = parseDeclarationsOrFail(input);
        
        // Check that all references are resolved
        ExpressionDeclaration declB = decls.declarations().get(1);
        assertInstanceOf(Reference.class, declB.expression());
        Reference refB = (Reference) declB.expression();
        assertNotNull(refB.expression());
        
        ExpressionDeclaration declC = decls.declarations().get(2);
        assertInstanceOf(Conjunction.class, declC.expression());
        Conjunction conjC = (Conjunction) declC.expression();
        
        assertInstanceOf(Reference.class, conjC.left());
        assertInstanceOf(Reference.class, conjC.right());
        
        Reference leftRef = (Reference) conjC.left();
        Reference rightRef = (Reference) conjC.right();
        assertNotNull(leftRef.expression());
        assertNotNull(rightRef.expression());
    }

    @Test
    void testReadAndLinkDeclarationsWithContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("ext", new False());
        
        String input = "a = ext b = a or ext";
        Declarations decls = parseDeclarationsOrFail(input, context);
        
        ExpressionDeclaration declA = decls.declarations().get(0);
        assertInstanceOf(Reference.class, declA.expression());
        Reference refA = (Reference) declA.expression();
        assertNotNull(refA.expression());
        assertInstanceOf(False.class, refA.expression());
    }

    @Test
    void testUndefinedSymbolThrowsException() {
        assertDeclarationsParseError("a = undefined_symbol", "undefined-symbol");
    }

    @Test
    void testComplexFormula() {
        String input = """
            aliceCS = |{Alice}1@CS|
            bobCS = |{Bob}1@CS|
            fairness *= let
                aliceFlagUP = |{sys}1:flags[0] = true|,
                bobFlagUP = |{sys}1:flags[1] = true|
            in
                ([] ((aliceFlagUP -> (<> aliceCS)) && (bobFlagUP -> (<> bobCS))))
            """;
        
        Declarations decls = parseDeclarationsOrFail(input);
        assertEquals(3, decls.declarations().size());
        
        ExpressionDeclaration fairnessDecl = decls.declarations().get(2);
        assertEquals("fairness", fairnessDecl.name());
        assertFalse(fairnessDecl.isInternal()); // *= means external
        assertInstanceOf(LetExpression.class, fairnessDecl.expression());
    }

    @Test
    void testAutomatonDeclaration() {
        String input = "a = states s0, s1; initial s0; accept s1; s0 [true] s1; s1 [false] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        assertEquals(1, decls.declarations().size());
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        assertEquals("a", automDecl.name());
        assertInstanceOf(LetExpression.class, automDecl.expression());
        
        LetExpression letExpr = (LetExpression) automDecl.expression();
        assertInstanceOf(Automaton.class, letExpr.expression());
        
        Automaton automaton = (Automaton) letExpr.expression();
        assertEquals(2, automaton.states().size());
        assertEquals(1, automaton.initialStates().size());
        assertEquals(1, automaton.acceptStates().size());
        assertEquals(2, automaton.transitions().size());
    }

    @Test
    void testAutomatonWithPriority() {
        String input = "a = states s0; initial s0; accept s0; s0 10 [true] s0; s0 5 [false] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        // Transitions should be sorted by priority (descending)
        assertEquals(2, automaton.transitions().size());
        assertEquals(10, automaton.transitions().get(0).priority());
        assertEquals(5, automaton.transitions().get(1).priority());
    }

    @Test
    void testNFASemantics() {
        String input = "a = nfa states s0; initial s0; accept s0; s0 [true] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        assertEquals(AutomatonSemanticsKind.NFA, automaton.semanticsKind());
    }

    @Test
    void testBuchiSemantics() {
        String input = "a = buchi states s0; initial s0; accept s0; s0 [true] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
    }
}
