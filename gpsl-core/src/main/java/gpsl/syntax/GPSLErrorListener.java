package gpsl.syntax;

import org.antlr.v4.runtime.*;
import rege.reader.infra.*;
import java.util.*;

/**
 * Collects ANTLR4 syntax errors into ParseContext.
 * Compatible with LSP diagnostics.
 */
public class GPSLErrorListener extends BaseErrorListener {
    
    private final ParseContext context;
    
    public GPSLErrorListener(ParseContext context) {
        this.context = context;
    }
    
    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {
        
        Token token = offendingSymbol instanceof Token ? (Token) offendingSymbol : null;
        
        Position start;
        Position end;
        
        if (token != null) {
            int startOffset = token.getStartIndex();
            int length = Math.max(1, token.getStopIndex() - token.getStartIndex() + 1);
            
            start = new Position(line, charPositionInLine + 1, startOffset);
            end = new Position(line, charPositionInLine + 1 + length, startOffset + length);
        } else {
            // Fallback: estimate offset from line/column
            String[] lines = context.source().split("\n", -1);
            int offset = 0;
            for (int i = 0; i < line - 1 && i < lines.length; i++) {
                offset += lines[i].length() + 1; // +1 for newline
            }
            offset += charPositionInLine;
            
            start = new Position(line, charPositionInLine + 1, offset);
            end = new Position(line, charPositionInLine + 2, offset + 1);
        }
        
        Range range = new Range(start, end);
        
        ParseError error = new ParseError(
            range,
            simplifyMessage(msg),
            ParseError.Severity.ERROR,
            Optional.of("syntax-error")
        );
        
        context.addError(error);
    }
    
    /**
     * Simplify ANTLR's verbose error messages.
     */
    private String simplifyMessage(String msg) {
        // "mismatched input '&&' expecting ..." → "unexpected '&&'"
        if (msg.startsWith("mismatched input")) {
            return msg.replaceFirst("mismatched input (.+?) expecting.*", "unexpected $1");
        }
        
        // "extraneous input '&&' expecting ..." → "unexpected '&&'"
        if (msg.startsWith("extraneous input")) {
            return msg.replaceFirst("extraneous input (.+?) expecting.*", "unexpected $1");
        }
        
        // Keep other messages as-is
        return msg;
    }
}
