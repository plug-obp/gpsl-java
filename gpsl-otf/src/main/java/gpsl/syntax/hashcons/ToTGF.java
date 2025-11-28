package gpsl.syntax.hashcons;

import gpsl.syntax.SimpleName;
import gpsl.syntax.ite.IteFactory;
import gpsl.syntax.model.Expression;
import gpsl.syntax.model.Factory;
import gpsl.syntax.model.SyntaxTreeElement;
import gpsl.syntax.model.Visitor;
import obp3.runtime.sli.IRootedGraph;
import obp3.traversal.dfs.DepthFirstTraversal;
import obp3.traversal.dfs.model.FunctionalDFTCallbacksModel;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class ToTGF implements Visitor<Boolean, String>, BiFunction<SyntaxTreeElement, Boolean, String> {
    static ToTGF INSTANCE = new ToTGF();
    public static ToTGF instance() {
        return INSTANCE;
    }

    public static void writeToFile(SyntaxTreeElement term, String filename, boolean withEdgeLabels) throws Exception {
            var writer = new java.io.FileWriter(filename);
            writer.write(transform(term, withEdgeLabels));
            writer.close();
    }

    public static String transform(SyntaxTreeElement term, boolean withEdgeLabels) {
        return withEdgeLabels ? INSTANCE.toEdgedTGF(term) : INSTANCE.toTGF(term);
    }

    @Override
    public String apply(SyntaxTreeElement syntaxTreeElement, Boolean withEdgeLabels) {
        return transform(syntaxTreeElement, withEdgeLabels);
    }

    @Override
    public String visit(SyntaxTreeElement node, Boolean withEdgeLabels){
        return transform(node, withEdgeLabels);
    }

    public String toTGF(SyntaxTreeElement term) {
        var rr = new ToRootedGraph(term);
        var edges = extractMap(rr, Function.identity());
        return tgfFromMap(edges, Function.identity(), SimpleName::print, _ -> "");
    }

    public String toEdgedTGF(SyntaxTreeElement term) {
        var rr = new ToEdgedRootedGraph(term);
        var edges = extractMap(rr, ToEdgedRootedGraph.Pair::b);
        return tgfFromMap(
                edges,
                ToEdgedRootedGraph.Pair::b,
                SimpleName::print,
                t -> t.a().orElse(""));
    }

    public <V, E> Map<V, List<E>> extractMap(IRootedGraph<E> graph, Function<E, V> vertexMapper) {
        Map<V, List<E>> map = new IdentityHashMap<>();
        var dfs = new DepthFirstTraversal<>(DepthFirstTraversal.Algorithm.WHILE, graph,
                () -> Collections.newSetFromMap(new IdentityHashMap<>()),
                vertexMapper,
                new FunctionalDFTCallbacksModel<>(
                        (s, t, c) -> {
                            if (s != null) {
                                map.get(vertexMapper.apply(s)).add(t);
                            }
                            map.put(vertexMapper.apply(t), new ArrayList<>());
                            return false; },
                        (s, t, c) -> {
                            if (s != null) {
                                map.get(vertexMapper.apply(s)).add(t);
                            }
                            return false; },
                        null )
        );

        dfs.runAlone();
        return map;
    }

    <V, E> String tgfFromMap(
            Map<V, List<E>> map,
            Function<E, V> targetGetter,
            Function<V, String> nodeLabelGetter,
            Function<E, String> edgeLabelGetter) {
        var tgfContents = nodesToString(map.keySet().stream(), nodeLabelGetter);
        tgfContents += "\n#\n";
        tgfContents += edgesToString(
                map.entrySet().stream(),
                targetGetter,
                edgeLabelGetter);
        return tgfContents;
    }

    <V> String nodesToString(Stream<V> stream, Function<V, String> nodeLabelGetter) {
        return stream.reduce(
                "",
                (acc, v) -> acc + System.identityHashCode(v) + " " + nodeLabelGetter.apply(v) + "\n",
                String::concat).trim();
    }

    <V, E> String edgesToString(
            Stream<Map.Entry<V, List<E>>> stream,
            Function<E, V> targetGetter,
            Function<E, String> edgeLabelGetter) {
        return stream.reduce("",
                (acc, fanout) -> {
                    var source = System.identityHashCode(fanout.getKey());
                    acc += fanout.getValue().stream().reduce("",
                            (b, target) ->
                                    b + source + " " + System.identityHashCode(targetGetter.apply(target))
                                            + " " + (edgeLabelGetter.apply(target))+ "\n" ,
                            String::concat);
                    return acc;
                },
                String::concat).trim();
    }


    public static void main(String[] args) throws Exception {
//        var f = new HashConsingFactory();
//        var term1 = f.conjunction("&&", f.t(), f.t());
//        var term2 = f.conjunction("and", f.t(), f.t());
//        var term = f.or(term1, term2);
//        System.out.println(ToTGF.transform(term, false));
//        System.out.println("-----");
//        System.out.println(ToTGF.transform(term, true));
//
//        System.out.println(bigC(3, f).accept(ToTGF.instance(), false));
//        System.out.println("-----");
//        System.out.println(ToTGF.instance().apply(bigC(3, new Factory()), true));
//
//        ToTGF.writeToFile(bigC(5, f), "gpsl_term.tgf", true);

        all("maj", ToTGF::majority);
        all("bigc", (f) -> bigC(5, f));


    }

    static void all(String name, Function<Factory, Expression> termGenerator) throws Exception {
        var f = new Factory();
        var term = termGenerator.apply(f);
        ToTGF.writeToFile(term, "gpsl_"+name+".tgf", true);
        var hcF = new HashConsingFactory();
        var hcTerm = termGenerator.apply(hcF);
        ToTGF.writeToFile(hcTerm, "gpsl_"+name+"_hc.tgf", true);
        var iF = new IteFactory();
        var iTerm = termGenerator.apply(iF);
        ToTGF.writeToFile(iTerm, "gpsl_"+name+"_ite.tgf", true);

    }

    static Expression bigC(int n, Factory f) {
        if (n == 0) {
            return f.t();
        } else {
            return f.conjunction("and", bigC(n - 1, f), bigC(n-1, f));
        }
    }

    //(a ∧ b) ∨ (a ∧ c) ∨ (b ∧ c)
    static Expression majority(Factory f) {
        var ab = f.and(f.atom("a"), f.atom("b"));
        var ac = f.and(f.atom("a"), f.atom("c"));
        var bc = f.and(f.atom("b"), f.atom("c"));
        return f.or(f.or(ab, ac), bc);
    }
}
