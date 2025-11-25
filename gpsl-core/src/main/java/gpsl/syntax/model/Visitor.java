package gpsl.syntax.model;

/**
 * Visitor interface for traversing the GPSL syntax tree.
 * 
 * @param <T> the input parameter type
 * @param <R> the return type
 */
public interface Visitor<T, R> {
    
    default R visitSyntaxTreeElement(SyntaxTreeElement element, T input) {
        return null;
    }
    
    default R visitDeclarations(Declarations element, T input) {
        return visitSyntaxTreeElement(element, input);
    }
    
    default R visitExpression(Expression element, T input) {
        return visitSyntaxTreeElement(element, input);
    }
    
    default R visitTrue(True element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitFalse(False element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitAtom(Atom element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitReference(Reference element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitUnaryExpression(UnaryExpression element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitNegation(Negation element, T input) {
        return visitUnaryExpression(element, input);
    }
    
    default R visitNext(Next element, T input) {
        return visitUnaryExpression(element, input);
    }
    
    default R visitEventually(Eventually element, T input) {
        return visitUnaryExpression(element, input);
    }
    
    default R visitGlobally(Globally element, T input) {
        return visitUnaryExpression(element, input);
    }
    
    default R visitBinaryExpression(BinaryExpression element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitConjunction(Conjunction element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitDisjunction(Disjunction element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitExclusiveDisjunction(ExclusiveDisjunction element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitImplication(Implication element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitEquivalence(Equivalence element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitStrongUntil(StrongUntil element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitWeakUntil(WeakUntil element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitStrongRelease(StrongRelease element, T input) {
        return visitBinaryExpression(element, input);
    }
    
    default R visitWeakRelease(WeakRelease element, T input) {
        return visitBinaryExpression(element, input);
    }

    default R visitConditional(Conditional element, T input) { return visitExpression(element, input); }

    default R visitExpressionDeclaration(ExpressionDeclaration element, T input) {
        return visitSyntaxTreeElement(element, input);
    }
    
    default R visitLetExpression(LetExpression element, T input) {
        return visitExpression(element, input);
    }
    
    default R visitState(State element, T input) {
        return visitSyntaxTreeElement(element, input);
    }
    
    default R visitTransition(Transition element, T input) {
        return visitSyntaxTreeElement(element, input);
    }
    
    default R visitAutomaton(Automaton element, T input) {
        return visitSyntaxTreeElement(element, input);
    }
}
