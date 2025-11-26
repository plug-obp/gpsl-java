package gpsl.syntax.hashcons;

import gpsl.syntax.model.*;
import obp3.runtime.sli.IRootedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ToRootedGraph implements IRootedGraph<SyntaxTreeElement> {
    SyntaxTreeElement root;
    public ToRootedGraph(SyntaxTreeElement root) {
        this.root = root;
    }
    @Override
    public Iterator<SyntaxTreeElement> roots() {
        return Collections.singleton(root).iterator();
    }

    NeighboursGenerator neighboursGenerator = new NeighboursGenerator();
    @Override
    public Iterator<SyntaxTreeElement> neighbours(SyntaxTreeElement syntaxTreeElement) {
        return syntaxTreeElement.accept(neighboursGenerator, null);
    }

    public static class NeighboursGenerator implements Visitor<Void, Iterator<SyntaxTreeElement>> {
        @Override
        public Iterator<SyntaxTreeElement> visit(SyntaxTreeElement element, Void input) {
            return Collections.emptyIterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(UnaryExpression element, Void input) {
            List<SyntaxTreeElement> list = List.of(element.expression());
            return list.iterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(BinaryExpression element, Void input) {
            List<SyntaxTreeElement> list = List.of(element.left(), element.right());
            return list.iterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(Conditional element, Void input) {
            List<SyntaxTreeElement> list = List.of(element.condition(), element.trueBranch(), element.falseBranch());
            return list.iterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(LetExpression element, Void input) {
            List<SyntaxTreeElement> list = List.of(element.declarations(), element.expression());
            return list.iterator();
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Iterator<SyntaxTreeElement> visit(Declarations element, Void input) {
            return (Iterator) element.declarations().iterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(Transition element, Void input) {
            List<SyntaxTreeElement> list = List.of(element.source(), element.target(), element.guard());
            return list.iterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(ExpressionDeclaration element, Void input) {
            List<SyntaxTreeElement> list = Collections.singletonList(element.expression());
            return list.iterator();
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Iterator<SyntaxTreeElement> visit(Reference element, Void input) {
            return element.expression() == null ? Collections.emptyIterator() : (Iterator) List.of(element.expression()).iterator();
        }

        @Override
        public Iterator<SyntaxTreeElement> visit(Automaton element, Void input) {
            List<SyntaxTreeElement> list = new ArrayList<>(element.states());
            list.addAll(element.transitions());
            return list.iterator();
        }
    }
}
