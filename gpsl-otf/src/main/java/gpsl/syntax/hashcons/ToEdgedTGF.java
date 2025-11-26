package gpsl.syntax.hashcons;

import gpsl.syntax.SimpleName;
import gpsl.syntax.hashcons.ToEdgedRootedGraph.Pair;
import gpsl.syntax.model.SyntaxTreeElement;
import obp3.traversal.dfs.DepthFirstTraversal;
import obp3.traversal.dfs.model.FunctionalDFTCallbacksModel;

import java.util.*;

public class ToEdgedTGF {


    public static void main(String[] args) {
        var f = new HashConsingFactory();
        var term1 = f.conjunction("&&", f.t(), f.t());
        var term2 = f.conjunction("and", f.t(), f.t());
        var term = f.or(term1, term2);
        var rr = new ToEdgedRootedGraph(ToTGF.bigC(10, f));

        Map<SyntaxTreeElement, List<Pair<Optional<String>, SyntaxTreeElement>>> edges = new IdentityHashMap<>();
        var dfs = new DepthFirstTraversal<>(DepthFirstTraversal.Algorithm.WHILE, rr,
                () -> Collections.newSetFromMap(new IdentityHashMap<>()),
                Pair::b,
                new FunctionalDFTCallbacksModel<>(
                        (s, t, c) -> {
                            if (s != null) {
                                edges.get(s.b()).add(t);
                            }
                            edges.put(t.b(), new ArrayList<>());
                            return false; },
                        (s, t, c) -> {
                            if (s != null) {
                                edges.get(s.b()).add(t);
                            }
                            return false; },
                        null )
                );

        var r = dfs.runAlone();

        var tgfNodes = edges.keySet().stream().reduce("", (a, b) -> {
            a += System.identityHashCode(b) + " " + SimpleName.print(b) + "\n";
            return a;
        }, (a, b) -> a + b);
        System.out.println(tgfNodes.trim());
        System.out.println("#");
        var tgfEdges = edges.entrySet().stream().reduce("",
                (a, fanout) -> {
                    var source = System.identityHashCode(fanout.getKey());
                    a += fanout.getValue().stream().reduce("",
                            (b, target) ->
                                    b + source + " " + System.identityHashCode(target.b()) + " " + (target.a().orElse(""))+ "\n" ,
                            String::concat);
                    return a;
                },
                String::concat);
        System.out.println(tgfEdges.trim());
    }
}
