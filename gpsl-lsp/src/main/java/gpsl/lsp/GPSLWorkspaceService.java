package gpsl.lsp;

import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class GPSLWorkspaceService implements WorkspaceService {
    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        // No-op for now
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        // No-op for now
    }

    @Override
    public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
        // No-op for now
    }
}
