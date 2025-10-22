package gpsl.syntax;

import gpsl.parser.GPSLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for GPSL parser ANTLR4 parse tree generation.
 * These tests verify that the parser correctly generates parse trees from GPSL input.
 */
class GPSLParserTest {

    private String parseExpression(String input) {
        GPSLParser parser = Reader.antlr4Parser(input);
        GPSLParser.FormulaContext tree = parser.formula();
        return tree.toStringTree(parser);
    }

    private String parseDeclarations(String input) {
        GPSLParser parser = Reader.antlr4Parser(input);
        GPSLParser.BlockContext tree = parser.block();
        return tree.toStringTree(parser);
    }

    @Test
    void testLiteral() {
        assertEquals("(formula (literal true))", parseExpression("true"));
        assertEquals("(formula (literal false))", parseExpression("false"));
    }

    @Test
    void testReference() {
        assertEquals("(formula x)", parseExpression("x"));
        assertEquals("(formula zm)", parseExpression("zm"));
        assertEquals("(formula x1)", parseExpression("x1"));
        assertEquals("(formula N (formula x))", parseExpression("N x"));
    }

    @Test
    void testAtomPipe() {
        assertEquals("(formula (atom |x|))", parseExpression("|x|"));
        assertEquals("(formula (atom |a-b|))", parseExpression("|a-b|"));
        assertEquals("(formula (atom |a>2|))", parseExpression("|a>2|"));
        assertEquals("(formula (atom |to\\|to|))", parseExpression("|to\\|to|"));
        assertEquals("(formula (atom |to\\\"to|))", parseExpression("|to\\\"to|"));
    }

    @Test
    void testAtomQuote() {
        assertEquals("(formula (atom \"x\"))", parseExpression("\"x\""));
        assertEquals("(formula (atom \"a-b\"))", parseExpression("\"a-b\""));
        assertEquals("(formula (atom \"a>2\"))", parseExpression("\"a>2\""));
        assertEquals("(formula (atom \"to\\|to\"))", parseExpression("\"to\\|to\""));
        assertEquals("(formula (atom \"to\\\"to\"))", parseExpression("\"to\\\"to\""));
    }

    @Test
    void testParen() {
        assertEquals("(formula ( (formula (literal true)) ))", parseExpression("(true)"));
        assertEquals("(formula ( (formula zm) ))", parseExpression("(zm)"));
        assertEquals("(formula ( (formula x) ))", parseExpression("(x)"));
        assertEquals("(formula ( (formula N (formula x)) ))", parseExpression("(N x)"));
    }

    @Test
    void testUnaryNegationExclamation() {
        assertEquals("(formula ! (formula (literal true)))", parseExpression("!true"));
        assertEquals("(formula ! (formula (literal false)))", parseExpression("!false"));
        assertEquals("(formula ! (formula x))", parseExpression("!x"));
        assertEquals("(formula ! (formula zm))", parseExpression("!zm"));
        assertEquals("(formula ! (formula ! (formula (literal true))))", parseExpression("!!true"));
    }

    @Test
    void testUnaryNegationTilde() {
        assertEquals("(formula ~ (formula (literal true)))", parseExpression("~true"));
        assertEquals("(formula ~ (formula (literal false)))", parseExpression("~false"));
        assertEquals("(formula ~ (formula x))", parseExpression("~x"));
        assertEquals("(formula ~ (formula zm))", parseExpression("~zm"));
        assertEquals("(formula ~ (formula ~ (formula (literal true))))", parseExpression("~~true"));
    }

    @Test
    void testUnaryNegationNot() {
        assertEquals("(formula not (formula (literal true)))", parseExpression("not true"));
        assertEquals("(formula not (formula (literal false)))", parseExpression("not false"));
        assertEquals("(formula not (formula x))", parseExpression("not x"));
        assertEquals("(formula not (formula zm))", parseExpression("not zm"));
        assertEquals("(formula not (formula not (formula (literal true))))", parseExpression("not not true"));
    }

    @Test
    void testNext() {
        assertEquals("(formula next (formula (literal true)))", parseExpression("next true"));
        assertEquals("(formula N (formula (literal true)))", parseExpression("N true"));
        assertEquals("(formula () (formula (literal true)))", parseExpression("() true"));
        assertEquals("(formula ◯ (formula (literal true)))", parseExpression("◯ true"));
        assertEquals("(formula o (formula (literal true)))", parseExpression("o true"));
        assertEquals("(formula o (formula ( (formula o (formula x)) )))", parseExpression("o(o x)"));
    }

