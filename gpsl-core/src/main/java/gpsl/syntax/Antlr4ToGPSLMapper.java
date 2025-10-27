package gpsl.syntax;

import gpsl.parser.GPSLBaseListener;
import gpsl.parser.GPSLParser;
import gpsl.syntax.model.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Maps ANTLR4 parse tree to GPSL syntax model with external position tracking.
 * This class walks the ANTLR4 parse tree and constructs corresponding GPSL syntax tree elements.
 */
public class Antlr4ToGPSLMapper extends GPSLBaseListener {
    
    private final Map<ParserRuleContext, Object> valueMap = new HashMap<>();
    private final ParseContext context;
    private Declarations tree = null;

    public Antlr4ToGPSLMapper(ParseContext context) {
        this.context = context;
    }

    /**
     * Gets the constructed syntax tree.
     */
    public Declarations getTree() {
        return tree;
    }

    /**
     * Associates a value with a parse tree context.
     */
    private void setValue(ParserRuleContext ctx, Object value) {
        valueMap.put(ctx, value);
        
        // Track position for AST nodes
        if (value instanceof SyntaxTreeElement node) {
            context.positionMap().put(node, PositionMap.rangeOf(ctx));
            context.positionMap().putParseTree(node, ctx);
        }
    }

    /**
     * Retrieves the value associated with a parse tree context.
     * This method is package-private to allow Reader to access parsed values.
     */
    @SuppressWarnings("unchecked")
    <T> T getValue(ParserRuleContext ctx) {
        return (T) valueMap.get(ctx);
    }

    @Override
    public void exitLiteral(GPSLParser.LiteralContext ctx) {
        Expression literal = ctx.TRUE() != null ? new True() : new False();
        setValue(ctx, literal);
    }

    @Override
    public void exitAtom(GPSLParser.AtomContext ctx) {
        String value = ctx.ATOMINLINE().getText();
        String delimiter = value.substring(0, 1);
        String atom = value.substring(1, value.length() - 1);
        
        // Unescape based on delimiter
        switch (delimiter) {
            case "|":
                atom = atom.replace("\\|", "|");
                break;
            case "\"":
                atom = atom.replace("\\\"", "\"");
                break;
        }
        
        setValue(ctx, new Atom(atom, delimiter));
    }

    @Override
    public void exitLiteralExp(GPSLParser.LiteralExpContext ctx) {
        setValue(ctx, getValue(ctx.literal()));
    }

    @Override
    public void exitUnaryExp(GPSLParser.UnaryExpContext ctx) {
        String operator = ctx.operator.getText();
        Expression expression = getValue(ctx.formula());
        
        UnaryExpression unaryExpression = switch (ctx.operator.getType()) {
            case GPSLParser.NEGATION -> new Negation(operator, expression);
            case GPSLParser.NEXT -> new Next(operator, expression);
            case GPSLParser.EVENTUALLY -> new Eventually(operator, expression);
            case GPSLParser.GLOBALLY -> new Globally(operator, expression);
            default -> throw new IllegalStateException("Unknown unary operator: " + operator);
        };
        
        setValue(ctx, unaryExpression);
    }

    @Override
    public void exitBinaryExp(GPSLParser.BinaryExpContext ctx) {
        String operator = ctx.operator.getText();
        Expression left = getValue(ctx.formula(0));
        Expression right = getValue(ctx.formula(1));
        
        BinaryExpression binaryExpression = switch (ctx.operator.getType()) {
            case GPSLParser.CONJUNCTION -> new Conjunction(operator, left, right);
            case GPSLParser.DISJUNCTION -> new Disjunction(operator, left, right);
            case GPSLParser.XOR -> new ExclusiveDisjunction(operator, left, right);
            case GPSLParser.IMPLICATION -> new Implication(operator, left, right);
            case GPSLParser.EQUIVALENCE -> new Equivalence(operator, left, right);
            case GPSLParser.SUNTIL -> new StrongUntil(operator, left, right);
            case GPSLParser.WUNTIL -> new WeakUntil(operator, left, right);
            case GPSLParser.SRELEASE -> new StrongRelease(operator, left, right);
            case GPSLParser.WRELEASE -> new WeakRelease(operator, left, right);
            default -> throw new IllegalStateException("Unknown binary operator: " + operator);
        };
        
        setValue(ctx, binaryExpression);
    }

    @Override
    public void exitAtomExp(GPSLParser.AtomExpContext ctx) {
        setValue(ctx, getValue(ctx.atom()));
    }

    @Override
    public void exitParenExp(GPSLParser.ParenExpContext ctx) {
        setValue(ctx, getValue(ctx.formula()));
    }

    @Override
    public void exitReferenceExp(GPSLParser.ReferenceExpContext ctx) {
        Reference reference = new Reference(ctx.IDENTIFIER().getText());
        setValue(ctx, reference);
    }

