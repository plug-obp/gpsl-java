package gpsl.syntax;

import gpsl.parser.GPSLLexer;
import gpsl.parser.GPSLParser;
import gpsl.syntax.model.Automaton;
import gpsl.syntax.model.Declarations;
import gpsl.syntax.model.Expression;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import rege.reader.infra.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GPSL parser with comprehensive error reporting and position tracking.
 * All parsing returns ParseResult with errors collected (never thrown).
 */
public class Reader {

    /**
     * Parse a GPSL expression from source text.
     * 
     * @param source the GPSL expression source
     * @return ParseResult containing expression or errors with positions
     */
    public static ParseResult<Expression> parseExpression(String source) {
        ParseContext parseContext = new ParseContext(source);
        
        // Phase 1: Lexing and Parsing
        GPSLParser parser = createParser(source, parseContext);
        GPSLParser.FormulaContext tree = parser.formula();
        
        if (parseContext.hasErrors()) {
            return parseContext.toResult(null);
        }
        
        // Phase 2: Build AST with position tracking
        Expression expr = buildSyntaxModel(tree, parseContext);
        
        // Phase 3: Symbol resolution
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context();
        expr.accept(resolver, symbolContext);
        
        return parseContext.toResult(expr);
    }
    
    /**
     * Parse GPSL expression from source text with external symbol context.
     * This method allows providing predefined symbols (e.g., from external sources).
     * 
     * @param source the GPSL expression source
     * @param externalSymbols map of externally defined symbols (e.g., atoms from LTL3BA)
     * @return ParseResult containing expression or errors
     */
    public static ParseResult<Expression> parseExpressionWithContext(
            String source,
            Map<String, Object> externalSymbols) {
        
        ParseContext parseContext = new ParseContext(source);
        
        GPSLParser parser = createParser(source, parseContext);
        GPSLParser.FormulaContext tree = parser.formula();
        
        if (parseContext.hasErrors()) {
            return parseContext.toResult(null);
        }
        
        Expression expr = buildSyntaxModel(tree, parseContext);
        
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context(externalSymbols);
        expr.accept(resolver, symbolContext);
        
        return parseContext.toResult(expr);
    }
    
    /**
     * Parse GPSL declarations from source text.
     * 
     * @param source the GPSL declarations source
     * @return ParseResult containing declarations or errors
     */
    public static ParseResult<Declarations> parseDeclarations(String source) {
        ParseContext parseContext = new ParseContext(source);
        
        GPSLParser parser = createParser(source, parseContext);
        GPSLParser.BlockContext tree = parser.block();
        
        if (parseContext.hasErrors()) {
            return parseContext.toResult(null);
        }
        
        Declarations decls = buildSyntaxModel(tree, parseContext);
        
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context();
        decls.accept(resolver, symbolContext);
        
        return parseContext.toResult(decls);
    }

    /**
     * Parse with external symbols (for imports).
     * 
     * @param source the source text
     * @param externalSymbols symbols from imported modules
     * @return ParseResult with declarations
     */
    public static ParseResult<Declarations> parseDeclarationsWithContext(
            String source,
            Map<String, Object> externalSymbols) {
        
        ParseContext parseContext = new ParseContext(source);
        
        GPSLParser parser = createParser(source, parseContext);
        GPSLParser.BlockContext tree = parser.block();
        
        if (parseContext.hasErrors()) {
            return parseContext.toResult(null);
        }
        
        Declarations decls = buildSyntaxModel(tree, parseContext);
        
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context(externalSymbols);
        decls.accept(resolver, symbolContext);
        
        return parseContext.toResult(decls);
    }

    /**
     * Parse expression and return with position map.
     * Useful for LSP implementations that need position tracking.
     * 
     * @param source the source text
     * @return ParseResult with expression and accessible position map
     */
    public static ParseResultWithPositions<Expression> parseExpressionWithPositions(String source) {
        ParseContext parseContext = new ParseContext(source);
        ParseResult<Expression> result = parseExpression(source);
        return new ParseResultWithPositions<>(result, parseContext.positionMap());
    }

    /**
     * Parse declarations and return with position map.
     * 
     * @param source the source text
     * @return ParseResult with declarations and accessible position map
     */
    public static ParseResultWithPositions<Declarations> parseDeclarationsWithPositions(String source) {
        ParseContext parseContext = new ParseContext(source);
        ParseResult<Declarations> result = parseDeclarations(source);
        return new ParseResultWithPositions<>(result, parseContext.positionMap());
    }

    /**
     * Creates an ANTLR4 parser for the given input string with error listener.
     *
     * @param source the GPSL source code to parse
     * @param parseContext the parse context for error collection
     * @return a configured GPSLParser instance
     */
    private static GPSLParser createParser(String source, ParseContext parseContext) {
        CharStream chars = CharStreams.fromString(source);
        GPSLLexer lexer = new GPSLLexer(chars);
        
        // Remove default error listeners and add our custom one
        lexer.removeErrorListeners();
        lexer.addErrorListener(new GPSLErrorListener(parseContext));
        
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GPSLParser parser = new GPSLParser(tokens);
        
        // Remove default error listeners and add our custom one
        parser.removeErrorListeners();
        parser.addErrorListener(new GPSLErrorListener(parseContext));
        
        return parser;
    }

