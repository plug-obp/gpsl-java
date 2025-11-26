package gpsl.syntax.hashcons;

import gpsl.syntax.SimpleName;
import gpsl.syntax.model.Expression;
import gpsl.syntax.model.Factory;
import gpsl.syntax.model.SyntaxTreeElement;
import obp3.traversal.dfs.DepthFirstTraversal;
import obp3.traversal.dfs.model.FunctionalDFTCallbacksModel;

import java.util.*;

public class ToTGF {


    public static void main(String[] args) {
        var f = new HashConsingFactory();
//        var term1 = f.conjunction("&&", f.t(), f.t());
//        var term2 = f.conjunction("and", f.t(), f.t());
//        var term = f.or(term1, term2);
        var rr = new ToRootedGraph(bigC(4, f));

        Map<SyntaxTreeElement, List<SyntaxTreeElement>> edges = new IdentityHashMap<>();
        var dfs = new DepthFirstTraversal<>(DepthFirstTraversal.Algorithm.WHILE, rr,
                () -> Collections.newSetFromMap(new IdentityHashMap<>()),
                new FunctionalDFTCallbacksModel<>(
                        (s, t, c) -> {
                            if (s != null) {
                                edges.get(s).add(t);
                            }
                            edges.put(t, new ArrayList<>());
                            return false; },
                        (s, t, c) -> {
                            if (s != null) {
                                edges.get(s).add(t);
                            }
                            return false; },
                        null ));

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
                            (b, target) -> b + source + " " + System.identityHashCode(target) + "\n" ,
                            String::concat);
                    return a;
                },
                String::concat);
        System.out.println(tgfEdges.trim());
    }

    static Expression bigC(int n, Factory f) {
        if (n == 0) {
            return f.t();
        } else {
            return f.conjunction("and", bigC(n - 1, f), bigC(n-1, f));
        }
    }

}
