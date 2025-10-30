package gpsl.syntax;

import gpsl.toBuchi.Expression2BuchiAutomaton;
import gpsl.syntax.model.*;
import rege.reader.infra.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test utilities for GPSL parsing.
 * Provides helper methods to make tests cleaner when using ParseResult API.
 */
public class TestHelpers {
    
    /**
     * Parse and link expression or fail test.
     * This method performs both parsing and symbol resolution.
     */
    public static Expression parseExpressionOrFail(String source) {
        var resultWithPos = Reader.parseExpressionWithPositions(source);
        ParseResult<Expression> result = Reader.linkWithPositions(resultWithPos);
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            fail("Parse/link failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Expression>) result).value();
    }    /**
     * Parse expression without symbol resolution (for AST-only tests).
     * This is useful for testing the Antlr4ToGPSLMapper without requiring defined symbols.
     */
    public static Expression parseExpressionWithoutResolution(String source) {
        ParseResult<Expression> result = Reader.parseExpression(source);
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            fail("Parse failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Expression>) result).value();
    }
    
    /**
     * Parse declarations or fail test.
     * This method performs symbol resolution.
     */
    public static Declarations parseDeclarationsOrFail(String source) {
        var resultWithPos = Reader.parseDeclarationsWithPositions(source);
        ParseResult<Declarations> result = Reader.linkWithPositions(resultWithPos);
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            fail("Parse/link failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Declarations>) result).value();
    }
    
    /**
     * Parse declarations with external context or fail test.
     */
    public static Declarations parseDeclarationsOrFail(String source, java.util.Map<String, Object> context) {
        var resultWithPos = Reader.parseDeclarationsWithPositions(source);
        ParseResult<Declarations> result = Reader.linkWithPositions(resultWithPos, context);
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            fail("Parse/link failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Declarations>) result).value();
    }
    
    /**
     * Assert that parsing fails with specific error code.
     * This performs symbol resolution to catch linking errors.
     */
    public static void assertParseError(String source, String expectedErrorCode) {
        var resultWithPos = Reader.parseExpressionWithPositions(source);
        ParseResult<Expression> result = Reader.linkWithPositions(resultWithPos);
        
        assertTrue(result.isFailure(), "Expected parse to fail");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            boolean found = failure.errors().stream()
                .anyMatch(e -> e.code().map(c -> c.equals(expectedErrorCode)).orElse(false));
            
            assertTrue(found, 
                "Expected error code '" + expectedErrorCode + "' but got:\n" + 
                failure.formatErrors());
        }
    }
    
    /**
     * Assert parsing fails with error at specific position.
     * This performs symbol resolution to catch linking errors.
     */
    public static void assertParseErrorAt(String source, int line, int column, String expectedCode) {
        var resultWithPos = Reader.parseExpressionWithPositions(source);
        ParseResult<Expression> result = Reader.linkWithPositions(resultWithPos);
        
        assertTrue(result.isFailure(), "Expected parse to fail");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            boolean found = failure.errors().stream()
                .anyMatch(e -> 
                    e.range().start().line() == line &&
                    e.range().start().column() == column &&
                    e.code().map(c -> c.equals(expectedCode)).orElse(false)
                );
            
            assertTrue(found,
                "Expected error at " + line + ":" + column + " with code '" + expectedCode + 
                "' but got:\n" + failure.formatErrors());
        }
    }
    
    /**
     * Assert declarations parsing fails with error code.
     * This performs symbol resolution to catch linking errors.
     */
    public static void assertDeclarationsParseError(String source, String expectedErrorCode) {
        var resultWithPos = Reader.parseDeclarationsWithPositions(source);
        ParseResult<Declarations> result = Reader.linkWithPositions(resultWithPos);
        
        assertTrue(result.isFailure(), "Expected parse to fail");
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            boolean found = failure.errors().stream()
                .anyMatch(e -> e.code().map(c -> c.equals(expectedErrorCode)).orElse(false));
            
            assertTrue(found,
                "Expected error code '" + expectedErrorCode + "' but got:\n" +
                failure.formatErrors());
        }
    }
    
    /**
     * Get position map from successful parse.
     */
    public static PositionMap getPositionMap(String source) {
        var result = Reader.parseExpressionWithPositions(source);
        return result.positionMap();
    }
    
    /**
     * Get position map from declarations parse.
     */
    public static PositionMap getDeclarationsPositionMap(String source) {
        var result = Reader.parseDeclarationsWithPositions(source);
        return result.positionMap();
    }
    
    /**
     * Convert expression to automaton or fail test.
     */
    public static Automaton convertToAutomatonOrFail(Expression expression) {
        ParseResult<Automaton> result = Expression2BuchiAutomaton.convert(expression);
        
        if (result instanceof ParseResult.Failure<Automaton> failure) {
            fail("Automaton conversion failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Automaton>) result).value();
    }
}
