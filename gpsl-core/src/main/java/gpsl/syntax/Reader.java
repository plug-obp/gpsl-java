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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides utilities for reading and parsing GPSL expressions and declarations.
 * This class creates ANTLR4 parsers, builds syntax models, and links symbol references.
 */
public class Reader {

    /**
     * Creates an ANTLR4 parser for the given input string.
     *
     * @param input the GPSL source code to parse
     * @return a configured GPSLParser instance
     */
    public static GPSLParser antlr4Parser(String input) {
        CharStream chars = CharStreams.fromString(input);
        GPSLLexer lexer = new GPSLLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new GPSLParser(tokens);
    }

    /**
     * Builds a syntax model from an ANTLR4 parse tree.
     *
     * @param antlr4Tree the ANTLR4 parse tree
     * @param <T> the expected return type
     * @return the syntax model element corresponding to the parse tree
     */
    private static <T> T buildSyntaxModel(ParserRuleContext antlr4Tree) {
        Antlr4ToGPSLMapper syntaxBuilder = new Antlr4ToGPSLMapper();
        ParseTreeWalker.DEFAULT.walk(syntaxBuilder, antlr4Tree);
        return syntaxBuilder.getValue(antlr4Tree);
    }

    /**
     * Reads and parses a GPSL expression from the input string.
     *
     * @param input the GPSL expression as a string
     * @return the parsed Expression
     */
    public static Expression readExpression(String input) {
        GPSLParser parser = antlr4Parser(input);
        GPSLParser.FormulaContext tree = parser.formula();
        return buildSyntaxModel(tree);
    }

    /**
     * Reads and parses GPSL declarations from the input string.
     *
     * @param input the GPSL declarations as a string
     * @return the parsed Declarations
     */
    public static Declarations readDeclarations(String input) {
        GPSLParser parser = antlr4Parser(input);
        GPSLParser.BlockContext tree = parser.block();
        return buildSyntaxModel(tree);
    }

    /**
     * Links symbol references in a syntax tree using the provided context.
     * This resolves all named references to their definitions.
     *
     * @param tree the syntax tree to link
     * @param context the symbol context (may be null for empty context)
     */
    public static void link(Declarations tree, Map<String, Object> context) {
        Context symbolContext = context != null ? new Context(context) : new Context();
        tree.accept(new SymbolResolver(), symbolContext);
    }

    /**
     * Links symbol references in a syntax tree using an empty context.
     *
     * @param tree the syntax tree to link
     */
    public static void link(Declarations tree) {
        link(tree, new HashMap<>());
    }

    /**
     * Links symbol references in an automaton using the provided context.
     * This resolves atom references in transition guards.
     *
     * @param automaton the automaton to link
     * @param context the symbol context (atom name to Atom mapping)
     */
    public static void link(Automaton automaton, Map<String, Object> context) {
        Context symbolContext = context != null ? new Context(context) : new Context();
        automaton.accept(new SymbolResolver(), symbolContext);
    }

    /**
     * Links symbol references in an automaton using an empty context.
     *
     * @param automaton the automaton to link
     */
    public static void link(Automaton automaton) {
        link(automaton, new HashMap<>());
    }

    /**
     * Reads GPSL declarations from input and links all symbol references.
     * This is a convenience method that combines reading and linking.
     *
     * @param input the GPSL declarations as a string
     * @return the parsed and linked Declarations
     */
    public static Declarations readAndLinkDeclarations(String input) {
        Declarations declarations = readDeclarations(input);
        link(declarations);
        return declarations;
    }

    /**
     * Reads GPSL declarations from input and links symbol references using the provided context.
     *
     * @param input the GPSL declarations as a string
     * @param context the initial symbol context
     * @return the parsed and linked Declarations
     */
    public static Declarations readAndLinkDeclarations(String input, Map<String, Object> context) {
        Declarations declarations = readDeclarations(input);
        link(declarations, context);
        return declarations;
    }
}
