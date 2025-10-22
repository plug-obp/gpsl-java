package gpsl.syntax;

import java.util.*;

/**
 * Manages symbol scopes for GPSL expressions.
 * Supports nested contexts with lexical scoping.
 */
public class Context {
    
    private final Deque<Map<String, Object>> contextStack;

    /**
     * Creates a new context with an optional global scope.
     */
    public Context(Map<String, Object> globalScope) {
        this.contextStack = new ArrayDeque<>();
        this.contextStack.push(globalScope != null ? globalScope : new HashMap<>());
    }

    /**
     * Creates a new context with an empty global scope.
     */
    public Context() {
        this(new HashMap<>());
    }

    /**
     * Returns the current (innermost) context.
     */
    public Map<String, Object> currentContext() {
        return contextStack.peek();
    }

    /**
     * Pushes a new context onto the stack.
     */
    public void pushContext(Map<String, Object> context) {
        contextStack.push(context != null ? context : new HashMap<>());
    }

    /**
     * Pushes a new empty context onto the stack.
     */
    public void pushContext() {
        pushContext(new HashMap<>());
    }

    /**
     * Pops the current context from the stack.
     */
    public void popContext() {
        if (contextStack.size() > 1) {
            contextStack.pop();
        } else {
            throw new IllegalStateException("Cannot pop the global context");
        }
    }

    /**
     * Looks up a symbol in the context stack, starting from the innermost scope.
     * 
     * @param symbol the symbol name to look up
     * @return the value associated with the symbol
     * @throws SymbolNotFoundException if the symbol is not found in any scope
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String symbol) {
        for (Map<String, Object> scope : contextStack) {
            if (scope.containsKey(symbol)) {
                return (T) scope.get(symbol);
            }
        }
        throw new SymbolNotFoundException("Symbol " + symbol + " is not defined in the current scope");
    }

    /**
     * Defines a symbol in the current context.
     * 
     * @param symbol the symbol name to define
     * @param value the value to associate with the symbol
     * @throws SymbolAlreadyDefinedException if the symbol is already defined in the current scope
     */
    public void define(String symbol, Object value) {
        if (currentContext().containsKey(symbol)) {
            throw new SymbolAlreadyDefinedException("Symbol " + symbol + " is already defined in the current scope");
        }
        currentContext().put(symbol, value);
    }

    /**
     * Exception thrown when a symbol is not found.
     */
    public static class SymbolNotFoundException extends RuntimeException {
        public SymbolNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when attempting to redefine a symbol.
     */
    public static class SymbolAlreadyDefinedException extends RuntimeException {
        public SymbolAlreadyDefinedException(String message) {
            super(message);
        }
    }
}
