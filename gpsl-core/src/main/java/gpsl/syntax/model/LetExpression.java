package gpsl.syntax.model;

import java.util.List;

/**
 * Represents a let expression with local declarations.
 * The body can be either an expression or an automaton.
 */
public record LetExpression(Declarations declarations, SyntaxTreeElement expression) 
    implements Expression {
    
    public LetExpression(Declarations declarations, SyntaxTreeElement expression) {
        this.declarations = declarations == null 
            ? new Declarations(List.of()) 
            : declarations;
        this.expression = expression;
    }
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
