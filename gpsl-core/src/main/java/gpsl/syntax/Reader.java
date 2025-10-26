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
     * Function that extracts a parse tree from a parser.
     */
    @FunctionalInterface
    public interface ParserFunction {
        ParserRuleContext parse(GPSLParser parser);
    }

    /**
     * Core parsing method - all other methods delegate to this.
     * 
     * @param source the source text to parse
     * @param parserFn function that calls the appropriate parser method (e.g., GPSLParser::formula)
     * @param externalSymbols external symbols for symbol resolution (can be empty, ignored if doLink is false)
     * @param doLink whether to perform symbol resolution/linking
     * @param <T> the expected return type
     * @return ParseResult containing the parsed model or errors
     */
    private static <T> ParseResult<T> parse(
            String source,
            ParserFunction parserFn,
            Map<String, Object> externalSymbols,
            boolean doLink) {
        
        ParseContext parseContext = new ParseContext(source);
        
        // Phase 1: Lexing and Parsing
        GPSLParser parser = createParser(source, parseContext);
        ParserRuleContext tree = parserFn.parse(parser);
        
        if (parseContext.hasErrors()) {
            return parseContext.toResult(null);
        }
        
        // Phase 2: Build AST with position tracking
        T model = buildSyntaxModel(tree, parseContext);
        
        // Phase 3: Symbol resolution (optional)
        if (doLink) {
            SymbolResolver resolver = new SymbolResolver(parseContext);
            Context symbolContext = new Context(externalSymbols);
            ((gpsl.syntax.model.SyntaxTreeElement) model).accept(resolver, symbolContext);
        }
        
        return parseContext.toResult(model);
    }

    /**
     * Parse a GPSL expression from source text (without symbol resolution).
     * 
     * @param source the GPSL expression source
     * @return ParseResult containing expression or errors with positions
     */
    public static ParseResult<Expression> parseExpression(String source) {
        return parse(source, GPSLParser::formula, new HashMap<>(), false);
    }
    
    /**
     * Parse GPSL expression from source text with external symbol context.
     * This method performs symbol resolution with the provided external symbols.
     * 
     * @param source the GPSL expression source
     * @param externalSymbols map of externally defined symbols (e.g., atoms from LTL3BA)
     * @return ParseResult containing expression or errors
     */
    public static ParseResult<Expression> parseExpression(
            String source,
            Map<String, Object> externalSymbols) {
        return parse(source, GPSLParser::formula, externalSymbols, true);
    }
    
    /**
     * Parse GPSL declarations from source text (without symbol resolution).
     * 
     * @param source the GPSL declarations source
     * @return ParseResult containing declarations or errors
     */
    public static ParseResult<Declarations> parseDeclarations(String source) {
        return parse(source, GPSLParser::block, new HashMap<>(), false);
    }

    /**
     * Parse GPSL declarations from source text with external symbol context.
     * This method performs symbol resolution with the provided external symbols.
     * 
     * @param source the source text
     * @param externalSymbols symbols from imported modules
     * @return ParseResult with declarations
     */
    public static ParseResult<Declarations> parseDeclarations(
            String source,
            Map<String, Object> externalSymbols) {
        return parse(source, GPSLParser::block, externalSymbols, true);
    }
    
    /**
     * Parse and link GPSL expression (performs symbol resolution).
     * 
     * @param source the GPSL expression source
     * @return ParseResult containing linked expression or errors
     */
    public static ParseResult<Expression> parseAndLinkExpression(String source) {
        return parse(source, GPSLParser::formula, new HashMap<>(), true);
    }
    
    /**
     * Parse and link GPSL declarations (performs symbol resolution).
     * 
     * @param source the GPSL declarations source
     * @return ParseResult containing linked declarations or errors
     */
    public static ParseResult<Declarations> parseAndLinkDeclarations(String source) {
        return parse(source, GPSLParser::block, new HashMap<>(), true);
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
     * @deprecated Use parseAndLinkDeclarations() for better error handling
     */
    @Deprecated
    public static Declarations readAndLinkDeclarations(String input) {
        try {
            return parseAndLinkDeclarations(input).orElseThrow();
        } catch (ParseException e) {
            throw new RuntimeException("Parse error: " + e.getMessage(), e);
        }
    }

    /**
     * Reads GPSL declarations from input and links symbol references using the provided context.
     * @deprecated Use parseDeclarations(source, externalSymbols) for better error handling
     */
    @Deprecated
    public static Declarations readAndLinkDeclarations(String input, Map<String, Object> context) {
        try {
            return parseDeclarations(input, context).orElseThrow();
        } catch (ParseException e) {
            throw new RuntimeException("Parse error: " + e.getMessage(), e);
        }
    }
}
