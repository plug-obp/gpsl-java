package gpsl.lsp;

import gpsl.syntax.Reader;
import gpsl.syntax.PositionMap;
import gpsl.syntax.model.Declarations;
import gpsl.syntax.model.Expression;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;
import rege.reader.infra.ParseError;
import rege.reader.infra.ParseResult;
import rege.reader.infra.Range;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GPSLTextDocumentService implements TextDocumentService {
    private final GPSLLanguageServer server;
    private final Map<String, String> openDocs = new HashMap<>();

    public GPSLTextDocumentService(GPSLLanguageServer server) {
        this.server = server;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        var doc = params.getTextDocument();
        openDocs.put(doc.getUri(), doc.getText());
        publishDiagnostics(doc.getUri(), doc.getText());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        var uri = params.getTextDocument().getUri();
        var changes = params.getContentChanges();
        if (!changes.isEmpty()) {
            // Simplest: treat last change as full text
            String text = changes.get(changes.size() - 1).getText();
            openDocs.put(uri, text);
            publishDiagnostics(uri, text);
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        var uri = params.getTextDocument().getUri();
        openDocs.remove(uri);
        publishEmptyDiagnostics(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) { }

    private void publishDiagnostics(String uri, String text) {
        var decls = Reader.parseDeclarationsWithPositions(text);
        var expr = Reader.parseExpressionWithPositions(text);
        var chosen = pickBest(decls, expr);
        var diags = toDiagnostics(chosen.result());
        server.client().publishDiagnostics(new PublishDiagnosticsParams(uri, diags));
    }

    private static Reader.ParseResultWithPositions<?> pickBest(
            Reader.ParseResultWithPositions<Declarations> decls,
            Reader.ParseResultWithPositions<Expression> expr) {
        if (decls.result().isSuccess()) return decls;
        if (expr.result().isSuccess()) return expr;
        int d = ((ParseResult.Failure<?>) decls.result()).errors().size();
        int e = ((ParseResult.Failure<?>) expr.result()).errors().size();
        return d >= e ? decls : expr;
    }

    private static List<Diagnostic> toDiagnostics(ParseResult<?> result) {
        if (result.isSuccess()) return List.of();
        var failure = (ParseResult.Failure<?>) result;
        List<Diagnostic> out = new ArrayList<>();
        for (ParseError err : failure.errors()) {
            out.add(toDiagnostic(err));
        }
        return out;
    }

    private static Diagnostic toDiagnostic(ParseError err) {
        var d = new Diagnostic();
        d.setMessage(err.message());
        d.setSeverity(toSeverity(err.severity()));
        d.setRange(toLspRange(err.range()));
        err.code().ifPresent(d::setCode);
        return d;
    }

    private static DiagnosticSeverity toSeverity(ParseError.Severity s) {
        return switch (s) {
            case ERROR -> DiagnosticSeverity.Error;
            case WARNING -> DiagnosticSeverity.Warning;
            case INFO -> DiagnosticSeverity.Information;
            case HINT -> DiagnosticSeverity.Hint;
        };
    }

    // Convert 1-based Range (reader-infra) to 0-based LSP Range
    private static org.eclipse.lsp4j.Range toLspRange(Range r) {
        var start = r.start();
        var end = r.end();
        var lspStart = new Position(Math.max(0, start.line() - 1), Math.max(0, start.column() - 1));
        var lspEnd = new Position(Math.max(0, end.line() - 1), Math.max(0, end.column() - 1));
        return new org.eclipse.lsp4j.Range(lspStart, lspEnd);
    }

    // Utility for clearing diagnostics
    private void publishEmptyDiagnostics(String uri) {
        server.client().publishDiagnostics(new PublishDiagnosticsParams(uri, List.of()));
    }
}
