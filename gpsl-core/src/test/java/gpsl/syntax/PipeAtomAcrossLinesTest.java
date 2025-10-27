package gpsl.syntax;

import gpsl.syntax.model.*;
import org.junit.jupiter.api.Test;
import rege.reader.infra.ParseResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that pipe atoms cannot span across multiple lines,
 * preventing the lexer from treating | operator as atom delimiter.
 */
class PipeAtomAcrossLinesTest {
    
    @Test
    void testPipeShouldNotMatchAcrossLines() {
        String source = """
            pp = true
            qq = false
            disj2 = pp || qq
            disj3 = pp || qq
            """;
        
        // This should parse successfully - single | is no longer a disjunction operator
        var result = Reader.parseDeclarations(source);
        switch(result) {
            case ParseResult.Success<Declarations> success -> {
                assertEquals(4, success.value().declarations().size());
                assertEquals("disj2", success.value().declarations().get(2).name());
                assertInstanceOf(Disjunction.class, success.value().declarations().get(2).expression());
                break;
            }
            case ParseResult.Failure<Declarations> failure -> {
                fail("Should parse successfully - || is disjunction operator. " +
                    "Errors: " + failure.formatErrors());
                break;
            }
        }
        assertTrue(result.isSuccess(), 
            "Should parse successfully - || is disjunction operator. " +
            "Errors: " + (result.isFailure() ? ((ParseResult.Failure<?>) result).formatErrors() : "none"));
    }
    
    @Test
    void testPipeAtomMustBeOnSameLine() {
        String source = """
            test = |atom|
            """;
        
        // This should work - pipe atom on same line
        var result = Reader.parseDeclarations(source);
        assertTrue(result.isSuccess());
    }
}
