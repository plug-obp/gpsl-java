package gpsl.syntax;

import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static gpsl.syntax.TestHelpers.*;

/**
 * Test that double pipe || operator is correctly parsed as disjunction.
 * Note: Single | is reserved for PIPEATOM to avoid ambiguity.
 */
class PipeOperatorTest {
    
    @Test
    void testDoublePipeIsDisjunction() {
        String source = """
            pp = true
            qq = false
            test = pp || qq
            """;
        
        Declarations decls = parseDeclarationsOrFail(source);
        
        assertEquals(3, decls.declarations().size());
        
        ExpressionDeclaration testDecl = decls.declarations().get(2);
        assertEquals("test", testDecl.name());
        
        // Should be a BinaryExpression with OR operator
        Expression expr = testDecl.expression();
        assertInstanceOf(BinaryExpression.class, expr, 
            "|| should parse as BinaryExpression (disjunction)");
        
        // Should specifically be a Disjunction
        assertInstanceOf(Disjunction.class, expr);
        Disjunction disjunction = (Disjunction) expr;
        
        // Left side should be Reference to pp
        assertInstanceOf(Reference.class, disjunction.left());
        Reference leftRef = (Reference) disjunction.left();
        assertEquals("pp", leftRef.name());
        assertNotNull(leftRef.expression(), "pp should be linked");
        
        // Right side should be Reference to qq
        assertInstanceOf(Reference.class, disjunction.right());
        Reference rightRef = (Reference) disjunction.right();
        assertEquals("qq", rightRef.name());
        assertNotNull(rightRef.expression(), "qq should be linked");
    }
    
    @Test
    void testOrKeywordIsDisjunction() {
        String source = """
            pp = true
            qq = false
            test = pp or qq
            """;
        
        Declarations decls = parseDeclarationsOrFail(source);
        
        ExpressionDeclaration testDecl = decls.declarations().get(2);
        Expression expr = testDecl.expression();
        assertInstanceOf(Disjunction.class, expr);
    }
}
