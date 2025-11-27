package gpsl.syntax.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Factory {
    static Factory INSTANCE = new Factory();
    public static Factory instance() {
        return INSTANCE;
    }

    public <T extends SyntaxTreeElement> T wrap(T term) {
        return term;
    }

    public Expression atom(String value, String delimiter) {
        return wrap(new Atom(value, delimiter));
    }

    public Expression atom(String value) {
        return atom(value, "|");
    }

    public Expression t() {
        return wrap(new True());
    }

    public Expression f() {
        return wrap(new False());
    }

    public Expression reference(String name) {
        return wrap(new Reference(name));
    }

    public Expression reference(Expression expression) {
        return wrap(expression);
    }

    public Expression negation(String operator, Expression expression) {
        var e = new Negation(operator, wrap(expression));
        return wrap(e);
    }

    public Expression negation(Expression expression) {
        return negation("!", expression);
    }

    public Expression not(Expression expression) {
        return negation(expression);
    }

    public Expression next(String operator, Expression expression) {
        var e = new Next(operator, wrap(expression));
        return wrap(e);
    }

    public Expression next(Expression expression) {
        return next("X", expression);
    }

    public Globally globally(String operator, Expression expression) {
        var e = new Globally(operator, wrap(expression));
        return wrap(e);
    }

    public Expression globally(Expression expression) {
        return globally("[]", expression);
    }

    public Expression eventually(String operator, Expression expression) {
        var e = new Eventually(operator, wrap(expression));
        return wrap(e);
    }

    public Expression eventually(Expression expression) {
        return eventually("<>", expression);
    }

    public Expression conjunction(String operator, Expression left, Expression right) {
        var e = new Conjunction(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression conjunction(Expression left, Expression right) {
        return conjunction("&&", left, right);
    }

    public Expression and(Expression left, Expression right) {
        return conjunction(left, right);
    }

    public Expression disjunction(String operator, Expression left, Expression right) {
        var e = new Disjunction(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression disjunction(Expression left, Expression right) {
        return disjunction("||", left, right);
    }

    public Expression or(Expression left, Expression right) {
        return disjunction(left, right);
    }

    public Expression equivalence(String operator, Expression left, Expression right) {
        var e = new Equivalence(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression equivalence(Expression left, Expression right) {
        return equivalence("<->", left, right);
    }

    public Expression implication(String operator, Expression left, Expression right) {
        var e = new Implication(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression implication(Expression left, Expression right) {
        return implication("->", left, right);
    }

    public Expression implies(Expression left, Expression right) {
        return implication(left, right);
    }

    public Expression exclusiveDisjunction(String operator, Expression left, Expression right) {
        var e = new ExclusiveDisjunction(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression exclusiveDisjunction(Expression left, Expression right) {
        return exclusiveDisjunction("xor", left, right);
    }

    public Expression xor(Expression left, Expression right) {
        return exclusiveDisjunction(left, right);
    }

    public Expression strongRelease(String operator, Expression left, Expression right) {
        var e = new StrongRelease(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression strongRelease(Expression left, Expression right) {
        return strongRelease("M", left, right);
    }

    public Expression weakRelease(String operator, Expression left, Expression right) {
        var e = new WeakRelease(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression weakRelease(Expression left, Expression right) {
        return weakRelease("R", left, right);
    }

    public Expression strongUntil(String operator, Expression left, Expression right) {
        var e = new StrongUntil(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression strongUntil(Expression left, Expression right) {
        return strongUntil("U", left, right);
    }

    public Expression weakUntil(String operator, Expression left, Expression right) {
        var e = new WeakUntil(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Expression weakUntil(Expression left, Expression right) {
        return weakUntil("W", left, right);
    }

    public Expression conditional(Expression condition, Expression thenClause, Expression elseClause) {
        var e = new Conditional(wrap(condition), wrap(thenClause), wrap(elseClause));
        return wrap(e);
    }

    public ExpressionDeclaration declaration(String name, Expression expression, boolean isInternal) {
        var e = new ExpressionDeclaration(name, wrap(expression), isInternal);
        return wrap(e);
    }

    public ExpressionDeclaration internal(String name, Expression expression) {
        return declaration(name, expression, true);
    }

    public ExpressionDeclaration external(String name, Expression expression) {
        return declaration(name, expression, false);
    }

    public Declarations declarations(List<ExpressionDeclaration> declarations) {
        return wrap(new Declarations(declarations.stream().map(this::wrap).toList()));
    }

    public Declarations declarations(ExpressionDeclaration... declarations) {
        return wrap(new Declarations(Arrays.stream(declarations).map(this::wrap).toList()));
    }

    public Expression letExpression(Declarations declarations, Expression expression) {
        return wrap(new LetExpression(wrap(declarations), wrap(expression)));
    }

    public Expression letExpression(Expression expression, ExpressionDeclaration... whereClauses) {
        return wrap(new LetExpression(wrap(declarations(whereClauses)), wrap(expression)));
    }

    public State state(String name) {
        return wrap(new State(name));
    }

    public Transition transition(State source,
                                 int priority,
                                 Expression guard,
                                 State target) {
        return wrap(new Transition(wrap(source), priority, wrap(guard), wrap(target)));
    }

    public Automaton automaton(AutomatonSemanticsKind semanticsKind,
                               Set<State> states,
                               Set<State> initialStates,
                               Set<State> acceptStates,
                               List<Transition> transitions) {
        return wrap(new Automaton(
                semanticsKind,
                states.stream().map(this::wrap).collect(Collectors.toSet()),
                initialStates.stream().map(this::wrap).collect(Collectors.toSet()),
                acceptStates.stream().map(this::wrap).collect(Collectors.toSet()),
                transitions.stream().map(this::wrap).collect(Collectors.toList())));
    }
}
