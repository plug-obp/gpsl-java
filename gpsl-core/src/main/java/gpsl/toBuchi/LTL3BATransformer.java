package gpsl.toBuchi;

import gpsl.semantics.Evaluator.EvaluationException;
import gpsl.syntax.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformer that converts GPSL expressions to LTL3BA abstract text format.
 * This transformer handles atom name mapping and converts GPSL operators to
 * LTL3BA-compatible syntax, including encoding operators not directly supported
 * by LTL3BA (such as XOR, W, and M) into equivalent expressions.
 */
public class LTL3BATransformer implements Visitor<Void, String> {
    
    private final Map<String, Atom> nameToAtom;
    private final Map<Atom, String> atomToName;
    
    public LTL3BATransformer() {
        this.nameToAtom = new HashMap<>();
        this.atomToName = new HashMap<>();
    }
    
    /**
     * Gets the map of atom names to atom objects.
     * This can be used to retrieve the original atoms after transformation.
     * 
     * @return unmodifiable view of the name-to-atom mapping
     */
    public Map<String, Atom> getNameToAtomMap() {
        return Map.copyOf(nameToAtom);
    }
    
    /**
     * Gets the map of atom objects to their assigned names.
     * 
     * @return unmodifiable view of the atom-to-name mapping
     */
    public Map<Atom, String> getAtomToNameMap() {
        return Map.copyOf(atomToName);
    }
    
    @Override
    public String visitSyntaxTreeElement(SyntaxTreeElement element, Void input) {
        throw new UnsupportedOperationException(
            "The GPSL to abstract text converter does not support " 
            + element.getClass().getSimpleName() + " elements."
        );
    }
    
    @Override
    public String visitAtom(Atom element, Void input) {
        String name = atomToName.get(element);
        if (name == null) {
            name = "atom" + nameToAtom.size();
            nameToAtom.put(name, element);
            atomToName.put(element, name);
        }
        return name;
    }
    
    @Override
    public String visitTrue(True element, Void input) {
        return "true";
    }
    
    @Override
    public String visitFalse(False element, Void input) {
        return "false";
    }
    
    @Override
    public String visitReference(Reference element, Void input) {
        if (element.expression() == null) {
            throw new EvaluationException("Unresolved reference: " + element.name());
        }
        return element.expression().accept(this, input);
    }
    
    @Override
    public String visitNegation(Negation element, Void input) {
        return "(!" + element.expression().accept(this, input) + ")";
    }
    
    @Override
    public String visitNext(Next element, Void input) {
        return "(X " + element.expression().accept(this, input) + ")";
    }
    
    @Override
    public String visitEventually(Eventually element, Void input) {
        return "(<> " + element.expression().accept(this, input) + ")";
    }
    
    @Override
    public String visitGlobally(Globally element, Void input) {
        return "([] " + element.expression().accept(this, input) + ")";
    }
    
    @Override
    public String visitConjunction(Conjunction element, Void input) {
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(" + left + " && " + right + ")";
    }
    
    @Override
    public String visitDisjunction(Disjunction element, Void input) {
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(" + left + " || " + right + ")";
    }
    
    @Override
    public String visitExclusiveDisjunction(ExclusiveDisjunction element, Void input) {
        // XOR is not supported by LTL3BA, use the XOR encoding instead: (!a && b) || (a && !b)
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "((!"+left+" && "+right+") || ("+left+" && !"+right+"))";
    }
    
    @Override
    public String visitImplication(Implication element, Void input) {
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(" + left + " -> " + right + ")";
    }
    
    @Override
    public String visitEquivalence(Equivalence element, Void input) {
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(" + left + " <-> " + right + ")";
    }

    @Override
    public String visitConditional(Conditional element, Void input) {
        // Is ' c ? tB :fB / ITE(c, tB, fB)' by LTL3BA?, use the encoding instead: (c && tB) || (!c && fB)
        var tB = "(" + element.condition().accept(this, input) + " && " + element.trueBranch().accept(this, input) + ")";
        var fB = "(!" + element.condition().accept(this, input) + " && " + element.falseBranch().accept(this, input) + ")";
        return "(" + tB + " || " + fB + ")";
    }

    @Override
    public String visitStrongUntil(StrongUntil element, Void input) {
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(" + left + " U " + right + ")";
    }
    
    @Override
    public String visitWeakUntil(WeakUntil element, Void input) {
        // W is not supported by LTL3BA, encode as: ([] a) || (a U b)
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(([] " + left + ") || (" + left + " U " + right + "))";
    }
    
    @Override
    public String visitStrongRelease(StrongRelease element, Void input) {
        // M is not supported by LTL3BA, encode as: b U (a && b)
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "((" + right + ") U (" + left + " && " + right + "))";
    }
    
    @Override
    public String visitWeakRelease(WeakRelease element, Void input) {
        String left = element.left().accept(this, input);
        String right = element.right().accept(this, input);
        return "(" + left + " R " + right + ")";
    }
    
    @Override
    public String visitLetExpression(LetExpression element, Void input) {
        return element.expression().accept(this, input);
    }
    
    @Override
    public String visitExpressionDeclaration(ExpressionDeclaration element, Void input) {
        return element.expression().accept(this, input);
    }
}
