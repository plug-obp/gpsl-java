package gpsl.ltl3ba;

import gpsl.syntax.Reader;
import gpsl.syntax.model.Automaton;
import gpsl.syntax.model.Expression;

public class Expression2Automaton {

    public static Automaton convert(Expression gpslExpression) throws Exception {

        //Convert GPSL expression to LTL3BA formula
        var transformer = new LTL3BATransformer();
        String ltlFormula = gpslExpression.accept(transformer, null);
        
        //Fetch buchi automaton text from LTL3BA
        var automatonText = LTL3BA.getInstance().convert(ltlFormula);
        
        //Read automaton from text
        var automaton = AutomatonReaderFromLTL3BA.read(automatonText);

        // Convert atom map to Object map for linking
        java.util.Map<String, Object> atomContext = new java.util.HashMap<>(transformer.getNameToAtomMap());
        Reader.link(automaton, atomContext);
       
        return automaton;
    }

    private static class AutomatonReaderFromLTL3BA {
        static Automaton read(String automatonText) {
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
                
                // Parse guard expression
                Expression guard = Reader.readExpression(guardStr);
                
                // Create transition with priority 0
                gpsl.syntax.model.Transition transition = new gpsl.syntax.model.Transition(
                    sourceState, 0, guard, targetState
                );
                transitions.add(transition);
            }
            
            return new Automaton(
                gpsl.syntax.model.AutomatonSemanticsKind.BUCHI,
                states,
                initialStates,
                acceptStates,
                transitions
            );
        }
    }

}
