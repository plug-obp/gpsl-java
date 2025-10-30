package gpsl.modelchecker;

import gpsl.semantics.AtomEvaluator;
import gpsl.semantics.Semantics;
import gpsl.syntax.Reader;
import gpsl.syntax.model.*;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.modelchecking.tools.BuchiModelCheckerModel;
import obp3.modelchecking.tools.ModelCheckerBuilder;
import obp3.runtime.IExecutable;
import obp3.runtime.sli.SemanticRelation;
import obp3.runtime.sli.Step;
import obp3.traversal.dfs.DepthFirstTraversal;
import rege.reader.infra.ParseResult;

public class StepModelChecker<MA, MC> {
    //model SLI
    SemanticRelation<MA, MC> modelSemantics;
    AtomEvaluator<Step<MA, MC>> atomicPropositionEvaluator;

    //property
    SyntaxTreeElement propertyModel;

    //options
    BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm emptinessCheckerAlgorithm;
    DepthFirstTraversal.Algorithm traversalAlgorithm;
    int depthBound;

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            AtomEvaluator<Step<MA, MC>> atomicPropositionEvaluator,
            String property) {
        var modelP = Reader.parseExpressionWithPositions(property);
        var model = Reader.linkWithPositions(modelP);
        switch (model) {
            case ParseResult.Success<Expression> success -> {
                this.modelSemantics = modelSemantics;
                this.atomicPropositionEvaluator = atomicPropositionEvaluator;
                this.propertyModel = success.value();
                this.emptinessCheckerAlgorithm = BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm.GS09_CDLP05_SEPARATED;
                this.traversalAlgorithm = DepthFirstTraversal.Algorithm.WHILE;
                this.depthBound = -1;
            }
            case ParseResult.Failure<Expression> failure -> {
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

    IExecutable<EmptinessCheckerAnswer<?>> modelChecker() {
        var propertySemantics = new Semantics<>(propertyModel, atomicPropositionEvaluator);
        var builder =
                new ModelCheckerBuilder<MA, MC, Transition, State>()
                        .modelSemantics(modelSemantics)
                        .propertySemantics(propertySemantics)
                        .acceptingPredicateForProduct((c) -> propertySemantics.isAccepting(c.r()))
                        .buchi(propertySemantics.getAutomaton().semanticsKind() == AutomatonSemanticsKind.BUCHI)
                        .emptinessCheckerAlgorithm(emptinessCheckerAlgorithm)
                        .traversalStrategy(traversalAlgorithm)
                        .depthBound(depthBound);
        return builder.modelChecker();
    }
}
