package gpsl.ltl3ba;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeExecutable {
    private final Path executablePath;
    
    public NativeExecutable() throws IOException {
        this.executablePath = extractExecutable();
    }
    
    private Path extractExecutable() throws IOException {
        String platform = detectPlatform();
        String resourcePath = "/native/ltl3ba/" + platform + "/" + getExecutableName(platform);
        
        // Extract from JAR to temp location
        Path tempDir = Files.createTempDirectory("ltl3ba-native");
        Path tempFile = tempDir.resolve(getExecutableName(platform));
        
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Native executable not found for platform: " + platform);
            }
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Set executable permissions (Unix-like systems)
        if (!isWindows()) {
            tempFile.toFile().setExecutable(true);
        }
        
        // Delete on exit
        tempFile.toFile().deleteOnExit();
        tempDir.toFile().deleteOnExit();
        
        return tempFile;
    }
    
    private String detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch");
        
        if (osName.contains("linux")) {
            return arch.equals("aarch64") ? "linux-arm64" : "linux-x64";
        } else if (osName.contains("mac")) {
            return arch.equals("aarch64") ? "mac-arm64" : "mac-x64";
        } else if (osName.contains("windows")) {
            return "windows-x64";
        }
        throw new UnsupportedOperationException("Unsupported platform: " + osName + "/" + arch);
    }
    
    private String getExecutableName(String platform) {
        return platform.startsWith("windows") ? "ltl3ba.exe" : "ltl3ba";
    }
    
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    public ProcessBuilder createProcess(String... args) {
        String[] command = new String[args.length + 1];
        command[0] = executablePath.toAbsolutePath().toString();
        System.arraycopy(args, 0, command, 1, args.length);
        
        return new ProcessBuilder(command);
    }
}
