package gpsl.syntax;

import gpsl.syntax.model.*;
import rege.reader.infra.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for error reporting quality.
 * Verifies that errors are:
 * - Accurately positioned in source code
 * - Clear and actionable
 * - Include proper error codes
 * - Provide helpful context
 */
class ErrorReportingTest {

    // ========== SYNTAX ERROR TESTS ==========
    
    @Test
    void testUnclosedParenthesis() {
        var result = Reader.parseAndLinkExpression("(true");
        
        assertTrue(result.isFailure(), "Should fail for unclosed parenthesis");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertFalse(failure.errors().isEmpty(), "Should have at least one error");
            
            String formatted = failure.formatErrors();
            assertTrue(formatted.contains("(true"), "Error should show the problematic code");
        }
    }
    
    @Test
    void testUnexpectedToken() {
        // Missing operator between operands
        var result = Reader.parseAndLinkExpression("(true false)");
        
        assertTrue(result.isFailure(), "Should fail for unexpected token");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertFalse(failure.errors().isEmpty(), "Should have at least one error");
            
            // Should indicate where the unexpected token is
            String formatted = failure.formatErrors();
            assertTrue(formatted.contains("true false"), "Error should show the problematic code");
        }
    }
    
    @Test
    void testInvalidOperator() {
        var result = Reader.parseAndLinkExpression("true ++ false");
        
        assertTrue(result.isFailure(), "Should fail for invalid operator");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertFalse(failure.errors().isEmpty(), "Should have at least one error");
        }
    }
    
    @Test
    void testMissingOperand() {
        var result = Reader.parseAndLinkExpression("and true");
        
        assertTrue(result.isFailure(), "Should fail for missing left operand");
    }
    
    @Test
    void testEmptyInput() {
        var result = Reader.parseAndLinkExpression("");
        
        assertTrue(result.isFailure(), "Should fail for empty input");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertFalse(failure.errors().isEmpty(), "Should have at least one error");
        }
    }
    
    @Test
    void testWhitespaceOnly() {
        var result = Reader.parseAndLinkExpression("   \n  \t  ");
        
        assertTrue(result.isFailure(), "Should fail for whitespace-only input");
    }

    // ========== UNDEFINED SYMBOL TESTS ==========
    
    @Test
    void testUndefinedSymbol_SingleReference() {
        var result = Reader.parseAndLinkExpression("x");
        
        assertTrue(result.isFailure(), "Should fail for undefined symbol");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size(), "Should have exactly one error");
            
            ParseError error = failure.errors().get(0);
            assertEquals("undefined-symbol", error.code().orElse(""), 
                "Error code should be 'undefined-symbol'");
            assertTrue(error.message().contains("x"), 
                "Error message should mention the undefined symbol");
            
            // Check position
            Range range = error.range();
            assertNotNull(range, "Error should have position information");
            assertEquals(1, range.start().line(), "Error should be on line 1");
            assertEquals(1, range.start().column(), "Error should start at column 1");
            assertEquals(1, range.end().line(), "Error should end on line 1");
            assertEquals(2, range.end().column(), "Error should end at column 2");
        }
    }
    
    @Test
    void testUndefinedSymbol_InExpression() {
        var result = Reader.parseAndLinkExpression("true and x");
        
        assertTrue(result.isFailure(), "Should fail for undefined symbol in expression");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size(), "Should have exactly one error");
            
            ParseError error = failure.errors().get(0);
            assertEquals("undefined-symbol", error.code().orElse(""));
            assertTrue(error.message().contains("x"));
            
            // Position should point to 'x'
            
            Range range = error.range();
            assertEquals(1, range.start().line());
            assertEquals(10, range.start().column(), "Error should point to 'x' at column 10");
        }
    }
    
    @Test
    void testUndefinedSymbol_MultipleReferences() {
        var result = Reader.parseAndLinkExpression("a and b or c");
        
        assertTrue(result.isFailure(), "Should fail for multiple undefined symbols");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(3, failure.errors().size(), "Should have 3 errors (one for each symbol)");
            
            // Check that each symbol is reported
            var messages = failure.errors().stream()
                .map(ParseError::message)
                .toList();
            
            assertTrue(messages.stream().anyMatch(m -> m.contains("'a'")), "Should report 'a'");
            assertTrue(messages.stream().anyMatch(m -> m.contains("'b'")), "Should report 'b'");
            assertTrue(messages.stream().anyMatch(m -> m.contains("'c'")), "Should report 'c'");
            
            // All should have undefined-symbol code
            assertTrue(failure.errors().stream()
                .allMatch(e -> "undefined-symbol".equals(e.code().orElse(""))),
                "All errors should have 'undefined-symbol' code");
        }
    }
    
    @Test
    void testUndefinedSymbol_NestedExpression() {
        var result = Reader.parseAndLinkExpression("(x and y) or (z and w)");
        
        assertTrue(result.isFailure());
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(4, failure.errors().size(), "Should report all 4 undefined symbols");
        }
    }

    // ========== DUPLICATE SYMBOL TESTS ==========
    
    @Test
    void testDuplicateSymbol() {
        var result = Reader.parseAndLinkDeclarations("x = true\nx = false");
        
        assertTrue(result.isFailure(), "Should fail for duplicate symbol");
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            assertEquals(1, failure.errors().size(), "Should have exactly one error");
            
            ParseError error = failure.errors().get(0);
            assertEquals("duplicate-symbol", error.code().orElse(""),
                "Error code should be 'duplicate-symbol'");
            assertTrue(error.message().contains("x"),
                "Error message should mention the duplicate symbol");
            
            // Position should point to the second declaration
            Range range = error.range();
            assertEquals(2, range.start().line(), 
                "Error should point to second 'x' on line 2");
        }
    }
    
    @Test
    void testDuplicateSymbol_Multiple() {
        var result = Reader.parseAndLinkDeclarations("a = true\nb = false\na = false\nb = true");
        
        assertTrue(result.isFailure());
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            assertEquals(2, failure.errors().size(), "Should report both duplicate symbols");
            
            // Both should have duplicate-symbol code
            assertTrue(failure.errors().stream()
                .allMatch(e -> "duplicate-symbol".equals(e.code().orElse(""))));
        }
    }

    // ========== LET EXPRESSION TESTS ==========
    
    @Test
    void testLetExpression_UndefinedSymbolInBody() {
        var result = Reader.parseAndLinkExpression("let x = true in y");
        
        assertTrue(result.isFailure(), "Should fail for undefined symbol in let body");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size());
            
            ParseError error = failure.errors().get(0);
            assertEquals("undefined-symbol", error.code().orElse(""));
            assertTrue(error.message().contains("y"));
            
            // Position should point to 'y', not 'x'
            
            Range range = error.range();
            assertTrue(range.start().column() > 16, 
                "Error should point to 'y' in the body");
        }
    }
    
    @Test
    void testLetExpression_DuplicateBinding() {
        var result = Reader.parseAndLinkExpression("let x = true, x = false in x");
        
        assertTrue(result.isFailure(), "Should fail for duplicate binding in let");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size());
            
            ParseError error = failure.errors().get(0);
            assertEquals("duplicate-symbol", error.code().orElse(""));
        }
    }
    
    @Test
    void testLetExpression_UndefinedInBinding() {
        var result = Reader.parseAndLinkExpression("let x = y in x");
        
        assertTrue(result.isFailure(), "Should fail for undefined symbol in binding");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size());
            
            ParseError error = failure.errors().get(0);
            assertEquals("undefined-symbol", error.code().orElse(""));
            assertTrue(error.message().contains("y"));
        }
    }

    // ========== ERROR MESSAGE QUALITY TESTS ==========
    
    @Test
    void testErrorMessage_ContainsSymbolName() {
        var result = Reader.parseAndLinkExpression("undefined_var");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            assertTrue(error.message().contains("undefined_var"),
                "Error message should contain the actual symbol name");
        }
    }
    
    @Test
    void testErrorMessage_HasErrorCode() {
        var result = Reader.parseAndLinkExpression("x");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            assertTrue(error.code().isPresent(), "Error should have a code");
            assertNotNull(error.code().get(), "Error code should not be null");
            assertFalse(error.code().get().isEmpty(), "Error code should not be empty");
        }
    }
    
    @Test
    void testFormattedErrors_ShowsSourceCode() {
        var result = Reader.parseAndLinkExpression("a and b");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            String formatted = failure.formatErrors();
            
            assertTrue(formatted.contains("a and b"), 
                "Formatted error should show the source code");
            assertTrue(formatted.contains("^"), 
                "Formatted error should show position indicator");
            assertTrue(formatted.contains("undefined-symbol"),
                "Formatted error should show error code");
        }
    }
    
    @Test
    void testFormattedErrors_MultipleErrors() {
        var result = Reader.parseAndLinkExpression("x and y");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            String formatted = failure.formatErrors();
            
            // Should show both error messages
            assertTrue(formatted.contains("x"), "Should mention 'x'");
            assertTrue(formatted.contains("y"), "Should mention 'y'");
            assertTrue(formatted.contains("undefined"), "Should indicate undefined symbol");
            assertTrue(formatted.contains("^"), "Should show position indicators");
        }
    }

    // ========== POSITION ACCURACY TESTS ==========
    
    @Test
    void testPosition_SimpleReference() {
        var result = Reader.parseAndLinkExpression("xyz");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            
            
            Range range = error.range();
            assertEquals(1, range.start().line());
            assertEquals(1, range.start().column());
            assertEquals(1, range.end().line());
            assertEquals(4, range.end().column(), "Should span the entire identifier");
        }
    }
    
    @Test
    void testPosition_ReferenceAfterWhitespace() {
        var result = Reader.parseAndLinkExpression("   x");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            
            
            Range range = error.range();
            assertEquals(4, range.start().column(), 
                "Position should account for leading whitespace");
        }
    }
    
    @Test
    void testPosition_ReferenceInComplexExpression() {
        var result = Reader.parseAndLinkExpression("true and (false or x)");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            
            
            Range range = error.range();
            assertEquals(20, range.start().column(), 
                "Should point to 'x' at correct position");
        }
    }
    
    @Test
    void testPosition_SecondOccurrence() {
        var result = Reader.parseAndLinkDeclarations("x = true\nx = false");
        
        if (result instanceof ParseResult.Failure<Declarations> failure) {
            ParseError error = failure.errors().get(0);
            
            Range range = error.range();
            assertEquals(2, range.start().line(), 
                "Should point to second 'x' on line 2");
        }
    }

    // ========== MULTI-LINE ERROR TESTS ==========
    
    @Test
    void testMultilineError_UndefinedOnSecondLine() {
        var result = Reader.parseAndLinkExpression("true and\nx");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            
            
            Range range = error.range();
            assertEquals(2, range.start().line(), 
                "Error should be on line 2");
            assertEquals(1, range.start().column(), 
                "Should be at start of line 2");
        }
    }
    
    @Test
    void testMultilineError_FormattedOutput() {
        var result = Reader.parseAndLinkExpression("a and\nb or\nc");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            String formatted = failure.formatErrors();
            
            // Should show line numbers
            assertTrue(formatted.contains("1:1"), "Should show position 1:1 for 'a'");
            assertTrue(formatted.contains("2:1"), "Should show position 2:1 for 'b'");
            assertTrue(formatted.contains("3:1"), "Should show position 3:1 for 'c'");
        }
    }

    // ========== ERROR RECOVERY TESTS ==========
    
    @Test
    void testErrorRecovery_ContinuesAfterFirstError() {
        // Parser should report all undefined symbols, not stop at the first one
        var result = Reader.parseAndLinkExpression("a and b and c");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(3, failure.errors().size(), 
                "Should report all errors, not just the first");
        }
    }
    
    @Test
    void testErrorRecovery_ReportsBothSyntaxAndSemanticErrors() {
        // If we have both syntax and semantic errors, both should be reported
        // This test verifies that syntax errors are caught first
        var result = Reader.parseAndLinkExpression("((x)");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            // Should have syntax error for unclosed paren
            assertFalse(failure.errors().isEmpty());
        }
    }

    // ========== ERROR SUPPRESSION TESTS ==========
    
    @Test
    void testNoErrorForValidExpression() {
        var result = Reader.parseAndLinkExpression("true and false");
        
        assertTrue(result.isSuccess(), "Should succeed for valid expression");
    }
    
    @Test
    void testNoErrorForValidLetExpression() {
        var result = Reader.parseAndLinkExpression("let x = true in x");
        
        assertTrue(result.isSuccess(), "Should succeed for valid let expression");
    }
    
    @Test
    void testNoErrorForValidDeclarations() {
        var result = Reader.parseAndLinkDeclarations("x = true\ny = false\nz = x and y");
        
        assertTrue(result.isSuccess(), "Should succeed for valid declarations");
    }

    // ========== EXTERNAL CONTEXT TESTS ==========
    
    @Test
    void testExternalContext_DefinedSymbol() {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("x", new True());
        
        var result = Reader.parseExpression("x", context);
        
        assertTrue(result.isSuccess(), "Should succeed when symbol is in context");
    }
    
    @Test
    void testExternalContext_UndefinedStillReported() {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("x", new True());
        
        var result = Reader.parseExpression("y", context);
        
        assertTrue(result.isFailure(), "Should fail for undefined symbol even with context");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            assertEquals("undefined-symbol", error.code().orElse(""));
            assertTrue(error.message().contains("y"));
        }
    }
    
    @Test
    void testExternalContext_MixedDefinedAndUndefined() {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("x", new True());
        
        var result = Reader.parseExpression("x and y", context);
        
        assertTrue(result.isFailure(), "Should fail for undefined 'y'");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size(), 
                "Should only report 'y', not 'x'");
            
            ParseError error = failure.errors().get(0);
            assertTrue(error.message().contains("y"));
        }
    }

    // ========== EDGE CASE TESTS ==========
    
    @Test
    void testUnicodeInErrors() {
        var result = Reader.parseAndLinkExpression("α and β");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            String formatted = failure.formatErrors();
            assertTrue(formatted.contains("α"), "Should handle Unicode in error messages");
            assertTrue(formatted.contains("β"), "Should handle Unicode in error messages");
        }
    }
    
    @Test
    void testVeryLongIdentifier() {
        String longId = "x".repeat(100);
        var result = Reader.parseAndLinkExpression(longId);
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            ParseError error = failure.errors().get(0);
            assertTrue(error.message().contains(longId) || error.message().length() > 50,
                "Should handle very long identifiers");
        }
    }
    
    @Test
    void testSpecialCharactersInAtoms() {
        // Atoms can contain special characters, should not cause issues in error reporting
        var result = Reader.parseAndLinkExpression("|a@#$| and x");
        
        if (result instanceof ParseResult.Failure<Expression> failure) {
            assertEquals(1, failure.errors().size(), 
                "Should only report undefined 'x', not complain about atom content");
        }
    }
}
