package gpsl.syntax;

import gpsl.syntax.model.*;
import rege.reader.infra.*;
import java.util.*;

/**
 * Context for a parse operation, containing:
 * - The source text
 * - Position map (AST node → source range)
 * - Accumulated errors
 * 
 * This is the "compilation unit" that flows through the parse pipeline.
 */
public class ParseContext {
    
    private final String source;
    private final PositionMap positionMap;
    private final List<ParseError> errors;
    
    public ParseContext(String source) {
        this.source = source;
        this.positionMap = new PositionMap();
        this.errors = new ArrayList<>();
    }
    
    /**
     * Create a ParseContext with an existing PositionMap.
     * Useful for linking phase that reuses positions from parsing phase.
     */
    public ParseContext(String source, PositionMap positionMap) {
        this.source = source;
        this.positionMap = positionMap;
        this.errors = new ArrayList<>();
    }
    
    /**
     * The source text being parsed.
     */
    public String source() {
        return source;
    }
    
    /**
     * Position map for AST nodes.
     */
    public PositionMap positionMap() {
        return positionMap;
    }
    
    /**
     * Accumulated parse errors.
     */
    public List<ParseError> errors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Add an error to the context.
     */
    public void addError(ParseError error) {
        errors.add(error);
    }
    
    /**
     * Add multiple errors.
     */
    public void addErrors(List<ParseError> errors) {
        this.errors.addAll(errors);
    }
    
    /**
     * Check if any errors occurred.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Create a ParseError for an AST node using tracked position.
     */
    public ParseError errorAt(SyntaxTreeElement node, String message, String errorCode) {
        Range range = positionMap.get(node)
            .orElseGet(() -> Range.at(Position.start())); // Fallback to start
        
        return new ParseError(range, message, ParseError.Severity.ERROR, Optional.of(errorCode));
    }
    
    /**
     * Create a ParseResult from this context.
     */
    public <T> ParseResult<T> toResult(T value) {
        if (hasErrors()) {
            return new ParseResult.Failure<>(errors, source);
        }
        return new ParseResult.Success<>(value);
    }
}