    @Override
    public void exitFormulaDeclaration(GPSLParser.FormulaDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Expression expression = null;

        if (ctx.formula() != null) {
            expression = getValue(ctx.formula());
        } else if (ctx.automaton() != null) {
            expression = getValue(ctx.automaton());
        }

        boolean isInternal = ctx.SEQ() == null;
        ExpressionDeclaration declaration = new ExpressionDeclaration(name, expression, isInternal);
        setValue(ctx, declaration);
    }

    @Override
    public void exitFormulaDeclarationList(GPSLParser.FormulaDeclarationListContext ctx) {
        List<ExpressionDeclaration> declarations = ctx.formulaDeclaration().stream()
                .map(this::<ExpressionDeclaration>getValue)
                .collect(Collectors.toList());
        
        Declarations declarationNode = new Declarations(declarations);
        setValue(ctx, declarationNode);
    }

    @Override
    public void exitLetDecl(GPSLParser.LetDeclContext ctx) {
        Declarations declarations = getValue(ctx.formulaDeclarationList());
        setValue(ctx, declarations);
    }

    @Override
    public void exitLetExp(GPSLParser.LetExpContext ctx) {
        Declarations declarations = getValue(ctx.letDecl());
        Expression expression = getValue(ctx.formula());
        LetExpression letExpression = new LetExpression(declarations, expression);
        setValue(ctx, letExpression);
    }

    @Override
    public void exitBlock(GPSLParser.BlockContext ctx) {
        List<ExpressionDeclaration> declarations = ctx.formulaDeclaration().stream()
                .map(this::<ExpressionDeclaration>getValue)
                .collect(Collectors.toList());
        
        this.tree = new Declarations(declarations);
        setValue(ctx, this.tree);
    }

    @Override
    public void exitStateDecl(GPSLParser.StateDeclContext ctx) {
        List<String> states = ctx.IDENTIFIER().stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        setValue(ctx, states);
    }

    @Override
    public void exitInitialDecl(GPSLParser.InitialDeclContext ctx) {
        List<String> initial = ctx.IDENTIFIER().stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        setValue(ctx, initial);
    }

    @Override
    public void exitAcceptDecl(GPSLParser.AcceptDeclContext ctx) {
        List<String> accept = ctx.IDENTIFIER().stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        setValue(ctx, accept);
    }

    @Override
    public void exitTransitionDecl(GPSLParser.TransitionDeclContext ctx) {
        // Source state name
        String source = ctx.IDENTIFIER(0).getText();
        
        // Priority (may be null)
        int priority = Integer.MIN_VALUE;
        if (ctx.priority != null) {
            try {
                priority = Integer.parseInt(ctx.priority.getText());
            } catch (NumberFormatException e) {
                priority = Integer.MIN_VALUE;
            }
        }
        
        // Target state name
        String target = ctx.IDENTIFIER(1).getText();
        
        // Guard expression
        Expression guard = getValue(ctx.formula());
        
        // Note: source and target are strings here, will be resolved to State objects later
        Transition transition = new Transition(new State(source), priority, guard, new State(target));
        setValue(ctx, transition);
    }

    @Override
    public void exitAutomatonDecl(GPSLParser.AutomatonDeclContext ctx) {
        AutomatonSemanticsKind semantics = ctx.NFA() != null 
                ? AutomatonSemanticsKind.NFA 
                : AutomatonSemanticsKind.BUCHI;
        
        List<String> stateNames = getValue(ctx.stateDecl());
        List<String> initialNames = getValue(ctx.initialDecl());
        List<String> acceptNames = getValue(ctx.acceptDecl());
        
        List<Transition> transitions = ctx.transitionDecl().stream()
                .map(this::<Transition>getValue)
                .collect(Collectors.toList());
        
        // Sort transitions by priority (ascending: 0, 1, 2, ... where 0 > 1 > 2 in precedence)
        transitions.sort((a, b) -> Integer.compare(a.priority(), b.priority()));
        
        // Convert state names to a set for the automaton
        Set<State> states = stateNames.stream()
                .map(State::new)
                .collect(Collectors.toSet());
        
        Set<State> initialStates = initialNames.stream()
                .map(State::new)
                .collect(Collectors.toSet());
        
        Set<State> acceptStates = acceptNames.stream()
                .map(State::new)
                .collect(Collectors.toSet());
        
        Automaton automaton = new Automaton(semantics, states, initialStates, acceptStates, transitions);
        setValue(ctx, automaton);
    }

    @Override
    public void exitAutomaton(GPSLParser.AutomatonContext ctx) {
        Declarations declarations = ctx.letDecl() != null 
                ? getValue(ctx.letDecl()) 
                : new Declarations(List.of());
        Automaton automaton = getValue(ctx.automatonDecl());
        LetExpression letExpression = new LetExpression(declarations, automaton);
        setValue(ctx, letExpression);
    }
}