    /**
     * Builds a syntax model from an ANTLR4 parse tree.
     *
     * @param antlr4Tree the ANTLR4 parse tree
     * @param parseContext the parse context for position tracking
     * @param <T> the expected return type
     * @return the syntax model element corresponding to the parse tree
     */
    private static <T> T buildSyntaxModel(ParserRuleContext antlr4Tree, ParseContext parseContext) {
        Antlr4ToGPSLMapper syntaxBuilder = new Antlr4ToGPSLMapper(parseContext);
        ParseTreeWalker.DEFAULT.walk(syntaxBuilder, antlr4Tree);
        return syntaxBuilder.getValue(antlr4Tree);
    }

    /**
     * Wrapper for ParseResult that includes position map.
     * Useful for IDE integrations.
     */
    public record ParseResultWithPositions<T>(
        ParseResult<T> result,
        PositionMap positionMap
    ) {
        public java.util.Optional<rege.reader.infra.Range> rangeOf(gpsl.syntax.model.SyntaxTreeElement node) {
            return positionMap.get(node);
        }
    }

    // ========== LEGACY API (for backward compatibility with tests) ==========

    /**
     * Creates an ANTLR4 parser for the given input string.
     * @deprecated Use parseExpression() or parseDeclarations() for better error handling
     */
    @Deprecated
    public static GPSLParser antlr4Parser(String input) {
        CharStream chars = CharStreams.fromString(input);
        GPSLLexer lexer = new GPSLLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new GPSLParser(tokens);
    }

    /**
     * Reads and parses a GPSL expression from the input string.
     * @deprecated Use parseExpression() for better error handling
     */
    @Deprecated
    public static Expression readExpression(String input) {
        try {
            return parseExpression(input).orElseThrow();
        } catch (ParseException e) {
            throw new RuntimeException("Parse error: " + e.getMessage(), e);
        }
    }

    /**
     * Reads and parses GPSL declarations from the input string.
     * @deprecated Use parseDeclarations() for better error handling
     */
    @Deprecated
    public static Declarations readDeclarations(String input) {
        try {
            return parseDeclarations(input).orElseThrow();
        } catch (ParseException e) {
            throw new RuntimeException("Parse error: " + e.getMessage(), e);
        }
    }

    /**
     * Links symbol references in a syntax tree using the provided context.
     * @deprecated Symbol resolution is now integrated into parse methods
     */
    @Deprecated
    public static void link(Declarations tree, Map<String, Object> context) {
        ParseContext parseContext = new ParseContext("");
        Context symbolContext = context != null ? new Context(context) : new Context();
        SymbolResolver resolver = new SymbolResolver(parseContext);
        tree.accept(resolver, symbolContext);
        
        if (parseContext.hasErrors()) {
            throw new RuntimeException("Symbol resolution errors: " + parseContext.errors());
        }
    }

    /**
     * Links symbol references in a syntax tree using an empty context.
     * @deprecated Symbol resolution is now integrated into parse methods
     */
    @Deprecated
    public static void link(Declarations tree) {
        link(tree, new HashMap<>());
    }

    /**
     * Links symbol references in an automaton using the provided context.
     * @deprecated Symbol resolution is now integrated into parse methods
     */
    @Deprecated
    public static void link(Automaton automaton, Map<String, Object> context) {
        ParseContext parseContext = new ParseContext("");
        Context symbolContext = context != null ? new Context(context) : new Context();
        SymbolResolver resolver = new SymbolResolver(parseContext);
        automaton.accept(resolver, symbolContext);
        
        if (parseContext.hasErrors()) {
            throw new RuntimeException("Symbol resolution errors: " + parseContext.errors());
        }
    }

    /**
     * Links symbol references in an automaton using an empty context.
     * @deprecated Symbol resolution is now integrated into parse methods
     */
    @Deprecated
    public static void link(Automaton automaton) {
        link(automaton, new HashMap<>());
    }

    /**
     * Reads GPSL declarations from input and links all symbol references.
     * @deprecated Use parseDeclarations() for better error handling
     */
    @Deprecated
    public static Declarations readAndLinkDeclarations(String input) {
        return readDeclarations(input);
    }

    /**
     * Reads GPSL declarations from input and links symbol references using the provided context.
     * @deprecated Use parseDeclarationsWithContext() for better error handling
     */
    @Deprecated
    public static Declarations readAndLinkDeclarations(String input, Map<String, Object> context) {
        try {
            return parseDeclarationsWithContext(input, context).orElseThrow();
        } catch (ParseException e) {
            throw new RuntimeException("Parse error: " + e.getMessage(), e);
        }
    }
}
