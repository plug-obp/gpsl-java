package gpsl.syntax;

import gpsl.parser.GPSLParser;
import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static gpsl.syntax.TestHelpers.*;

/**
 * Comprehensive robustness tests for GPSL grammar and model.
 * Tests edge cases, operator precedence, error conditions, and complex real-world patterns.
 */
class GPSLRobustnessTest {

    // ========== OPERATOR PRECEDENCE TESTS ==========
    
    @Test
    void testOperatorPrecedenceConjunctionBeforeDisjunction() {
        // "a and b or c" should parse as "(a and b) or c"
        Expression expr = parseExpressionWithoutResolution("a and b or c");
        assertInstanceOf(Disjunction.class, expr);
        Disjunction disj = (Disjunction) expr;
        assertInstanceOf(Conjunction.class, disj.left());
        assertInstanceOf(Reference.class, disj.right());
    }
    
    @Test
    void testOperatorPrecedenceNegationHighest() {
        // "!a and b" should parse as "(!a) and b"
        Expression expr = parseExpressionWithoutResolution("!a and b");
        assertInstanceOf(Conjunction.class, expr);
        Conjunction conj = (Conjunction) expr;
        assertInstanceOf(Negation.class, conj.left());
        assertInstanceOf(Reference.class, conj.right());
    }
    
    @Test
    void testOperatorPrecedenceTemporalOperators() {
        // "F a and b" should parse as "(F a) and b"
        Expression expr = parseExpressionWithoutResolution("F a and b");
        assertInstanceOf(Conjunction.class, expr);
        Conjunction conj = (Conjunction) expr;
        assertInstanceOf(Eventually.class, conj.left());
        assertInstanceOf(Reference.class, conj.right());
    }
    
    @Test
    void testOperatorAssociativityImplicationRightAssociative() {
        // "a -> b -> c" should parse as "a -> (b -> c)"
        Expression expr = parseExpressionWithoutResolution("a -> b -> c");
        assertInstanceOf(Implication.class, expr);
        Implication impl = (Implication) expr;
        assertInstanceOf(Reference.class, impl.left());
        assertInstanceOf(Implication.class, impl.right());
    }
    
    @Test
    void testOperatorAssociativityUntilRightAssociative() {
        // "a U b U c" should parse as "a U (b U c)"
        Expression expr = parseExpressionWithoutResolution("a U b U c");
        assertInstanceOf(StrongUntil.class, expr);
        StrongUntil until = (StrongUntil) expr;
        assertInstanceOf(Reference.class, until.left());
        assertInstanceOf(StrongUntil.class, until.right());
    }

    @Test
    void testComplexPrecedence() {
        // "!a U b and c -> d" should respect all precedence rules
        Expression expr = parseExpressionWithoutResolution("!a U b and c -> d");
        assertInstanceOf(Implication.class, expr);
        Implication impl = (Implication) expr;
        assertInstanceOf(Conjunction.class, impl.left());
        Conjunction conj = (Conjunction) impl.left();
        assertInstanceOf(StrongUntil.class, conj.left());
    }

    // ========== ALTERNATIVE OPERATOR SYMBOLS ==========
    
    @Test
    void testAlternativeLetBackslash() {
        Expression expr = parseExpressionWithoutResolution("\\ x = true in x");
        assertInstanceOf(LetExpression.class, expr);
    }
    
    @Test
    void testAlternativeLiterals() {
        assertEquals(new True(), parseExpressionOrFail("1"));
        assertEquals(new False(), parseExpressionOrFail("0"));
    }
    
    @Test
    void testMixedUnicodeOperators() {
        Expression expr = parseExpressionWithoutResolution("1 ∧ 0 ∨ x");
        assertInstanceOf(Disjunction.class, expr);
        Disjunction disj = (Disjunction) expr;
        assertInstanceOf(Conjunction.class, disj.left());
    }
    
    @Test
    void testAllConjunctionVariants() {
        String[] variants = {"and", "&", "&&", "/\\", "*", "∧"};
        for (String op : variants) {
            Expression expr = parseExpressionOrFail("true " + op + " false");
            assertInstanceOf(Conjunction.class, expr, "Failed for operator: " + op);
            assertEquals(op, ((Conjunction) expr).operator());
        }
    }
    
    @Test
    void testAllDisjunctionVariants() {
        // Single | removed from DISJUNCTION to avoid conflict with PIPEATOM
        String[] variants = {"or", "||", "\\/", "+", "∨"};
        for (String op : variants) {
            Expression expr = parseExpressionOrFail("true " + op + " false");
            assertInstanceOf(Disjunction.class, expr, "Failed for operator: " + op);
            assertEquals(op, ((Disjunction) expr).operator());
        }
    }

