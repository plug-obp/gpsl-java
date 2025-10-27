package gpsl.syntax;

import gpsl.syntax.model.*;
import rege.reader.infra.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.*;

/**
 * Maps AST nodes to their source positions.
 * This is the "debug info" for the GPSL compiler, similar to DWARF for C.
 * 
 * <p>Position tracking is optional - AST nodes don't carry positions.
 * This allows:
 * <ul>
 *   <li>Clean AST without position clutter</li>
 *   <li>Memory efficiency (discard positions after parsing)</li>
 *   <li>Multiple source maps (original, preprocessed, generated)</li>
 *   <li>Simple testing (no need to create positions)</li>
 * </ul>
 */
public class PositionMap {
    
    private final Map<SyntaxTreeElement, Range> positions = new IdentityHashMap<>();
    private final Map<SyntaxTreeElement, ParserRuleContext> parseTreeNodes = new IdentityHashMap<>();
    
    /**
     * Record the position of an AST node.
     */
    public void put(SyntaxTreeElement node, Range range) {
        positions.put(node, range);
    }
    
    /**
     * Record the ANTLR4 parse tree node for an AST node.
     * Useful for error reporting and IDE features.
     */
    public void putParseTree(SyntaxTreeElement node, ParserRuleContext ctx) {
        parseTreeNodes.put(node, ctx);
    }
    
    /**
     * Get the position of an AST node.
     * @return the range, or empty if not tracked
     */
    public Optional<Range> get(SyntaxTreeElement node) {
        return Optional.ofNullable(positions.get(node));
    }
    
    /**
     * Get the ANTLR4 parse tree node for an AST node.
     * @return the parse tree context, or empty if not tracked
     */
    public Optional<ParserRuleContext> getParseTree(SyntaxTreeElement node) {
        return Optional.ofNullable(parseTreeNodes.get(node));
    }
    
    /**
     * Get position or throw if not found.
     * Useful for error reporting where position is required.
     */
    public Range getOrThrow(SyntaxTreeElement node) {
        return get(node).orElseThrow(() -> 
            new IllegalStateException("No position tracked for node: " + node));
    }
    
    /**
     * Check if a node has position tracking.
     */
    public boolean contains(SyntaxTreeElement node) {
        return positions.containsKey(node);
    }
    
    /**
     * Get all tracked nodes.
     * Useful for debugging and analysis.
     */
    public Set<SyntaxTreeElement> trackedNodes() {
        return Collections.unmodifiableSet(positions.keySet());
    }
    
    /**
     * Create a range from ANTLR4 context.
     */
    public static Range rangeOf(ParserRuleContext ctx) {
        Token start = ctx.getStart();
        Token stop = ctx.getStop() != null ? ctx.getStop() : start;
        
        Position startPos = new Position(
            start.getLine(),
            start.getCharPositionInLine() + 1,
            start.getStartIndex()
        );
        
        // For multiline tokens, ANTLR doesn't correctly track the stop line number.
        // We need to calculate it ourselves by counting newlines in the token text.
        String tokenText = stop.getText();
        int newlineCount = 0;
        int lastNewlineIndex = -1;
        
        for (int i = 0; i < tokenText.length(); i++) {
            if (tokenText.charAt(i) == '\n') {
                newlineCount++;
                lastNewlineIndex = i;
            }
        }
        
        // Calculate the actual end line and column
        int endLine = stop.getLine() + newlineCount;
        int endColumn;
        
        if (lastNewlineIndex >= 0) {
            // Multiline token: column is relative to the last newline
            String lastLine = tokenText.substring(lastNewlineIndex + 1);
            // For the last line, we start at column 1 (since we're after a newline)
            endColumn = lastLine.length() + 1;
        } else {
            // Single-line token: use stop position + token length
            endColumn = stop.getCharPositionInLine() + tokenText.length() + 1;
        }
        
        Position endPos = new Position(
            endLine,
            endColumn,
            stop.getStopIndex() + 1
        );
        
        return new Range(startPos, endPos);
    }
    
    /**
     * Merge two position maps.
     * Useful when combining multiple parse results.
     */
    public void mergeFrom(PositionMap other) {
        positions.putAll(other.positions);
        parseTreeNodes.putAll(other.parseTreeNodes);
    }
    
    /**
     * Clear all tracking data.
     * Use this to free memory after errors are reported.
     */
    public void clear() {
        positions.clear();
        parseTreeNodes.clear();
    }
    
    /**
     * Get memory footprint estimate in bytes.
     */
    public long estimatedSize() {
        // Rough estimate: each entry ~100 bytes (Range + references)
        return (long) positions.size() * 100;
    }
}