    @Test
    void testEventually() {
        assertEquals("(formula eventually (formula (literal true)))", parseExpression("eventually true"));
        assertEquals("(formula F (formula (literal true)))", parseExpression("F true"));
        assertEquals("(formula <> (formula (literal true)))", parseExpression("<> true"));
        assertEquals("(formula ◇ (formula (literal true)))", parseExpression("◇ true"));
    }

    @Test
    void testGlobally() {
        assertEquals("(formula globally (formula (literal true)))", parseExpression("globally true"));
        assertEquals("(formula always (formula (literal true)))", parseExpression("always true"));
        assertEquals("(formula G (formula (literal true)))", parseExpression("G true"));
        assertEquals("(formula [] (formula (literal true)))", parseExpression("[] true"));
        assertEquals("(formula ☐ (formula (literal true)))", parseExpression("☐ true"));
    }

    @Test
    void testStrongUntil() {
        assertEquals("(formula (formula (literal true)) U (formula (literal false)))", parseExpression("true U false"));
        assertEquals("(formula (formula (literal true)) SU (formula (literal false)))", parseExpression("true SU false"));
        assertEquals("(formula (formula (literal true)) until (formula (literal false)))", parseExpression("true until false"));
        assertEquals("(formula (formula (literal true)) strong-until (formula (literal false)))", parseExpression("true strong-until false"));
    }

    @Test
    void testWeakUntil() {
        assertEquals("(formula (formula (literal true)) W (formula (literal false)))", parseExpression("true W false"));
        assertEquals("(formula (formula (literal true)) WU (formula (literal false)))", parseExpression("true WU false"));
        assertEquals("(formula (formula (literal true)) weak-until (formula (literal false)))", parseExpression("true weak-until false"));
    }

    @Test
    void testStrongRelease() {
        assertEquals("(formula (formula (literal true)) M (formula (literal false)))", parseExpression("true M false"));
        assertEquals("(formula (formula (literal true)) SR (formula (literal false)))", parseExpression("true SR false"));
        assertEquals("(formula (formula (literal true)) strong-release (formula (literal false)))", parseExpression("true strong-release false"));
    }

    @Test
    void testWeakRelease() {
        assertEquals("(formula (formula (literal true)) R (formula (literal false)))", parseExpression("true R false"));
        assertEquals("(formula (formula (literal true)) WR (formula (literal false)))", parseExpression("true WR false"));
        assertEquals("(formula (formula (literal true)) weak-release (formula (literal false)))", parseExpression("true weak-release false"));
    }

    @Test
    void testConjunction() {
        assertEquals("(formula (formula (literal true)) and (formula (literal false)))", parseExpression("true and false"));
        assertEquals("(formula (formula (literal true)) & (formula (literal false)))", parseExpression("true & false"));
        assertEquals("(formula (formula (literal true)) && (formula (literal false)))", parseExpression("true && false"));
        assertEquals("(formula (formula (literal true)) /\\ (formula (literal false)))", parseExpression("true /\\ false"));
        assertEquals("(formula (formula (literal true)) * (formula (literal false)))", parseExpression("true * false"));
        assertEquals("(formula (formula (literal true)) ∧ (formula (literal false)))", parseExpression("true ∧ false"));
    }

    @Test
    void testDisjunction() {
        assertEquals("(formula (formula (literal true)) or (formula (literal false)))", parseExpression("true or false"));
        assertEquals("(formula (formula (literal true)) | (formula (literal false)))", parseExpression("true | false"));
        assertEquals("(formula (formula (literal true)) || (formula (literal false)))", parseExpression("true || false"));
        assertEquals("(formula (formula (literal true)) \\/ (formula (literal false)))", parseExpression("true \\/ false"));
        assertEquals("(formula (formula (literal true)) + (formula (literal false)))", parseExpression("true + false"));
        assertEquals("(formula (formula (literal true)) ∨ (formula (literal false)))", parseExpression("true ∨ false"));
    }

