package gpsl.lsp;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

public class GPSLLanguageServer implements LanguageServer, LanguageClientAware {
    private LanguageClient client;
    private final GPSLTextDocumentService textDocumentService = new GPSLTextDocumentService(this);
    private final WorkspaceService workspaceService = new GPSLWorkspaceService();

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    public LanguageClient client() {
        return client;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities caps = new ServerCapabilities();
        caps.setTextDocumentSync(TextDocumentSyncKind.Incremental);
        return CompletableFuture.completedFuture(new InitializeResult(caps));
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() { }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
