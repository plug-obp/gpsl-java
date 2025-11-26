package gpsl.syntax.model;

/**
 * Visitor interface for traversing the GPSL syntax tree.
 * 
 * @param <T> the input parameter type
 * @param <R> the return type
 */
public interface Visitor<T, R> {
    
    default R visit(SyntaxTreeElement element, T input) {
        return null;
    }
    
    default R visit(Declarations element, T input) {
        return visit((SyntaxTreeElement) element, input);
    }
    
    default R visit(Expression element, T input) {
        return visit((SyntaxTreeElement) element, input);
    }
    
    default R visit(True element, T input) {
        return visit((Expression) element, input);
    }
    
    default R visit(False element, T input) {
        return visit((Expression) element, input);
    }
    
    default R visit(Atom element, T input) {
        return visit((Expression) element, input);
    }
    
    default R visit(Reference element, T input) {
        return visit((Expression) element, input);
    }
    
    default R visit(UnaryExpression element, T input) {
        return visit((Expression) element, input);
    }
    
    default R visit(Negation element, T input) {
        return visit((UnaryExpression) element, input);
    }
    
    default R visit(Next element, T input) {
        return visit((UnaryExpression) element, input);
    }
    
    default R visit(Eventually element, T input) {
        return visit((UnaryExpression) element, input);
    }
    
    default R visit(Globally element, T input) {
        return visit((UnaryExpression)element, input);
    }
    
    default R visit(BinaryExpression element, T input) {
        return visit((Expression) element, input);
    }
    
    default R visit(Conjunction element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(Disjunction element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(ExclusiveDisjunction element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(Implication element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(Equivalence element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(StrongUntil element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(WeakUntil element, T input) {
        return visit((BinaryExpression) element, input);
    }
    
    default R visit(StrongRelease element, T input) {
        return this.visit((BinaryExpression) element, input);
    }
    
    default R visit(WeakRelease element, T input) {
        return this.visit((BinaryExpression) element, input);
    }

    default R visit(Conditional element, T input) { return visit((Expression) element, input); }

    default R visit(ExpressionDeclaration element, T input) {
        return visit((SyntaxTreeElement)element, input);
    }
    
    default R visit(LetExpression element, T input) {
        return visit((Expression)element, input);
    }
    
    default R visit(State element, T input) {
        return visit((SyntaxTreeElement)element, input);
    }
    
    default R visit(Transition element, T input) {
        return visit((SyntaxTreeElement)element, input);
    }
    
    default R visit(Automaton element, T input) {
        return visit((SyntaxTreeElement)element, input);
    }
}
