package gpsl.syntax.model;

import java.util.List;

/**
 * Represents a collection of GPSL declarations.
 */
public record Declarations(List<ExpressionDeclaration> declarations) 
    implements SyntaxTreeElement {
    
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T input) {
        return visitor.visit(this, input);
    }
}
