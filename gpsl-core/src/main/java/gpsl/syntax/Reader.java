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
     * Parse GPSL declarations from source text (without symbol resolution).
     * 
     * @param source the GPSL declarations source
     * @return ParseResult containing declarations or errors
     */
    public static ParseResult<Declarations> parseDeclarations(String source) {
        return parse(source, GPSLParser::block, new HashMap<>(), false);
    }

    // ========== LINKING (in-place symbol resolution) ==========
    
    /**
     * Link symbol references in a ParseResultWithPositions from parsing.
     * Preserves source code and position information for error reporting.
     * Works for Expression, Declarations, or Automaton.
     * 
     * @param <T> the type of syntax tree element (Expression, Declarations, or Automaton)
     * @param parseResult the parse result with positions
     * @return ParseResult with linking errors, or success with the linked element
     */
    public static <T extends gpsl.syntax.model.SyntaxTreeElement> ParseResult<T> linkWithPositions(ParseResultWithPositions<T> parseResult) {
        return linkWithPositions(parseResult, new HashMap<>());
    }
    
    /**
     * Link symbol references in a ParseResultWithPositions with external context.
     * Preserves source code and position information for error reporting.
     * Works for Expression, Declarations, or Automaton.
     * 
     * @param <T> the type of syntax tree element (Expression, Declarations, or Automaton)
     * @param parseResult the parse result with positions
     * @param externalSymbols external symbols available for resolution
     * @return ParseResult with linking errors, or success with the linked element
     */
    public static <T extends gpsl.syntax.model.SyntaxTreeElement> ParseResult<T> linkWithPositions(ParseResultWithPositions<T> parseResult, Map<String, Object> externalSymbols) {
        if (parseResult.result() instanceof ParseResult.Failure<T> failure) {
            return failure; // Return parse errors as-is
        }
        
        @SuppressWarnings("unchecked")
        T element = ((ParseResult.Success<T>) parseResult.result()).value();
        ParseContext parseContext = new ParseContext(parseResult.source(), parseResult.positionMap());
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context(externalSymbols);
        element.accept(resolver, symbolContext);
        return parseContext.toResult(element);
    }
    
    /**
     * Link symbol references in an expression without position tracking.
     * Useful for programmatically constructed ASTs.
     * Errors will not have source code or position information.
     * 
     * @param expression the expression to link
     * @return ParseResult with linking errors, or success with the same expression
     */
    public static ParseResult<Expression> link(Expression expression) {
        return link(expression, new HashMap<>());
    }
    
    /**
     * Link symbol references in an expression with external context, without position tracking.
     * Useful for programmatically constructed ASTs.
     * Errors will not have source code or position information.
     * 
     * @param expression the expression to link
     * @param externalSymbols external symbols available for resolution
     * @return ParseResult with linking errors, or success with the same expression
     */
    public static ParseResult<Expression> link(Expression expression, Map<String, Object> externalSymbols) {
        ParseContext parseContext = new ParseContext("");
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context(externalSymbols);
        expression.accept(resolver, symbolContext);
        return parseContext.toResult(expression);
    }
    
    /**
     * Link symbol references in declarations without position tracking.
     * Useful for programmatically constructed ASTs.
     * Errors will not have source code or position information.
     * 
     * @param declarations the declarations to link
     * @return ParseResult with linking errors, or success with the same declarations
     */
    public static ParseResult<Declarations> link(Declarations declarations) {
        return link(declarations, new HashMap<>());
    }
    
    /**
     * Link symbol references in declarations with external context, without position tracking.
     * Useful for programmatically constructed ASTs.
     * Errors will not have source code or position information.
     * 
     * @param declarations the declarations to link
     * @param externalSymbols external symbols available for resolution
     * @return ParseResult with linking errors, or success with the same declarations
     */
    public static ParseResult<Declarations> link(Declarations declarations, Map<String, Object> externalSymbols) {
        ParseContext parseContext = new ParseContext("");
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context(externalSymbols);
        declarations.accept(resolver, symbolContext);
        return parseContext.toResult(declarations);
    }
    
    /**
     * Link symbol references in an automaton without position tracking.
     * Useful for programmatically constructed ASTs.
     * Errors will not have source code or position information.
     * 
     * @param automaton the automaton to link
     * @return ParseResult with linking errors, or success with the same automaton
     */
    public static ParseResult<Automaton> link(Automaton automaton) {
        return link(automaton, new HashMap<>());
    }
    
    /**
     * Link symbol references in an automaton with external context, without position tracking.
     * Useful for programmatically constructed ASTs.
     * Errors will not have source code or position information.
     * 
     * @param automaton the automaton to link
     * @param externalSymbols external symbols available for resolution
     * @return ParseResult with linking errors, or success with the same automaton
     */
    public static ParseResult<Automaton> link(Automaton automaton, Map<String, Object> externalSymbols) {
        ParseContext parseContext = new ParseContext("");
        SymbolResolver resolver = new SymbolResolver(parseContext);
        Context symbolContext = new Context(externalSymbols);
        automaton.accept(resolver, symbolContext);
        return parseContext.toResult(automaton);
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
        
        // Phase 1: Lexing and Parsing
        GPSLParser parser = createParser(source, parseContext);
        ParserRuleContext tree = parser.formula();
        
        ParseResult<Expression> result;
        if (parseContext.hasErrors()) {
            result = parseContext.toResult(null);
        } else {
            // Phase 2: Build AST with position tracking
            Expression model = buildSyntaxModel(tree, parseContext);
            result = parseContext.toResult(model);
        }
        
        return new ParseResultWithPositions<>(result, source, parseContext.positionMap());
    }

    /**
     * Parse declarations and return with position map.
     * 
     * @param source the source text
     * @return ParseResult with declarations and accessible position map
     */
    public static ParseResultWithPositions<Declarations> parseDeclarationsWithPositions(String source) {
        ParseContext parseContext = new ParseContext(source);
        
        // Phase 1: Lexing and Parsing
        GPSLParser parser = createParser(source, parseContext);
        ParserRuleContext tree = parser.block();
        
        ParseResult<Declarations> result;
        if (parseContext.hasErrors()) {
            result = parseContext.toResult(null);
        } else {
            // Phase 2: Build AST with position tracking
            Declarations model = buildSyntaxModel(tree, parseContext);
            result = parseContext.toResult(model);
        }
        
        return new ParseResultWithPositions<>(result, source, parseContext.positionMap());
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
     * Wrapper for ParseResult that includes position map and source.
     * Useful for IDE integrations and for linking phase that needs both.
     */
    public record ParseResultWithPositions<T>(
        ParseResult<T> result,
        String source,
        PositionMap positionMap
    ) {
        public java.util.Optional<rege.reader.infra.Range> rangeOf(gpsl.syntax.model.SyntaxTreeElement node) {
            return positionMap.get(node);
        }
    }
}
