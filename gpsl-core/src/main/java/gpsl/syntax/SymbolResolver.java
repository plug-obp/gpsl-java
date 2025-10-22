package gpsl.syntax;

import gpsl.syntax.model.*;

import java.util.*;

/**
 * Resolves symbol references in GPSL syntax trees.
 * This visitor walks the syntax tree and:
 * - Resolves named references to their definitions
 * - Resolves state names in automata to State objects
 * - Maintains scoped symbol tables for nested let expressions
 */
public class SymbolResolver implements Visitor<Context, Void> {

    @Override
    public Void visitLetExpression(LetExpression letExpression, Context environment) {
        environment.pushContext();
        letExpression.declarations().accept(this, environment);
        letExpression.expression().accept(this, environment);
        environment.popContext();
        return null;
    }

    @Override
    public Void visitDeclarations(Declarations declarations, Context environment) {
        for (ExpressionDeclaration declaration : declarations.declarations()) {
            declaration.accept(this, environment);
        }
        return null;
    }

    @Override
    public Void visitExpressionDeclaration(ExpressionDeclaration expressionDeclaration, Context environment) {
        if (expressionDeclaration.expression() != null) {
            expressionDeclaration.expression().accept(this, environment);
            environment.define(expressionDeclaration.name(), expressionDeclaration.expression());
        }
        return null;
    }

    @Override
    public Void visitReference(Reference reference, Context environment) {
        if (reference.expression() == null) {
            try {
                Expression resolved = environment.lookup(reference.name());
                reference.setExpression(resolved);
            } catch (Context.SymbolNotFoundException e) {
                throw new Context.SymbolNotFoundException("Symbol " + reference.name() + " is not defined in the current scope");
            }
        }
        return null;
    }

    @Override
    public Void visitTransition(Transition transition, Context environment) {
        // Resolve source and target states if they are stored as State objects
        // Note: In the Java implementation, states are already State objects from the mapper
        // but we still need to resolve them from the automaton's state context
        
        // Visit the guard expression to resolve any references within it
        transition.guard().accept(this, environment);
        return null;
    }

    @Override
    public Void visitAutomaton(Automaton automaton, Context environment) {
        // Create a new context for the automaton's states
        Map<String, Object> stateContext = new HashMap<>();
        
        // Define all states in the context
        for (State state : automaton.states()) {
            stateContext.put(state.name(), state);
        }
        
        environment.pushContext(stateContext);

        // Resolve transitions - source and target states should be looked up
        // and guard expressions should be resolved
        for (Transition transition : automaton.transitions()) {
            transition.accept(this, environment);
        }
        
        environment.popContext();
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression unaryExpression, Context environment) {
        unaryExpression.expression().accept(this, environment);
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression binaryExpression, Context environment) {
        binaryExpression.left().accept(this, environment);
        binaryExpression.right().accept(this, environment);
        return null;
    }

    // Default implementations for other visit methods that don't need symbol resolution
    
    @Override
    public Void visitTrue(True element, Context input) {
        return null;
    }

    @Override
    public Void visitFalse(False element, Context input) {
        return null;
    }

    @Override
    public Void visitAtom(Atom element, Context input) {
        return null;
    }

    @Override
    public Void visitNegation(Negation element, Context input) {
        return visitUnaryExpression(element, input);
    }

    @Override
    public Void visitNext(Next element, Context input) {
        return visitUnaryExpression(element, input);
    }

    @Override
    public Void visitEventually(Eventually element, Context input) {
        return visitUnaryExpression(element, input);
    }

    @Override
    public Void visitGlobally(Globally element, Context input) {
        return visitUnaryExpression(element, input);
    }

    @Override
    public Void visitConjunction(Conjunction element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitDisjunction(Disjunction element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitExclusiveDisjunction(ExclusiveDisjunction element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitImplication(Implication element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitEquivalence(Equivalence element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitStrongUntil(StrongUntil element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitWeakUntil(WeakUntil element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitStrongRelease(StrongRelease element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitWeakRelease(WeakRelease element, Context input) {
        return visitBinaryExpression(element, input);
    }

    @Override
    public Void visitState(State element, Context input) {
        return null;
    }
}