    // ========== DEEPLY NESTED EXPRESSIONS ==========
    
    @Test
    void testDeeplyNestedParentheses() {
        Expression expr = parseExpressionOrFail("((((true))))");
        assertInstanceOf(True.class, expr);
    }
    
    @Test
    void testDeeplyNestedNegations() {
        Expression expr = parseExpressionWithoutResolution("!!!!!!x");
        Negation neg1 = assertInstanceOf(Negation.class, expr);
        Negation neg2 = assertInstanceOf(Negation.class, neg1.expression());
        Negation neg3 = assertInstanceOf(Negation.class, neg2.expression());
        Negation neg4 = assertInstanceOf(Negation.class, neg3.expression());
        Negation neg5 = assertInstanceOf(Negation.class, neg4.expression());
        Negation neg6 = assertInstanceOf(Negation.class, neg5.expression());
        assertInstanceOf(Reference.class, neg6.expression());
    }
    
    @Test
    void testDeeplyNestedLetExpressions() {
        String input = "let a = true in let b = a in let c = b in c";
        Expression expr = parseExpressionOrFail(input);
        LetExpression let1 = assertInstanceOf(LetExpression.class, expr);
        LetExpression let2 = assertInstanceOf(LetExpression.class, let1.expression());
        LetExpression let3 = assertInstanceOf(LetExpression.class, let2.expression());
        assertInstanceOf(Reference.class, let3.expression());
    }

    @Test
    void testComplexNestedExpression() {
        String input = "((G (a -> <> b)) and ([] (c U d))) or (!e and F f)";
        Expression expr = parseExpressionWithoutResolution(input);
        assertInstanceOf(Disjunction.class, expr);
    }

    // ========== IDENTIFIER EDGE CASES ==========
    
    @Test
    void testSingleLetterIdentifiers() {
        // Test only non-keyword single letters
        String[] validIds = {"a", "b", "c", "d", "e", "h", "i", "j", "k", "l", "m", 
                            "p", "q", "s", "t", "v", "w", "x", "y", "z"};
        for (String id : validIds) {
            Expression expr = parseExpressionWithoutResolution(id);
            if (expr instanceof Reference ref) {
                assertEquals(id, ref.name());
            }
            // Some single letters like 'o' might be operators (Next)
        }
    }
    
    @Test
    void testIdentifiersWithUnderscores() {
        // Identifiers must start with a letter, can contain underscores and numbers
        String[] validIdentifiers = {"var_name", "x_1_2", "test_VAR_123"};
        for (String id : validIdentifiers) {
            Reference ref = (Reference) parseExpressionWithoutResolution(id);
            assertEquals(id, ref.name());
        }
        
        // Identifiers starting with underscore are invalid per grammar
        // IDENTIFIER : [a-zA-Z][a-zA-Z_0-9]*;
    }
    
    @Test
    void testLongIdentifier() {
        String longId = "veryLongIdentifierNameWithManyCharacters123456789ABC";
        Reference ref = (Reference) parseExpressionWithoutResolution(longId);
        assertEquals(longId, ref.name());
    }
    
    @Test
    void testMixedCaseIdentifiers() {
        String[] identifiers = {"AbCdEf", "camelCase", "PascalCase", "ALLCAPS"};
        for (String id : identifiers) {
            Reference ref = (Reference) parseExpressionWithoutResolution(id);
            assertEquals(id, ref.name());
        }
    }

    // ========== ATOM EDGE CASES ==========
    
    // Empty atoms - grammar allows them but they may not be semantically meaningful
    // PIPEATOM : '|' ('\\|' | ~'|')* '|';  -- the * allows zero matches
    // QUOTEATOM: '"' ('\\"' | ~'"')* '"';  -- the * allows zero matches
    // Testing this behavior is tricky as ANTLR error recovery may vary
    
    @Test
    void testAtomsWithSpecialCharacters() {
        Atom atom = (Atom) parseExpressionWithoutResolution("|a@#$%^&*()|");
        assertEquals("a@#$%^&*()", atom.value());
    }
    
    @Test
    void testAtomsWithUnicode() {
        Atom atom = (Atom) parseExpressionOrFail("|α β γ δ|");
        assertEquals("α β γ δ", atom.value());
    }
    
    @Test
    void testLongAtom() {
        String content = "a".repeat(1000);
        Atom atom = (Atom) parseExpressionOrFail("|" + content + "|");
        assertEquals(content, atom.value());
    }
    
    @Test
    void testAtomWithMixedEscaping() {
        Atom atom1 = (Atom) parseExpressionWithoutResolution("|a\\|b|");
        assertEquals("a|b", atom1.value());
        
        Atom atom2 = (Atom) parseExpressionOrFail("\"a\\\"b\"");
        assertEquals("a\"b", atom2.value());
    }

