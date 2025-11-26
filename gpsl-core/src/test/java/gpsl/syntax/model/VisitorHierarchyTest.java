package gpsl.syntax.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Visitor interface hierarchy delegation.
 *
 * This test suite verifies that the default visitor implementation correctly
 * delegates method calls up the type hierarchy when specific methods are not overridden.
 *
 * Testing Strategy:
 * 1. Track method calls using a spy visitor that records invocations
 * 2. Verify the complete call chain for each element type
 * 3. Test that overriding a method at any level stops propagation
 * 4. Test that return values propagate correctly back down the chain
 */
@DisplayName("Visitor Hierarchy Delegation Tests")
class VisitorHierarchyTest {

    /**
     * A tracking visitor that records all method invocations.
     * This allows us to verify the exact delegation chain.
     */
    static class TrackingVisitor implements Visitor<List<String>, String> {

        @Override
        public String visit(SyntaxTreeElement element, List<String> callChain) {
            callChain.add("visit.SyntaxTreeElement");
            return "SyntaxTreeElement";
        }

        @Override
        public String visit(Expression element, List<String> callChain) {
            callChain.add("visit.Expression");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(UnaryExpression element, List<String> callChain) {
            callChain.add("visit.UnaryExpression");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Negation element, List<String> callChain) {
            callChain.add("visit.Negation");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Next element, List<String> callChain) {
            callChain.add("visit.Next");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Eventually element, List<String> callChain) {
            callChain.add("visit.Eventually");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Globally element, List<String> callChain) {
            callChain.add("visit.Globally");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(BinaryExpression element, List<String> callChain) {
            callChain.add("visit.BinaryExpression");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Conjunction element, List<String> callChain) {
            callChain.add("visit.Conjunction");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Disjunction element, List<String> callChain) {
            callChain.add("visit.Disjunction");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Implication element, List<String> callChain) {
            callChain.add("visit.Implication");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Equivalence element, List<String> callChain) {
            callChain.add("visit.Equivalence");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(ExclusiveDisjunction element, List<String> callChain) {
            callChain.add("visit.ExclusiveDisjunction");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(StrongUntil element, List<String> callChain) {
            callChain.add("visit.StrongUntil");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(WeakUntil element, List<String> callChain) {
            callChain.add("visit.WeakUntil");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(StrongRelease element, List<String> callChain) {
            callChain.add("visit.StrongRelease");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(WeakRelease element, List<String> callChain) {
            callChain.add("visit.WeakRelease");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(True element, List<String> callChain) {
            callChain.add("visit.True");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(False element, List<String> callChain) {
            callChain.add("visit.False");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Atom element, List<String> callChain) {
            callChain.add("visit.Atom");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Conditional element, List<String> callChain) {
            callChain.add("visit.Conditional");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Reference element, List<String> callChain) {
            callChain.add("visit.Reference");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(LetExpression element, List<String> callChain) {
            callChain.add("visit.LetExpression");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Declarations element, List<String> callChain) {
            callChain.add("visit.Declarations");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(ExpressionDeclaration element, List<String> callChain) {
            callChain.add("visit.ExpressionDeclaration");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(State element, List<String> callChain) {
            callChain.add("visit.State");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Transition element, List<String> callChain) {
            callChain.add("visit.Transition");
            return Visitor.super.visit(element, callChain);
        }

        @Override
        public String visit(Automaton element, List<String> callChain) {
            callChain.add("visit.Automaton");
            return Visitor.super.visit(element, callChain);
        }
    }

    @Nested
    @DisplayName("Unary Expression Hierarchy")
    class UnaryExpressionHierarchyTests {

        @Test
        @DisplayName("Negation should delegate through UnaryExpression → Expression → SyntaxTreeElement")
        void negationDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var negation = new Negation("!", new True());

            // When
            List<String> callChain = new ArrayList<>();
            String result = negation.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Negation",
                "visit.UnaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain, "Negation should delegate up the full hierarchy");

            assertEquals("SyntaxTreeElement", result, "Return value should propagate from root");
        }

        @Test
        @DisplayName("Next should delegate through UnaryExpression → Expression → SyntaxTreeElement")
        void nextDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var next = new Next("X", new True());

            // When
            List<String> callChain = new ArrayList<>();
            String result = next.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Next",
                "visit.UnaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Eventually should delegate through UnaryExpression → Expression → SyntaxTreeElement")
        void eventuallyDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var eventually = new Eventually("F", new True());

            // When
            List<String> callChain = new ArrayList<>();
            String result = eventually.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Eventually",
                "visit.UnaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Globally should delegate through UnaryExpression → Expression → SyntaxTreeElement")
        void globallyDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var globally = new Globally("G", new True());

            // When
            List<String> callChain = new ArrayList<>();
            String result = globally.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Globally",
                "visit.UnaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }
    }

    @Nested
    @DisplayName("Binary Expression Hierarchy")
    class BinaryExpressionHierarchyTests {

        @Test
        @DisplayName("Conjunction should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void conjunctionDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var conjunction = new Conjunction("&", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = conjunction.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Conjunction",
                "visit.BinaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain, "Conjunction should delegate up the full hierarchy");

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Disjunction should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void disjunctionDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var disjunction = new Disjunction("|", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = disjunction.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Disjunction",
                "visit.BinaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Implication should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void implicationDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var implication = new Implication("->", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = implication.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Implication",
                "visit.BinaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Equivalence should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void equivalenceDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var equivalence = new Equivalence("<->", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = equivalence.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.Equivalence",
                    "visit.BinaryExpression",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("ExclusiveDisjunction should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void exclusiveDisjunctionDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var exclusiveDisjunction = new ExclusiveDisjunction("xor", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = exclusiveDisjunction.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.ExclusiveDisjunction",
                    "visit.BinaryExpression",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("StrongUntil should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void strongUntilDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var strongUntil = new StrongUntil("U", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = strongUntil.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.StrongUntil",
                "visit.BinaryExpression",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("WeakUntil should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void weakUntilDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var weakUntil = new WeakUntil("WU", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = weakUntil.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.WeakUntil",
                    "visit.BinaryExpression",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("StrongRelease should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void strongReleaseDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var strongRelease = new StrongRelease("SR", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = strongRelease.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.StrongRelease",
                    "visit.BinaryExpression",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("WeakRelease should delegate through BinaryExpression → Expression → SyntaxTreeElement")
        void weakReleaseDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var weakRelease = new WeakRelease("WU", new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = weakRelease.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.WeakRelease",
                    "visit.BinaryExpression",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Conditional should delegate through Expression → SyntaxTreeElement")
        void conditionalDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var conditional = new Conditional(new True(), new True(), new False());

            // When
            List<String> callChain = new ArrayList<>();
            String result = conditional.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.Conditional",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Reference should delegate through Expression → SyntaxTreeElement")
        void referenceDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var reference = new Reference("toto");

            // When
            List<String> callChain = new ArrayList<>();
            String result = reference.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.Reference",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("LetExpression should delegate through Expression → SyntaxTreeElement")
        void letExpressionDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var letExpression = new LetExpression(
                new Declarations(List.of()),
                new True()
            );

            // When
            List<String> callChain = new ArrayList<>();
            String result = letExpression.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.LetExpression",
                    "visit.Expression",
                    "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }
    }

    @Nested
    @DisplayName("Leaf Expression Hierarchy")
    class LeafExpressionHierarchyTests {

        @Test
        @DisplayName("True should delegate through Expression → SyntaxTreeElement")
        void trueDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var trueExpr = new True();

            // When
            List<String> callChain = new ArrayList<>();
            String result = trueExpr.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.True",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain, "True should delegate directly to Expression");

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("False should delegate through Expression → SyntaxTreeElement")
        void falseDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var falseExpr = new False();

            // When
            List<String> callChain = new ArrayList<>();
            String result = falseExpr.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.False",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Atom should delegate through Expression → SyntaxTreeElement")
        void atomDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var atom = new Atom("a", "");

            // When
            List<String> callChain = new ArrayList<>();
            String result = atom.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                "visit.Atom",
                "visit.Expression",
                "visit.SyntaxTreeElement"
            ), callChain);

            assertEquals("SyntaxTreeElement", result);
        }
    }

    @Nested
    @DisplayName("Override Behavior Tests")
    class OverrideBehaviorTests {

        @Test
        @DisplayName("Overriding UnaryExpression should stop propagation for all unary types")
        void overrideUnaryExpressionStopsPropagation() {
            // Given: A visitor that overrides visitUnaryExpression and stops there
            var calls = new ArrayList<String>();
            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(UnaryExpression element, Void input) {
                    calls.add("visit.UnaryExpression");
                    return "UnaryExpression-Overridden";
                }

                @Override
                public String visit(Expression element, Void input) {
                    calls.add("visit.Expression");
                    return "Should-Not-Reach";
                }
            };

            var negation = new Negation("!", new True());

            // When
            String result = negation.accept(visitor, null);

            // Then
            assertEquals("UnaryExpression-Overridden", result,
                "Should return value from overridden method");
            assertEquals(List.of("visit.UnaryExpression"), calls,
                "Should not delegate past overridden method");
        }

        @Test
        @DisplayName("Overriding Expression should stop propagation for all expression types")
        void overrideExpressionStopsPropagation() {
            // Given: A visitor that overrides visitExpression
            var calls = new ArrayList<String>();
            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(Expression element, Void input) {
                    calls.add("visit.Expression");
                    return "Expression-Overridden";
                }

                @Override
                public String visit(SyntaxTreeElement element, Void input) {
                    calls.add("visit.SyntaxTreeElement");
                    return "Should-Not-Reach";
                }
            };

            var trueExpr = new True();

            // When
            String result = trueExpr.accept(visitor, null);

            // Then
            assertEquals("Expression-Overridden", result);
            assertEquals(List.of("visit.Expression"), calls,
                "Should not delegate past overridden method");
        }

        @Test
        @DisplayName("Overriding specific type (Negation) should stop propagation at that level")
        void overrideSpecificTypeStopsPropagation() {
            // Given: A visitor that overrides visitNegation
            var calls = new ArrayList<String>();
            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(Negation element, Void input) {
                    calls.add("visit.Negation");
                    return "Negation-Overridden";
                }

                @Override
                public String visit(UnaryExpression element, Void input) {
                    calls.add("visit.UnaryExpression");
                    return "Should-Not-Reach";
                }
            };

            var negation = new Negation("!", new True());

            // When
            String result = negation.accept(visitor, null);

            // Then
            assertEquals("Negation-Overridden", result);
            assertEquals(List.of("visit.Negation"), calls);
        }

        @Test
        @DisplayName("Overriding intermediate level affects only descendants")
        void overrideIntermediateLevelAffectsDescendants() {
            // Given: Override UnaryExpression but not BinaryExpression
            var unaryResult = new ArrayList<String>();
            var binaryResult = new ArrayList<String>();

            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(UnaryExpression element, Void input) {
                    unaryResult.add("UnaryExpression-Intercepted");
                    return "UnaryExpression-Custom";
                }

                @Override
                public String visit(BinaryExpression element, Void input) {
                    binaryResult.add("BinaryExpression-Intercepted");
                    // Continue delegation
                    return Visitor.super.visit(element, input);
                }

                @Override
                public String visit(SyntaxTreeElement element, Void input) {
                    return "Root";
                }
            };

            // When
            var negation = new Negation("!", new True());
            var conjunction = new Conjunction("&", new True(), new False());

            String unaryRes = negation.accept(visitor, null);
            String binaryRes = conjunction.accept(visitor, null);

            // Then
            assertEquals("UnaryExpression-Custom", unaryRes,
                "Unary should be intercepted");
            assertEquals("Root", binaryRes,
                "Binary should continue to root");
            assertEquals(List.of("UnaryExpression-Intercepted"), unaryResult);
            assertEquals(List.of("BinaryExpression-Intercepted"), binaryResult);
        }
    }

    @Nested
    @DisplayName("Default Implementation Tests")
    class DefaultImplementationTests {

        @Test
        @DisplayName("Empty visitor (all defaults) should return null from root")
        void emptyVisitorReturnsNull() {
            // Given: A completely empty visitor using all defaults
            var visitor = new Visitor<Void, String>() {};

            // When/Then: Various expressions should all return null
            assertNull(new True().accept(visitor, null), "True should return null");
            assertNull(new False().accept(visitor, null), "False should return null");
            assertNull(new Negation("!", new True()).accept(visitor, null),
                "Negation should return null");
            assertNull(new Conjunction("&", new True(), new False()).accept(visitor, null),
                "Conjunction should return null");
        }

        @Test
        @DisplayName("Overriding only root method affects all elements")
        void overrideRootAffectsAll() {
            // Given: Override only visitSyntaxTreeElement
            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(SyntaxTreeElement element, Void input) {
                    return "Root-" + element.getClass().getSimpleName();
                }
            };

            // When/Then: All elements should use this root implementation
            assertEquals("Root-True", new True().accept(visitor, null));
            assertEquals("Root-False", new False().accept(visitor, null));
            assertEquals("Root-Negation", new Negation("!", new True()).accept(visitor, null));
            assertEquals("Root-Conjunction",
                new Conjunction("&", new True(), new False()).accept(visitor, null));
            assertEquals("Root-Eventually",
                new Eventually("F", new True()).accept(visitor, null));
        }
    }

    @Nested
    @DisplayName("Return Value Propagation Tests")
    class ReturnValuePropagationTests {

        @Test
        @DisplayName("Return value should propagate through entire chain")
        void returnValuePropagation() {
            // Given: A visitor where each level modifies the return value
            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(SyntaxTreeElement element, Void input) {
                    return "Root";
                }

                @Override
                public String visit(Expression element, Void input) {
                    return Visitor.super.visit(element, input) + "->Expression";
                }

                @Override
                public String visit(UnaryExpression element, Void input) {
                    return Visitor.super.visit(element, input) + "->Unary";
                }

                @Override
                public String visit(Negation element, Void input) {
                    return Visitor.super.visit(element, input) + "->Negation";
                }
            };

            var negation = new Negation("!", new True());

            // When
            String result = negation.accept(visitor, null);

            // Then
            assertEquals("Root->Expression->Unary->Negation", result,
                "Return value should accumulate through the call chain");
        }

        @Test
        @DisplayName("Null return value should propagate correctly")
        void nullReturnPropagation() {
            // Given: A visitor where one level returns null explicitly
            var visitor = new Visitor<Void, String>() {
                @Override
                public String visit(Expression element, Void input) {
                    return null; // Explicit null
                }

                @Override
                public String visit(UnaryExpression element, Void input) {
                    String parent = Visitor.super.visit(element, input);
                    return parent == null ? "WasNull" : "WasNotNull";
                }
            };

            var negation = new Negation("!", new True());

            // When
            String result = negation.accept(visitor, null);

            // Then
            assertEquals("WasNull", result, "Null should propagate correctly");
        }
    }

    @Nested
    @DisplayName("Input Parameter Threading Tests")
    class InputParameterTests {

        @Test
        @DisplayName("Input parameter should be threaded through delegation chain")
        void inputParameterThreading() {
            // Given: A visitor that accumulates depth through the chain
            var visitor = new Visitor<Integer, Integer>() {
                @Override
                public Integer visit(SyntaxTreeElement element, Integer depth) {
                    return depth;
                }

                @Override
                public Integer visit(Expression element, Integer depth) {
                    return Visitor.super.visit(element, depth + 1);
                }

                @Override
                public Integer visit(UnaryExpression element, Integer depth) {
                    return Visitor.super.visit(element, depth + 1);
                }

                @Override
                public Integer visit(Negation element, Integer depth) {
                    return Visitor.super.visit(element, depth + 1);
                }
            };

            var negation = new Negation("!", new True());

            // When
            Integer result = negation.accept(visitor, 0);

            // Then
            assertEquals(3, result,
                "Depth should be incremented at each level: negation(0+1) -> unary(1+1) -> expression(2+1) = 3");
        }

        @Test
        @DisplayName("Complex input object should be accessible at all levels")
        void complexInputAccess() {
            // Given: A context object that tracks visited types
            record Context(List<String> visitedTypes) {}

            var context = new Context(new ArrayList<>());

            var visitor = new Visitor<Context, Void>() {
                @Override
                public Void visit(SyntaxTreeElement element, Context ctx) {
                    ctx.visitedTypes.add("SyntaxTreeElement");
                    return null;
                }

                @Override
                public Void visit(Expression element, Context ctx) {
                    ctx.visitedTypes.add("Expression");
                    return Visitor.super.visit(element, ctx);
                }

                @Override
                public Void visit(BinaryExpression element, Context ctx) {
                    ctx.visitedTypes.add("BinaryExpression");
                    return Visitor.super.visit(element, ctx);
                }

                @Override
                public Void visit(Conjunction element, Context ctx) {
                    ctx.visitedTypes.add("Conjunction");
                    return Visitor.super.visit(element, ctx);
                }
            };

            var conjunction = new Conjunction("&", new True(), new False());

            // When
            conjunction.accept(visitor, context);

            // Then
            assertEquals(List.of(
                "Conjunction",
                "BinaryExpression",
                "Expression",
                "SyntaxTreeElement"
            ), context.visitedTypes, "Context should track all visited types");
        }
    }

    @Nested
    @DisplayName("Non-Expression SyntaxTreeElement Tests")
    class NonExpressionSyntaxTreeElementTests {

        @Test
        @DisplayName("Declarations should delegate directly to SyntaxTreeElement")
        void declarationsDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var declarations = new Declarations(List.of());

            // When
            List<String> callChain = new ArrayList<>();
            String result = declarations.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.Declarations",
                    "visit.SyntaxTreeElement"
            ), callChain, "Declarations should delegate directly to SyntaxTreeElement");

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("ExpressionDeclaration should delegate directly to SyntaxTreeElement")
        void expressionDeclarationDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var expressionDecl = new ExpressionDeclaration("myExpr", new True());

            // When
            List<String> callChain = new ArrayList<>();
            String result = expressionDecl.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.ExpressionDeclaration",
                    "visit.SyntaxTreeElement"
            ), callChain, "ExpressionDeclaration should delegate directly to SyntaxTreeElement");

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("State should delegate directly to SyntaxTreeElement")
        void stateDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var state = new State("s0");

            // When
            List<String> callChain = new ArrayList<>();
            String result = state.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.State",
                    "visit.SyntaxTreeElement"
            ), callChain, "State should delegate directly to SyntaxTreeElement");

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Transition should delegate directly to SyntaxTreeElement")
        void transitionDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var transition = new Transition(
                    new State("s0"),
                    0,
                    new True(),
                    new State("s1")
            );

            // When
            List<String> callChain = new ArrayList<>();
            String result = transition.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.Transition",
                    "visit.SyntaxTreeElement"
            ), callChain, "Transition should delegate directly to SyntaxTreeElement");

            assertEquals("SyntaxTreeElement", result);
        }

        @Test
        @DisplayName("Automaton should delegate directly to SyntaxTreeElement")
        void automatonDelegationChain() {
            // Given
            var visitor = new TrackingVisitor();
            var automaton = new Automaton(
                    AutomatonSemanticsKind.NFA,
                    Set.of(new State("s0")),
                    Set.of(new State("s0")),
                    Set.of(new State("s0")),
                    List.of()
            );

            // When
            List<String> callChain = new ArrayList<>();
            String result = automaton.accept(visitor, callChain);

            // Then
            assertEquals(List.of(
                    "visit.Automaton",
                    "visit.SyntaxTreeElement"
            ), callChain, "Automaton should delegate directly to SyntaxTreeElement");

            assertEquals("SyntaxTreeElement", result);
        }
    }
}
