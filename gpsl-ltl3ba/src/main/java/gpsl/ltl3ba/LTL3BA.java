package gpsl.ltl3ba;

import java.io.IOException;

public class LTL3BA {
    private static volatile LTL3BA instance;
    private final NativeExecutable nativeExecutable;
    
    private LTL3BA() throws Exception {
        this.nativeExecutable = new NativeExecutable();
    }
    
    public static LTL3BA getInstance() throws Exception {
        if (instance == null) {
            synchronized (LTL3BA.class) {
                if (instance == null) {
                    instance = new LTL3BA();
                }
            }
        }
        return instance;
    }
    
    public String convert(String ltlFormula) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = nativeExecutable.createProcess(
                "-T3", // build the BA
                "-f", ltlFormula
        );
        Process process = processBuilder.start();
        
        // Read output
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            String error = new String(process.getErrorStream().readAllBytes());
            throw new RuntimeException("ltl3ba failed: " + error);
        }
        
        return output;
    }   
}
