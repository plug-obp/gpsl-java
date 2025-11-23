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

    public Atom atom(String value, String delimiter) {
        return wrap(new Atom(value, delimiter));
    }

    public True t() {
        return wrap(new True());
    }

    public False f() {
        return wrap(new False());
    }

    public Reference reference(String name) {
        return wrap(new Reference(name));
    }

    public Expression reference(Expression expression) {
        return wrap(expression);
    }

    public Negation negation(String operator, Expression expression) {
        var e = new Negation(operator, wrap(expression));
        return wrap(e);
    }

    public Negation negation(Expression expression) {
        return negation("!", expression);
    }

    public Negation not(Expression expression) {
        return negation(expression);
    }

    public Next next(String operator, Expression expression) {
        var e = new Next(operator, wrap(expression));
        return wrap(e);
    }

    public Next next(Expression expression) {
        return next("X", expression);
    }

    public Globally globally(String operator, Expression expression) {
        var e = new Globally(operator, wrap(expression));
        return wrap(e);
    }

    public Globally globally(Expression expression) {
        return globally("[]", expression);
    }

    public Eventually eventually(String operator, Expression expression) {
        var e = new Eventually(operator, wrap(expression));
        return wrap(e);
    }

    public Eventually eventually(Expression expression) {
        return eventually("<>", expression);
    }

    public Conjunction conjunction(String operator, Expression left, Expression right) {
        var e = new Conjunction(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Conjunction conjunction(Expression left, Expression right) {
        return conjunction("&&", left, right);
    }

    public Conjunction and(Expression left, Expression right) {
        return conjunction(left, right);
    }

    public Disjunction disjunction(String operator, Expression left, Expression right) {
        var e = new Disjunction(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Disjunction disjunction(Expression left, Expression right) {
        return disjunction("||", left, right);
    }

    public Disjunction or(Expression left, Expression right) {
        return disjunction(left, right);
    }

    public Equivalence equivalence(String operator, Expression left, Expression right) {
        var e = new Equivalence(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Equivalence equivalence(Expression left, Expression right) {
        return equivalence("<->", left, right);
    }

    public Implication implication(String operator, Expression left, Expression right) {
        var e = new Implication(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public Implication implication(Expression left, Expression right) {
        return implication("->", left, right);
    }

    public Implication implies(Expression left, Expression right) {
        return implication(left, right);
    }

    public ExclusiveDisjunction exclusiveDisjunction(String operator, Expression left, Expression right) {
        var e = new ExclusiveDisjunction(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public ExclusiveDisjunction exclusiveDisjunction(Expression left, Expression right) {
        return exclusiveDisjunction("xor", left, right);
    }

    public ExclusiveDisjunction xor(Expression left, Expression right) {
        return exclusiveDisjunction(left, right);
    }

    public StrongRelease strongRelease(String operator, Expression left, Expression right) {
        var e = new StrongRelease(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public StrongRelease strongRelease(Expression left, Expression right) {
        return strongRelease("M", left, right);
    }

    public WeakRelease weakRelease(String operator, Expression left, Expression right) {
        var e = new WeakRelease(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public WeakRelease weakRelease(Expression left, Expression right) {
        return weakRelease("R", left, right);
    }

    public StrongUntil strongUntil(String operator, Expression left, Expression right) {
        var e = new StrongUntil(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public StrongUntil strongUntil(Expression left, Expression right) {
        return strongUntil("U", left, right);
    }

    public WeakUntil weakUntil(String operator, Expression left, Expression right) {
        var e = new WeakUntil(operator, wrap(left), wrap(right));
        return wrap(e);
    }

    public WeakUntil weakUntil(Expression left, Expression right) {
        return weakUntil("W", left, right);
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

    public LetExpression letExpression(Declarations declarations, Expression expression) {
        return wrap(new LetExpression(wrap(declarations), wrap(expression)));
    }

    public LetExpression letExpression(Expression expression, ExpressionDeclaration... whereClauses) {
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
