package gpsl.syntax.hashcons;

import gpsl.syntax.hashcons.ToEdgedRootedGraph.Pair;
import gpsl.syntax.model.*;
import obp3.runtime.sli.IRootedGraph;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ToEdgedRootedGraph implements IRootedGraph<Pair<Optional<String>, SyntaxTreeElement>> {
    public record Pair<K, V>(K a, V b) {}

    SyntaxTreeElement root;
    public ToEdgedRootedGraph(SyntaxTreeElement root) {
        this.root = root;
    }
    @Override
    public Iterator<Pair<Optional<String>, SyntaxTreeElement>> roots() {
        return Collections.singleton(new Pair<>(Optional.<String>empty(), root)).iterator();
    }

    NeighboursGenerator neighboursGenerator = new NeighboursGenerator();
    @Override
    public Iterator<Pair<Optional<String>, SyntaxTreeElement>> neighbours(Pair<Optional<String>, SyntaxTreeElement> vertex) {
        return vertex.b.accept(neighboursGenerator, null);
    }

    public static class NeighboursGenerator implements Visitor<Void, Iterator<Pair<Optional<String>, SyntaxTreeElement>>> {
        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(SyntaxTreeElement element, Void input) {
            return Collections.emptyIterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(UnaryExpression element, Void input) {
            var pair = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("exp"), element.expression());
            return Collections.singletonList(pair).iterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(BinaryExpression element, Void input) {
            var lhs = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("left"), element.left());
            var rhs = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("right"), element.right());
            return List.of(lhs, rhs).iterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(Conditional element, Void input) {
            var cond = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("condition"), element.condition());
            var tBranch = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("trueBranch"), element.trueBranch());
            var fBranch = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("falseBranch"), element.falseBranch());
            return List.of(cond, tBranch, fBranch).iterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(Declarations element, Void input) {
            return element.declarations().stream()
                    .flatMap(ed ->
                            StreamSupport.stream(((Iterable<Pair<Optional<String>, SyntaxTreeElement>>)() -> ed.accept(this, input)).spliterator(), false)
                    ).collect(Collectors.toList()).iterator();
        }
        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(LetExpression element, Void input) {
            var dp = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("declarations"), element.declarations());
            var ep = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("expression"), element.expression());
            return List.of(dp, ep).iterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(Transition element, Void input) {
            var sp = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("source"), element.source());
            var tp = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("target"), element.target());
            var gp = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of("guard"), element.guard());
            return List.of(sp, tp, gp).iterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(ExpressionDeclaration element, Void input) {
            var pair = new Pair<Optional<String>, SyntaxTreeElement>(Optional.of(element.name()), element.expression());
            return Collections.singletonList(pair).iterator();
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(Reference element, Void input) {
            return element.expression() == null ? Collections.emptyIterator()
                    : (Collections.<Pair<Optional<String>, SyntaxTreeElement>>singletonList(
                        new Pair<>(Optional.of(element.name()), element.expression())
            ).iterator());
        }

        @Override
        public Iterator<Pair<Optional<String>, SyntaxTreeElement>> visit(Automaton element, Void input) {
            return Collections.emptyIterator();
        }
    }
}
