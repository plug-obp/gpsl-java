package gpsl.ltl3ba;

import gpsl.syntax.Reader;
import gpsl.syntax.model.Automaton;
import gpsl.syntax.model.Expression;
import rege.reader.infra.ParseResult;
import rege.reader.infra.ParseError;

import java.util.ArrayList;
import java.util.List;

public class Expression2Automaton {

    /**
     * Converts a GPSL expression to a Büchi automaton using LTL3BA.
     * 
     * @param gpslExpression the GPSL expression to convert
     * @return ParseResult containing the automaton or errors from guard parsing
     */
    public static ParseResult<Automaton> convert(Expression gpslExpression) {
        try {
            //Convert GPSL expression to LTL3BA formula
            var transformer = new LTL3BATransformer();
            String ltlFormula = gpslExpression.accept(transformer, null);
            
            //Fetch buchi automaton text from LTL3BA
            var automatonText = LTL3BA.getInstance().convert(ltlFormula);
            
            // Convert atom map to Object map for parsing context
            java.util.Map<String, Object> atomContext = new java.util.HashMap<>(transformer.getNameToAtomMap());
            
            //Read automaton from text with atom context
            return AutomatonReaderFromLTL3BA.read(automatonText, atomContext);
        } catch (Exception e) {
            // Wrap LTL3BA errors as parse errors
            ParseError error = new ParseError(
                null, // no specific range for LTL3BA conversion errors
                "LTL3BA conversion failed: " + e.getMessage(),
                "ltl3ba-error"
            );
            return new ParseResult.Failure<>(List.of(error), "");
        }
    }

    private static class AutomatonReaderFromLTL3BA {
        static ParseResult<Automaton> read(String automatonText, java.util.Map<String, Object> context) {
            List<ParseError> errors = new ArrayList<>();
            
            // Parse LTL3BA output format (CSV-like: source,target,guard,...)
            String[] lines = automatonText.split("\n");
            
            // Skip first line (acc = "...") header
            java.util.Set<gpsl.syntax.model.State> states = new java.util.HashSet<>();
            java.util.Set<gpsl.syntax.model.State> initialStates = new java.util.HashSet<>();
            java.util.Set<gpsl.syntax.model.State> acceptStates = new java.util.HashSet<>();
            java.util.List<gpsl.syntax.model.Transition> transitions = new java.util.ArrayList<>();
            
            // Process all lines after the header (line 0)
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                
                // Parse CSV line: source,target,guard,...
                String[] parts = line.split(",");
                // Take only first 3 parts and remove quotes
                String source = parts[0].replace("\"", "").trim();
                String target = parts[1].replace("\"", "").trim();
                String guardStr = parts[2].replace("\"", "").trim();
                
                // Create or retrieve states
                gpsl.syntax.model.State sourceState = new gpsl.syntax.model.State(source);
                gpsl.syntax.model.State targetState = new gpsl.syntax.model.State(target);
                
                states.add(sourceState);
                states.add(targetState);
                
                // Check for initial states (contains "init")
                if (source.contains("init")) {
                    initialStates.add(sourceState);
                }
                if (target.contains("init")) {
                    initialStates.add(targetState);
                }
                
                // Check for accept states (contains "accept")
                if (source.contains("accept")) {
                    acceptStates.add(sourceState);
                }
                if (target.contains("accept")) {
                    acceptStates.add(targetState);
                }
                
                // Parse guard expression and link with context - collect errors instead of throwing
                var guardResultWithPos = Reader.parseExpressionWithPositions(guardStr);
                ParseResult<Expression> guardResult = guardResultWithPos.result();
                if (guardResult.isSuccess()) {
                    guardResult = Reader.link(((ParseResult.Success<Expression>) guardResult).value(), 
                                            guardResultWithPos.source(),
                                            guardResultWithPos.positionMap(), 
                                            context);
                }
                
                if (guardResult instanceof ParseResult.Success<Expression> success) {
                    Expression guard = success.value();
                    
                    // Create transition with priority 0
                    gpsl.syntax.model.Transition transition = new gpsl.syntax.model.Transition(
                        sourceState, 0, guard, targetState
                    );
                    transitions.add(transition);
                } else if (guardResult instanceof ParseResult.Failure<Expression> failure) {
                    // Collect guard parsing errors with context about which transition failed
                    for (ParseError error : failure.errors()) {
                        ParseError contextualError = new ParseError(
                            error.range(),
                            "Failed to parse guard expression '" + guardStr + "' for transition " 
                                + source + " -> " + target + ": " + error.message(),
                            error.code().orElse("guard-parse-error")
                        );
                        errors.add(contextualError);
                    }
                }
            }
            
            // If there were any errors parsing guards, return failure
            if (!errors.isEmpty()) {
                return new ParseResult.Failure<>(errors, automatonText);
            }
            
            Automaton automaton = new Automaton(
                gpsl.syntax.model.AutomatonSemanticsKind.BUCHI,
                states,
                initialStates,
                acceptStates,
                transitions
            );
            
            return new ParseResult.Success<>(automaton);
        }
    }

}
