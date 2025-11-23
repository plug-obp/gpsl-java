package gpsl.syntax.model;

public class Builder {
    final Factory f;
    SyntaxTreeElement term;

    public Builder(Factory factory) {
        this(null, factory);
    }

    public Builder(SyntaxTreeElement initialTerm, Factory factory) {
        this.term = initialTerm;
        this.f = factory;
    }
    public SyntaxTreeElement term() {
        return term;
    }
    public Builder t() {
        term = f.t();
        return this;
    }
    public Builder f() {
        term = f.t();
        return this;
    }
    public Builder not() {
        if (term instanceof Expression e) {
            term = f.negation(e);
            return this;
        }
        throw new IllegalArgumentException("Expected Expression, got " + term.getClass());
    }
}
