package gpsl.syntax;

import gpsl.syntax.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves symbol references in GPSL syntax trees.
 * Collects errors instead of throwing exceptions.
 * This visitor walks the syntax tree and:
 * - Resolves named references to their definitions
 * - Resolves state names in automata to State objects
 * - Maintains scoped symbol tables for nested let expressions
 * - Accumulates all errors in ParseContext
 */
public class SymbolResolver implements Visitor<Context, Void> {
    
    private final ParseContext parseContext;
    
    public SymbolResolver(ParseContext parseContext) {
        this.parseContext = parseContext;
    }

    @Override
    public Void visit(LetExpression letExpression, Context environment) {
        environment.pushContext();
        letExpression.declarations().accept(this, environment);
        letExpression.expression().accept(this, environment);
        environment.popContext();
        return null;
    }

    @Override
    public Void visit(Declarations declarations, Context environment) {
        for (ExpressionDeclaration declaration : declarations.declarations()) {
            declaration.accept(this, environment);
        }
        return null;
    }

    @Override
    public Void visit(ExpressionDeclaration expressionDeclaration, Context environment) {
        if (expressionDeclaration.expression() != null) {
            expressionDeclaration.expression().accept(this, environment);
            
            // Check for duplicate symbols
            try {
                environment.define(expressionDeclaration.name(), expressionDeclaration.expression());
            } catch (Context.SymbolAlreadyDefinedException e) {
                parseContext.addError(parseContext.errorAt(
                    expressionDeclaration,
                    "duplicate symbol '" + expressionDeclaration.name() + "'",
                    "duplicate-symbol"
                ));
            }
        }
        return null;
    }

    @Override
    public Void visit(Reference reference, Context environment) {
        if (reference.expression() == null) {
            try {
                Expression resolved = environment.lookup(reference.name());
                reference.setExpression(resolved);
            } catch (Context.SymbolNotFoundException e) {
                parseContext.addError(parseContext.errorAt(
                    reference,
                    "undefined symbol '" + reference.name() + "'",
                    "undefined-symbol"
                ));
            }
        }
        return null;
    }

    @Override
    public Void visit(Transition transition, Context environment) {
        // Resolve source and target states if they are stored as State objects
        // Note: In the Java implementation, states are already State objects from the mapper
        // but we still need to resolve them from the automaton's state context
        
        // Visit the guard expression to resolve any references within it
        transition.guard().accept(this, environment);
        return null;
    }

    @Override
    public Void visit(Automaton automaton, Context environment) {
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
    public Void visit(UnaryExpression unaryExpression, Context environment) {
        unaryExpression.expression().accept(this, environment);
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression, Context environment) {
        binaryExpression.left().accept(this, environment);
        binaryExpression.right().accept(this, environment);
        return null;
    }

    @Override
    public Void visit(Conditional conditional, Context environment) {
        conditional.condition().accept(this, environment);
        conditional.trueBranch().accept(this, environment);
        conditional.falseBranch().accept(this, environment);
        return null;
    }
}
