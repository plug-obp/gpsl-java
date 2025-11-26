package gpsl.syntax;

import gpsl.syntax.model.SyntaxTreeElement;
import gpsl.syntax.model.Visitor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleName implements Visitor<Void, String> {
    static SimpleName INSTANCE = new SimpleName();

    public static String print(SyntaxTreeElement element) {
        return element.accept(INSTANCE, null);
    }

    @Override
    public String visit(SyntaxTreeElement element, Void input) {
        var nameMapList = List.of(
                "Conditional", "?:",
                "Conjunction", "∧",
                "Declarations", "D",
                "Disjunction", "∨",
                "Equivalence", "⟺",
                "Eventually", "◇",
                "ExclusiveDisjunction", "⊕",
                "ExpressionDeclaration", "ExpDecl",
                "False", "⊥",
                "Globally", "□",
                "Implication", "→",
                "LetExpression", "Let",
                "Negation", "¬",
                "Next", "◯",
                "Reference", "R",
                "State", "S",
                "StrongRelease", "SR",
                "StrongUntil", "SU",
                "Transition", "Trans",
                "True", "⊤",
                "WeakRelease", "WR",
                "WeakUntil", "WU"
        );

        var map = IntStream.range(0, nameMapList.size() / 2)
                .boxed()
                .collect(Collectors.toMap(
                        i -> nameMapList.get(i * 2),
                        i -> nameMapList.get(i * 2 + 1)
                ));

        var className = element.getClass().getSimpleName();
        return map.getOrDefault(className, className);
    }
}