    @Test
    void testXor() {
        assertEquals("(formula (formula (literal true)) xor (formula (literal false)))", parseExpression("true xor false"));
        assertEquals("(formula (formula (literal true)) ^ (formula (literal false)))", parseExpression("true ^ false"));
        assertEquals("(formula (formula (literal true)) ⊻ (formula (literal false)))", parseExpression("true ⊻ false"));
        assertEquals("(formula (formula (literal true)) ⊕ (formula (literal false)))", parseExpression("true ⊕ false"));
    }

    @Test
    void testImplication() {
        assertEquals("(formula (formula (literal true)) implies (formula (literal false)))", parseExpression("true implies false"));
        assertEquals("(formula (formula (literal true)) -> (formula (literal false)))", parseExpression("true -> false"));
        assertEquals("(formula (formula (literal true)) => (formula (literal false)))", parseExpression("true => false"));
        assertEquals("(formula (formula (literal true)) → (formula (literal false)))", parseExpression("true → false"));
        assertEquals("(formula (formula (literal true)) ⟹ (formula (literal false)))", parseExpression("true ⟹ false"));
    }

    @Test
    void testEquivalence() {
        assertEquals("(formula (formula (literal true)) iff (formula (literal false)))", parseExpression("true iff false"));
        assertEquals("(formula (formula (literal true)) <-> (formula (literal false)))", parseExpression("true <-> false"));
        assertEquals("(formula (formula (literal true)) <=> (formula (literal false)))", parseExpression("true <=> false"));
        assertEquals("(formula (formula (literal true)) ↔ (formula (literal false)))", parseExpression("true ↔ false"));
        assertEquals("(formula (formula (literal true)) ⟺ (formula (literal false)))", parseExpression("true ⟺ false"));
    }

    @Test
    void testLet() {
        assertEquals("(formula (letDecl let (formulaDeclarationList (formulaDeclaration x = (formula (literal true)))) in) (formula x))", 
                parseExpression("let x = true in x"));
        assertEquals("(formula (letDecl let (formulaDeclarationList (formulaDeclaration x = (formula (literal true)))) in) (formula (formula x) and (formula x)))", 
                parseExpression("let x = true in x and x"));
    }

    @Test
    void testMultipleDeclarations() {
        assertEquals("(block (formulaDeclaration a = (formula (literal true))) (formulaDeclaration b = (formula (literal false))))", 
                parseDeclarations("a = true b = false"));
    }

    @Test
    void testLinkedDeclarations() {
        assertEquals("(block (formulaDeclaration a = (formula (literal true))) (formulaDeclaration b = (formula a)))", 
                parseDeclarations("a = true b = a"));
    }

    @Test
    void testLinkedDeclarationsWithLet() {
        assertEquals("(block (formulaDeclaration a = (formula (literal true))) (formulaDeclaration b = (formula (letDecl let (formulaDeclarationList (formulaDeclaration x = (formula a))) in) (formula (formula x) && (formula (literal true))))))", 
                parseDeclarations("a = true b = let x = a in x && true"));
    }

    @Test
    void testAutomaton() {
        assertEquals("(block (formulaDeclaration a = (automaton (automatonDecl (stateDecl states s0) ; (initialDecl initial s0) ; (acceptDecl accept s0) ; (transitionDecl s0 [ (formula (literal true)) ] s0)))))", 
                parseDeclarations("a = states s0; initial s0; accept s0; s0 [true] s0"));
    }

    @Test
    void testAutomatonWithPriority() {
        assertEquals("(block (formulaDeclaration a = (automaton (automatonDecl (stateDecl states s0) ; (initialDecl initial s0) ; (acceptDecl accept s0) ; (transitionDecl s0 5 [ (formula (literal true)) ] s0)))))", 
                parseDeclarations("a = states s0; initial s0; accept s0; s0 5 [true] s0"));
        assertEquals("(block (formulaDeclaration a = (automaton (automatonDecl (stateDecl states s0) ; (initialDecl initial s0) ; (acceptDecl accept s0) ; (transitionDecl s0 true [ (formula (literal true)) ] s0)))))", 
                parseDeclarations("a = states s0; initial s0; accept s0; s0 true [true] s0"));
        assertEquals("(block (formulaDeclaration a = (automaton (automatonDecl (stateDecl states s0) ; (initialDecl initial s0) ; (acceptDecl accept s0) ; (transitionDecl s0 false [ (formula (literal true)) ] s0)))))", 
                parseDeclarations("a = states s0; initial s0; accept s0; s0 false [true] s0"));
    }
}
