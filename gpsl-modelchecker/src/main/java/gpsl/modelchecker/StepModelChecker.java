package gpsl.modelchecker;

import gpsl.semantics.AtomEvaluator;
import gpsl.semantics.AutomatonSemantics;
import gpsl.semantics.Semantics;
import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.modelchecking.EmptinessCheckerStatus;
import obp3.modelchecking.tools.BuchiModelCheckerModel;
import obp3.modelchecking.tools.XModelCheckerBuilder;
import obp3.runtime.IExecutable;
import obp3.runtime.sli.DependentSemanticRelation;
import obp3.runtime.sli.SemanticRelation;
import obp3.runtime.sli.Step;
import obp3.sli.core.operators.product.Product;
import obp3.traversal.dfs.DepthFirstTraversal;
import rege.reader.infra.ParseResult;

import java.util.function.BiPredicate;

public class StepModelChecker<MA, MC> {
    //model SLI
    SemanticRelation<MA, MC> modelSemantics;
    AtomEvaluator<Step<MA, MC>> atomicPropositionEvaluator;

    //property
    SyntaxTreeElement propertyModel;
    Automaton automaton;

    //options
    BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm emptinessCheckerAlgorithm;
    DepthFirstTraversal.Algorithm traversalAlgorithm;
    int depthBound;

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            AtomEvaluator<Step<MA, MC>> atomicPropositionEvaluator,
            String property) {
        var modelP = Reader.parseDeclarationsWithPositions(property);
        var model = Reader.linkWithPositions(modelP);
        switch (model) {
            case ParseResult.Success<Declarations> success -> {
                this.modelSemantics = modelSemantics;
                this.atomicPropositionEvaluator = atomicPropositionEvaluator;
                this.propertyModel = success.value().declarations().getLast();
                this.automaton = Semantics.toAutomaton(this.propertyModel);
                this.emptinessCheckerAlgorithm = BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm.GS09_CDLP05_SEPARATED;
                this.traversalAlgorithm = DepthFirstTraversal.Algorithm.WHILE;
                this.depthBound = -1;
            }
            case ParseResult.Failure<Declarations> failure -> {
                throw new IllegalArgumentException("Failed to parse property: " + failure.formatErrors());
            }
        }
    }

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            AtomEvaluator<Step<MA, MC>> atomicPropositionEvaluator,
            SyntaxTreeElement propertyModel) {
        this(
                modelSemantics,
                atomicPropositionEvaluator,
                propertyModel,
                BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm.GS09_CDLP05_SEPARATED,
                DepthFirstTraversal.Algorithm.WHILE,
                -1
        );
    }

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            AtomEvaluator<Step<MA, MC>> atomicPropositionEvaluator,
            SyntaxTreeElement propertyModel,
            BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm emptinessCheckerAlgorithm,
            DepthFirstTraversal.Algorithm traversal,
            int depthBound) {
        this.modelSemantics = modelSemantics;
        this.atomicPropositionEvaluator = atomicPropositionEvaluator;
        this.propertyModel = propertyModel;
        this.emptinessCheckerAlgorithm = emptinessCheckerAlgorithm;
        this.traversalAlgorithm = traversal;
        this.depthBound = depthBound;
    }

    DependentSemanticRelation<Step<MA, MC>, Transition, State> propertySemanticsProvider(BiPredicate<String, Step<MA, MC>> atomEval) {
        return new AutomatonSemantics<>(automaton, AtomEvaluator.from(atomEval));
    }

    public IExecutable<EmptinessCheckerStatus, EmptinessCheckerAnswer<Product<MC, State>>> modelChecker() {
        var builder =
                new XModelCheckerBuilder<MA, MC, Transition, State>()
                        .modelSemantics(modelSemantics)
                        .atomicPropositionEvaluator(atomicPropositionEvaluator.toBiPredicate())
                        .propertySemantics(this::propertySemanticsProvider)
                        .acceptingPredicateForProduct((c, sem) ->((AutomatonSemantics<Step<MA, MC>>)sem.r()).isAccepting(c.r()))
                        .buchi(automaton.semanticsKind() == AutomatonSemanticsKind.BUCHI)
                        .emptinessCheckerAlgorithm(emptinessCheckerAlgorithm)
                        .traversalStrategy(traversalAlgorithm)
                        .depthBound(depthBound);
        return builder.modelChecker();
    }
}
