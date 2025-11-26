package gpsl.syntax.model;

/**
 * Represents a named expression declaration.
 */
public record ExpressionDeclaration(
    String name, 
    Expression expression, 
    boolean isInternal
) implements SyntaxTreeElement {
    
    public ExpressionDeclaration(String name, Expression expression) {
        this(name, expression, true);
    }
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
