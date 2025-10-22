package gpsl.semantics;

import gpsl.syntax.model.*;

/**
 * Evaluates boolean GPSL expressions by traversing the syntax tree.
 * This evaluator handles propositional logic operators and delegates
 * atom evaluation to a provided AtomEvaluator.
 * 
 * Temporal operators (Next, Eventually, Globally, Until, Release) and
 * Automaton expressions are not supported by this basic evaluator.
 * 
 * @param <T> the type of input context passed during evaluation
 */
public class Evaluator<T> implements Visitor<T, Boolean> {
    
    private final AtomEvaluator<T> atomEvaluator;
    
    /**
     * Creates an evaluator with the specified atom evaluator.
     * 
     * @param atomEvaluator the evaluator to use for atomic propositions
     */
    public Evaluator(AtomEvaluator<T> atomEvaluator) {
        this.atomEvaluator = atomEvaluator;
    }
    
    @Override
    public Boolean visitAtom(Atom element, T input) {
        return atomEvaluator.evaluate(element.value(), input);
    }
    
    @Override
    public Boolean visitTrue(True element, T input) {
        return true;
    }
    
    @Override
    public Boolean visitFalse(False element, T input) {
        return false;
    }
    
    @Override
    public Boolean visitReference(Reference element, T input) {
        if (element.expression() == null) {
            throw new EvaluationException("Unresolved reference: " + element.name());
        }
        return element.expression().accept(this, input);
    }
    
    @Override
    public Boolean visitNegation(Negation element, T input) {
        return !element.expression().accept(this, input);
    }
    
    @Override
    public Boolean visitConjunction(Conjunction element, T input) {
        return element.left().accept(this, input) && element.right().accept(this, input);
    }
    
    @Override
    public Boolean visitDisjunction(Disjunction element, T input) {
        return element.left().accept(this, input) || element.right().accept(this, input);
    }
    
    @Override
    public Boolean visitExclusiveDisjunction(ExclusiveDisjunction element, T input) {
        return element.left().accept(this, input) != element.right().accept(this, input);
    }
    
    @Override
    public Boolean visitImplication(Implication element, T input) {
        return !element.left().accept(this, input) || element.right().accept(this, input);
    }
    
    @Override
    public Boolean visitEquivalence(Equivalence element, T input) {
        return element.left().accept(this, input) == element.right().accept(this, input);
    }
    
    @Override
    public Boolean visitLetExpression(LetExpression element, T input) {
        // Let expressions just evaluate their body
        // The declarations should already be resolved via references
        return element.expression().accept(this, input);
    }
    
    @Override
    public Boolean visitExpressionDeclaration(ExpressionDeclaration element, T input) {
        if (element.expression() == null) {
            throw new EvaluationException("Expression declaration has no expression: " + element.name());
        }
        return element.expression().accept(this, input);
    }
    
    // Temporal operators are not supported in basic boolean evaluation
    
    @Override
    public Boolean visitNext(Next element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: Next");
    }
    
    @Override
    public Boolean visitEventually(Eventually element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: Eventually");
    }
    
    @Override
    public Boolean visitGlobally(Globally element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: Globally");
    }
    
    @Override
    public Boolean visitStrongUntil(StrongUntil element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: StrongUntil");
    }
    
    @Override
    public Boolean visitWeakUntil(WeakUntil element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: WeakUntil");
    }
    
    @Override
    public Boolean visitStrongRelease(StrongRelease element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: StrongRelease");
    }
    
    @Override
    public Boolean visitWeakRelease(WeakRelease element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support temporal operator: WeakRelease");
    }

    // Automaton expressions are not supported in basic boolean evaluation
    
    @Override
    public Boolean visitAutomaton(Automaton element, T input) {
        throw new UnsupportedOperationException(
            "The GPSL evaluator does not support Automaton expressions");
    }
    
    @Override
    public Boolean visitDeclarations(Declarations element, T input) {
        throw new UnsupportedOperationException(
            "Cannot directly evaluate Declarations - evaluate individual declarations instead");
    }
    
    @Override
    public Boolean visitState(State element, T input) {
        throw new UnsupportedOperationException(
            "Cannot evaluate automaton State");
    }
    
    @Override
    public Boolean visitTransition(Transition element, T input) {
        throw new UnsupportedOperationException(
            "Cannot evaluate automaton Transition");
    }
    
    /**
     * Exception thrown when evaluation fails.
     */
    public static class EvaluationException extends RuntimeException {
        public EvaluationException(String message) {
            super(message);
        }
        
        public EvaluationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
