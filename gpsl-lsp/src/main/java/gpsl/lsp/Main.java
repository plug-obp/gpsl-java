package gpsl.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;

public class Main {
    public static void main(String[] args) throws Exception {
        var server = new GPSLLanguageServer();
        var launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);
        server.connect(launcher.getRemoteProxy());
        launcher.startListening().get();
    }
}
