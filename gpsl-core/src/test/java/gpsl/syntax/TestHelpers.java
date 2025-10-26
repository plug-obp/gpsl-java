package gpsl.syntax;

import gpsl.syntax.model.*;
import rege.reader.infra.*;
import static org.junit.jupiter.api.Assertions.*;
import static gpsl.syntax.TestHelpers.*;

/**
 * Test utilities for GPSL parsing.
 * Provides helper methods to make tests cleaner when using ParseResult API.
 */
public class TestHelpers {
    
    /**
     * Parse expression or fail test with formatted error.
     */
    public static Expression parseExpressionOrFail(String source) {
        ParseResult<Expression> result = Reader.parseExpression(source);
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            fail("Parse failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Expression>) result).value();
    }
    
    /**
     * Parse expression without symbol resolution (for AST-only tests).
     * This is useful for testing the Antlr4ToGPSLMapper without requiring defined symbols.
     */
    public static Expression parseExpressionWithoutResolution(String source) {
        // Create a context with permissive mode that allows undefined symbols
        java.util.Map<String, Object> permissiveContext = new java.util.HashMap<>();
        // We'll just extract all identifiers and add them as "dummy" symbols
        // This is a hack for testing, but it allows AST construction tests to work
        
        // For now, we'll try parsing with an empty context and if it fails with undefined-symbol,
        // we'll extract the symbol names and add them
        ParseResult<Expression> result = Reader.parseExpression(source);
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            // Extract undefined symbols from errors and retry with dummy expressions
            for (ParseError error : failure.errors()) {
                if (error.code().map(c -> c.equals("undefined-symbol")).orElse(false)) {
                    // Extract symbol name from error message
                    String msg = error.message();
                    if (msg.contains("undefined symbol '")) {
                        int start = msg.indexOf("'") + 1;
                        int end = msg.indexOf("'", start);
                        String symbolName = msg.substring(start, end);
                        // Use a True expression as a dummy placeholder
                        permissiveContext.put(symbolName, new True());
                    }
                }
            }
            
            // Retry with the symbols defined
            result = Reader.parseExpressionWithContext(source, permissiveContext);
        }
        
        if (result instanceof ParseResult.Failure<Expression> failure2) {
            fail("Parse failed:\n" + failure2.formatErrors());
        }
        
        return ((ParseResult.Success<Expression>) result).value();
    }
    
    /**
     * Parse declarations or fail test.
     */
    public static Declarations parseDeclarationsOrFail(String source) {
        ParseResult<Declarations> result = Reader.parseDeclarations(source);
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            fail("Parse failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Declarations>) result).value();
    }
    
    /**
     * Parse declarations with external context or fail test.
     */
    public static Declarations parseDeclarationsOrFail(String source, java.util.Map<String, Object> context) {
        ParseResult<Declarations> result = Reader.parseDeclarationsWithContext(source, context);
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            fail("Parse failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Declarations>) result).value();
    }
    
    /**
     * Assert that parsing fails with specific error code.
     */
    public static void assertParseError(String source, String expectedErrorCode) {
        ParseResult<Expression> result = Reader.parseExpression(source);
        
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
     */
    public static void assertParseErrorAt(String source, int line, int column, String expectedCode) {
        ParseResult<Expression> result = Reader.parseExpression(source);
        
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
     */
    public static void assertDeclarationsParseError(String source, String expectedErrorCode) {
        ParseResult<Declarations> result = Reader.parseDeclarations(source);
        
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
        ParseResult<Automaton> result = gpsl.ltl3ba.Expression2Automaton.convert(expression);
        
        if (result instanceof ParseResult.Failure<Automaton> failure) {
            fail("Automaton conversion failed:\n" + failure.formatErrors());
        }
        
        return ((ParseResult.Success<Automaton>) result).value();
    }
}