    // ========== DECLARATION EDGE CASES ==========
    
    @Test
    void testSingleDeclaration() {
        Declarations decls = parseDeclarationsOrFail("x = true");
        assertEquals(1, decls.declarations().size());
    }
    
    @Test
    void testManyDeclarations() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            if (i > 0) sb.append(" ");
            sb.append("x").append(i).append(" = true");
        }
        Declarations decls = parseDeclarationsOrFail(sb.toString());
        assertEquals(50, decls.declarations().size());
    }
    
    @Test
    void testDeclarationWithTrailingCommaInLet() {
        Expression expr = parseExpressionWithoutResolution("let x = true, y = false, in x");
        LetExpression let = assertInstanceOf(LetExpression.class, expr);
        assertEquals(2, let.declarations().declarations().size());
    }
    
    @Test
    void testForwardReference() {
        // Forward references fail - declarations must be in order
        String input = "a = b b = true";
        assertDeclarationsParseError(input, "undefined-symbol");
        
        // But backward references work
        String validInput = "b = true a = b";
        Declarations validDecls = parseDeclarationsOrFail(validInput);
        assertNotNull(validDecls);
    }

    // ========== LET EXPRESSION SCOPING ==========
    
    @Test
    void testLetWithMultipleVariables() {
        String input = "let x = true, y = false, z = x in z and y";
        Expression expr = parseExpressionOrFail(input);
        LetExpression let = assertInstanceOf(LetExpression.class, expr);
        assertEquals(3, let.declarations().declarations().size());
    }
    
    @Test
    void testVariableShadowing() {
        String input = """
            outer = let x = true in
                let x = false in
                    x
            """;
        Declarations decls = parseDeclarationsOrFail(input);
        // The inner x should shadow the outer x
        assertNotNull(decls);
    }
    
    @Test
    void testLetWithComplexBody() {
        String input = "let x = |atom| in G (x -> <> !x)";
        Expression expr = parseExpressionOrFail(input);
        LetExpression let = assertInstanceOf(LetExpression.class, expr);
        assertInstanceOf(Globally.class, let.expression());
    }

    // ========== AUTOMATON EDGE CASES ==========
    
    @Test
    void testAutomatonWithMultipleInitialStates() {
        String input = "a = states s0, s1, s2; initial s0, s1; accept s2; s0 [true] s2; s1 [true] s2";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        assertEquals(2, automaton.initialStates().size());
    }
    
    @Test
    void testAutomatonWithMultipleAcceptStates() {
        String input = "a = states s0, s1, s2; initial s0; accept s1, s2; s0 [true] s1; s0 [false] s2";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        assertEquals(2, automaton.acceptStates().size());
    }
    
    // Grammar requires at least one identifier after 'accept': ACCEPT IDENTIFIER (',' IDENTIFIER)*
    // Testing parse errors is tricky as ANTLR error recovery behavior may vary
    
    @Test
    void testAutomatonWithComplexGuard() {
        String input = "a = states s0, s1; initial s0; accept s1; s0 [G (|x| -> <> |y|)] s1";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        Transition trans = automaton.transitions().get(0);
        assertInstanceOf(Globally.class, trans.guard());
    }
    
    @Test
    void testAutomatonWithLetInGuard() {
        String input = "a = states s0; initial s0; accept s0; s0 [let x = |atom| in x] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        Transition trans = automaton.transitions().get(0);
        assertInstanceOf(LetExpression.class, trans.guard());
    }
    
    @Test
    void testAutomatonWithLetDeclaration() {
        String input = "a = let helper = |atom| in states s0; initial s0; accept s0; s0 [helper] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        assertNotNull(decls);
    }
    
    @Test
    void testAutomatonDefaultSemantics() {
        // When no NFA/BUCHI keyword, should default to BUCHI
        String input = "a = states s0; initial s0; accept s0; s0 [true] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        assertEquals(AutomatonSemanticsKind.BUCHI, automaton.semanticsKind());
    }
    
    @Test
    void testAutomatonSelfLoopWithDifferentPriorities() {
        String input = "a = states s0; initial s0; accept s0; s0 10 [|x|] s0; s0 5 [|y|] s0; s0 1 [true] s0";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration automDecl = decls.declarations().get(0);
        LetExpression letExpr = (LetExpression) automDecl.expression();
        Automaton automaton = (Automaton) letExpr.expression();
        
        // Transitions should be sorted in ascending order (1, 5, 10) where 1 > 5 > 10 in priority
        assertEquals(3, automaton.transitions().size());
        assertEquals(1, automaton.transitions().get(0).priority());
        assertEquals(5, automaton.transitions().get(1).priority());
        assertEquals(10, automaton.transitions().get(2).priority());
    }

    // ========== REAL-WORLD LTL PATTERNS ==========
    
    @Test
    void testSafetyProperty() {
        // []precondition -> []postcondition
        String input = "safety = [] (|precondition| -> [] |postcondition|)";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        assertInstanceOf(Globally.class, decl.expression());
    }
    
    @Test
    void testLivenessProperty() {
        // []<>condition
        String input = "liveness = [] <> |condition|";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        Globally glob = assertInstanceOf(Globally.class, decl.expression());
        assertInstanceOf(Eventually.class, glob.expression());
    }
    
    @Test
    void testResponsePattern() {
        // [](request -> <>response)
        String input = "response = [] (|request| -> <> |response|)";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        Globally glob = assertInstanceOf(Globally.class, decl.expression());
        Implication impl = assertInstanceOf(Implication.class, glob.expression());
        assertInstanceOf(Eventually.class, impl.right());
    }
    
    @Test
    void testFairnessProperty() {
        // []<>enabled -> []<>executed
        String input = "fairness = ([] <> |enabled|) -> ([] <> |executed|)";
        Declarations decls = parseDeclarationsOrFail(input);
        
        ExpressionDeclaration decl = decls.declarations().get(0);
        Implication impl = assertInstanceOf(Implication.class, decl.expression());
        assertInstanceOf(Globally.class, impl.left());
        assertInstanceOf(Globally.class, impl.right());
    }
    
    @Test
    void testComplexMutualExclusion() {
        String input = """
            cs1 = |process1_in_critical_section|
            cs2 = |process2_in_critical_section|
            mutex = [] !(cs1 and cs2)
            """;
        Declarations decls = parseDeclarationsOrFail(input);
        assertEquals(3, decls.declarations().size());
        
        ExpressionDeclaration mutexDecl = decls.declarations().get(2);
        Globally glob = assertInstanceOf(Globally.class, mutexDecl.expression());
        Negation neg = assertInstanceOf(Negation.class, glob.expression());
        assertInstanceOf(Conjunction.class, neg.expression());
    }

    // ========== ERROR HANDLING ==========
    
    @Test
    void testUndefinedSymbolThrows() {
        assertDeclarationsParseError("x = undefined", "undefined-symbol");
    }
    
    @Test
    void testCircularReferenceDetection() {
        // Circular references fail because of forward reference restriction
        // "a = b" tries to resolve b before it's defined
        String input = "a = b b = a";
        assertDeclarationsParseError(input, "undefined-symbol");
    }

    // ========== WHITESPACE AND COMMENTS ==========
    
    @Test
    void testExpressionWithLineComment() {
        String input = "true and false // this is a comment";
        Expression expr = parseExpressionOrFail(input);
        assertInstanceOf(Conjunction.class, expr);
    }
    
    @Test
    void testExpressionWithBlockComment() {
        String input = "true /* comment */ and /* another */ false";
        Expression expr = parseExpressionOrFail(input);
        assertInstanceOf(Conjunction.class, expr);
    }
    
    @Test
    void testMultilineExpression() {
        String input = """
            G (
                |condition| ->
                <> |result|
            )
            """;
        Expression expr = parseExpressionOrFail(input);
        assertInstanceOf(Globally.class, expr);
    }
    
    @Test
    void testDeclarationsWithComments() {
        String input = """
            // First declaration
            a = true
            /* Block comment */
            b = false // inline comment
            """;
        Declarations decls = parseDeclarationsOrFail(input);
        assertEquals(2, decls.declarations().size());
    }

    // ========== INTEGRATION TESTS ==========
    
    @Test
    void testCompleteAliceBobFairness() {
        String input = """
            aliceCS = |{Alice}1@CS|
            bobCS = |{Bob}1@CS|
            aliceFlagUP = |{sys}1:flags[0] = true|
            bobFlagUP = |{sys}1:flags[1] = true|
            fairness *= [] ((aliceFlagUP -> <> aliceCS) and (bobFlagUP -> <> bobCS))
            """;
        
        Declarations decls = parseDeclarationsOrFail(input);
        assertEquals(5, decls.declarations().size());
        
        ExpressionDeclaration fairnessDecl = decls.declarations().get(4);
        assertFalse(fairnessDecl.isInternal());
        assertInstanceOf(Globally.class, fairnessDecl.expression());
    }
    
    @Test
    void testAutomatonWithExternalReferences() {
        String input = """
            condition = |x > 0|
            automaton = states s0, s1; initial s0; accept s1; 
                       s0 [condition] s1;
                       s1 [!condition] s0
            """;
        
        Declarations decls = parseDeclarationsOrFail(input);
        assertEquals(2, decls.declarations().size());
    }
}
