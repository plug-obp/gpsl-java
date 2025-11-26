package gpsl.semantics;

import gpsl.syntax.model.*;

public class IsPropositional implements Visitor<Void, Boolean> {

    @Override
    public Boolean visit(SyntaxTreeElement element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(Atom element, Void input) {
        return true;
    }

    @Override
    public Boolean visit(True element, Void input) {
        return true;
    }

    @Override
    public Boolean visit(False element, Void input) {
        return true;
    }

    @Override
    public Boolean visit(Reference element, Void input) {
        return element.expression() != null && element.expression().accept(this, input);
    }

    @Override
    public Boolean visit(Negation element, Void input) {
        return element.expression().accept(this, input);
    }

    @Override
    public Boolean visit(BinaryExpression element, Void input) {
        return element.left().accept(this, input) && element.right().accept(this, input);
    }

    @Override
    public Boolean visit(Conditional element, Void input) {
        return element.condition().accept(this, input)
            && element.trueBranch().accept(this, input)
            && element.falseBranch().accept(this, input);
    }

    @Override
    public Boolean visit(Next element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(Eventually element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(Globally element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(StrongRelease element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(StrongUntil element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(WeakRelease element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(WeakUntil element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(State element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(Transition element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(Automaton element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(Declarations element, Void input) {
        return false;
    }

    @Override
    public Boolean visit(ExpressionDeclaration element, Void input) {
        return element.expression().accept(this, input);
    }

    @Override
    public Boolean visit(LetExpression element, Void input) {
        return element.expression().accept(this, input);
    }
}
