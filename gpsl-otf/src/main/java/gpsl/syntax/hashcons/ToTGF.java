package gpsl.syntax.hashcons;

import gpsl.syntax.model.SyntaxTreeElement;
import obp3.sli.core.operators.product.Product;
import obp3.traversal.dfs.DepthFirstTraversal;
import obp3.traversal.dfs.domain.IDepthFirstTraversalConfiguration;
import obp3.traversal.dfs.model.FunctionalDFTCallbacksModel;
import obp3.utils.Either;

import java.util.*;

public class ToTGF {


    public static void main(String[] args) {
        var f = new HashConsingFactory();
        var term1 = f.and(f.t(), f.t());
        var term2 = f.and(f.t(), f.t());
        var term = f.or(term1, term2);
        var rr = new ToRootedGraph(term);

        Map<SyntaxTreeElement, List<SyntaxTreeElement>> edges = new IdentityHashMap<>();
        var dfs = new DepthFirstTraversal<>(DepthFirstTraversal.Algorithm.WHILE, rr,
                () -> Collections.newSetFromMap(new IdentityHashMap<>()),
                new FunctionalDFTCallbacksModel<>(
                        (s, t, c) -> {
                            if (s != null) {
                                edges.get(s).add(t);
                            }
                            edges.put(t, new ArrayList<>());
                            System.out.println(s + "->" + t);
                            return false; },
                        (s, t, c) -> {
                            if (s != null) {
                                edges.get(s).add(t);
                            }
                            System.out.println(s + "->" + t);
                            return false; },
                        null ));

        var r = dfs.run((c) -> {
            switch (c) {
                case Either.Left(IDepthFirstTraversalConfiguration<SyntaxTreeElement, Object> s)  -> {
                    System.out.println("known size: " + s.getKnown().size());
                    return false;
                }
                case Either.Right(Product<IDepthFirstTraversalConfiguration<SyntaxTreeElement, Object>, Boolean> s) -> {
                    System.out.println("known size: " + s.l().getKnown().size());
                    return false;
                }
            }
        });

        var tgfNodes = edges.keySet().stream().reduce("", (a, b) -> {
            a += System.identityHashCode(b) + " " + b + "\n";
            return a;
        }, (a, b) -> a + b);
        System.out.println(tgfNodes.trim());
        System.out.println("#");
        var tgfEdges = edges.entrySet().stream().reduce("",
                (a, fanout) -> {
                    var source = System.identityHashCode(fanout.getKey());
                    a += fanout.getValue().stream().reduce("",
                            (b, target) -> b + source + " " + System.identityHashCode(target) + "\n" ,
                            String::concat);
                    return a;
                },
                String::concat);
        System.out.println(tgfEdges.trim());
    }
}
