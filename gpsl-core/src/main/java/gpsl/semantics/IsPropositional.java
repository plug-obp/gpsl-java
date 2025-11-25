package gpsl.semantics;

import gpsl.syntax.model.*;

public class IsPropositional implements Visitor<Void, Boolean> {

    @Override
    public Boolean visitSyntaxTreeElement(SyntaxTreeElement element, Void input) {
        return false;
    }

    @Override
    public Boolean visitAtom(Atom element, Void input) {
        return true;
    }

    @Override
    public Boolean visitTrue(True element, Void input) {
        return true;
    }

    @Override
    public Boolean visitFalse(False element, Void input) {
        return true;
    }

    @Override
    public Boolean visitReference(Reference element, Void input) {
        return element.expression() != null && element.expression().accept(this, input);
    }

    @Override
    public Boolean visitNegation(Negation element, Void input) {
        return element.expression().accept(this, input);
    }

    @Override
    public Boolean visitBinaryExpression(BinaryExpression element, Void input) {
        return element.left().accept(this, input) && element.right().accept(this, input);
    }

    @Override
    public Boolean visitConditional(Conditional element, Void input) {
        return element.condition().accept(this, input)
            && element.trueBranch().accept(this, input)
            && element.falseBranch().accept(this, input);
    }

    @Override
    public Boolean visitNext(Next element, Void input) {
        return false;
    }

    @Override
    public Boolean visitEventually(Eventually element, Void input) {
        return false;
    }

    @Override
    public Boolean visitGlobally(Globally element, Void input) {
        return false;
    }

    @Override
    public Boolean visitStrongRelease(StrongRelease element, Void input) {
        return false;
    }

    @Override
    public Boolean visitStrongUntil(StrongUntil element, Void input) {
        return false;
    }

    @Override
    public Boolean visitWeakRelease(WeakRelease element, Void input) {
        return false;
    }

    @Override
    public Boolean visitWeakUntil(WeakUntil element, Void input) {
        return false;
    }

    @Override
    public Boolean visitState(State element, Void input) {
        return false;
    }

    @Override
    public Boolean visitTransition(Transition element, Void input) {
        return false;
    }

    @Override
    public Boolean visitAutomaton(Automaton element, Void input) {
        return false;
    }

    @Override
    public Boolean visitDeclarations(Declarations element, Void input) {
        return false;
    }

    @Override
    public Boolean visitExpressionDeclaration(ExpressionDeclaration element, Void input) {
        return element.expression().accept(this, input);
    }

    @Override
    public Boolean visitLetExpression(LetExpression element, Void input) {
        return element.expression().accept(this, input);
    }
}
