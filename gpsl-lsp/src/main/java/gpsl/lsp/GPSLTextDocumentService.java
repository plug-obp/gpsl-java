package gpsl.lsp;

import gpsl.syntax.Reader;
import gpsl.syntax.PositionMap;
import gpsl.syntax.model.*;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import rege.reader.infra.ParseError;
import rege.reader.infra.ParseResult;
import rege.reader.infra.Range;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GPSLTextDocumentService implements TextDocumentService {
    private final GPSLLanguageServer server;
    private final Map<String, String> openDocs = new HashMap<>();
    // Cache parsed and linked results for go-to-definition
    private final Map<String, ParsedDocument> parsedDocs = new HashMap<>();

    public GPSLTextDocumentService(GPSLLanguageServer server) {
        this.server = server;
    }
    
    // Helper record to store parsed and linked declarations with positions
    private record ParsedDocument(Declarations declarations, PositionMap positionMap) {}

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
            // Get full document text from the change
            // LSP sends full document when TextDocumentSyncKind is Full
            var change = changes.get(0);
            String text = change.getText();
            openDocs.put(uri, text);
            publishDiagnostics(uri, text);
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        var uri = params.getTextDocument().getUri();
        openDocs.remove(uri);
        parsedDocs.remove(uri);
        publishEmptyDiagnostics(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) { }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params) {
        String uri = params.getTextDocument().getUri();
        ParsedDocument parsed = parsedDocs.get(uri);
        
        if (parsed == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        // Use visitor to extract document symbols
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = parsed.declarations.accept(visitor, parsed.positionMap);
        
        // Wrap in Either for LSP (supports both flat SymbolInformation and hierarchical DocumentSymbol)
        List<Either<SymbolInformation, DocumentSymbol>> result = symbols.stream()
            .map(Either::<SymbolInformation, DocumentSymbol>forRight)
            .collect(java.util.stream.Collectors.toList());
        
        return CompletableFuture.completedFuture(result);
    }

        @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        String uri = params.getTextDocument().getUri();
        Position position = params.getPosition();
        
        ParsedDocument parsed = parsedDocs.get(uri);
        if (parsed == null) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        
        // Find the reference at the cursor position
        Reference ref = findReferenceAtPosition(parsed, position);
        if (ref == null) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        
        // Get the linked expression
        Expression linkedExpr = ref.expression();
        if (linkedExpr == null) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        
        // Find the declaration that contains this expression
        ExpressionDeclaration targetDecl = findDeclarationByExpression(parsed, linkedExpr);
        if (targetDecl == null) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        
        // Get the position range for the target declaration
        var range = parsed.positionMap.get(targetDecl);
        if (range.isEmpty()) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        
        // Convert to LSP range and return location
        var lspRange = toLspRange(range.get());
        Location location = new Location(uri, lspRange);
        
        return CompletableFuture.completedFuture(Either.forLeft(List.of(location)));
    }

    private Reference findReferenceAtPosition(ParsedDocument parsed, Position position) {
        // Convert LSP position (0-based) to 1-based line/column
        int line = position.getLine() + 1;
        int column = position.getCharacter() + 1;
        
        // Search all references in the declarations
        var visitor = new ReferenceFinderVisitor(line, column, parsed.positionMap);
        for (var decl : parsed.declarations.declarations()) {
            findReferencesInExpression(decl.expression(), visitor);
            if (visitor.foundReference != null) break;
        }
        return visitor.foundReference;
    }
    
    private ExpressionDeclaration findDeclarationByExpression(ParsedDocument parsed, Expression targetExpression) {
        // Search top-level declarations
        for (var decl : parsed.declarations.declarations()) {
            if (decl.expression() == targetExpression) {
                return decl;
            }
            // Also search in nested expressions (let expressions, etc.)
            var result = findDeclarationByExpressionInTree(decl.expression(), targetExpression);
            if (result != null) return result;
        }
        return null;
    }
    
    private ExpressionDeclaration findDeclarationByExpressionInTree(Expression expr, Expression targetExpression) {
        if (expr instanceof LetExpression let) {
            // Check if any binding has this expression
            for (var binding : let.declarations().declarations()) {
                if (binding.expression() == targetExpression) {
                    return binding;
                }
                // Recursively search in the binding's expression
                var result = findDeclarationByExpressionInTree(binding.expression(), targetExpression);
                if (result != null) return result;
            }
            // Search in the body
            if (let.expression() instanceof Expression bodyExpr) {
                return findDeclarationByExpressionInTree(bodyExpr, targetExpression);
            }
        } else if (expr instanceof BinaryExpression bin) {
            var left = findDeclarationByExpressionInTree(bin.left(), targetExpression);
            if (left != null) return left;
            return findDeclarationByExpressionInTree(bin.right(), targetExpression);
        } else if (expr instanceof UnaryExpression un) {
            return findDeclarationByExpressionInTree(un.expression(), targetExpression);
        }
        return null;
    }
    
    private void findReferencesInExpression(Expression expr, ReferenceFinderVisitor visitor) {
        if (visitor.foundReference != null) return; // Already found
        
        if (expr instanceof Reference ref) {
            visitor.checkReference(ref);
        } else if (expr instanceof BinaryExpression bin) {
            findReferencesInExpression(bin.left(), visitor);
            findReferencesInExpression(bin.right(), visitor);
        } else if (expr instanceof UnaryExpression un) {
            findReferencesInExpression(un.expression(), visitor);
        } else if (expr instanceof LetExpression let) {
            // Search in all bindings
            for (var binding : let.declarations().declarations()) {
                findReferencesInExpression(binding.expression(), visitor);
            }
            // Search in the body (can be Expression or Automaton)
            if (let.expression() instanceof Expression bodyExpr) {
                findReferencesInExpression(bodyExpr, visitor);
            } else if (let.expression() instanceof Automaton automaton) {
                // Search through all transition guards
                for (var transition : automaton.transitions()) {
                    findReferencesInExpression(transition.guard(), visitor);
                }
            }
        }
    }
    
    // Helper class to find a reference at a specific position
    private static class ReferenceFinderVisitor {
        private final int targetLine;
        private final int targetColumn;
        private final PositionMap positionMap;
        Reference foundReference = null;
        
        ReferenceFinderVisitor(int line, int column, PositionMap positionMap) {
            this.targetLine = line;
            this.targetColumn = column;
            this.positionMap = positionMap;
        }
        
        void checkReference(Reference ref) {
            var range = positionMap.get(ref);
            if (range.isPresent()) {
                var r = range.get();
                // Check if cursor position is within this reference
                if (isPositionInRange(targetLine, targetColumn, r)) {
                    foundReference = ref;
                }
            }
        }
        
        private boolean isPositionInRange(int line, int column, Range range) {
            var start = range.start();
            var end = range.end();
            
            if (line < start.line() || line > end.line()) {
                return false;
            }
            if (line == start.line() && column < start.column()) {
                return false;
            }
            if (line == end.line() && column > end.column()) {
                return false;
            }
            return true;
        }
    }

    private void publishDiagnostics(String uri, String text) {
        // Try parsing as declarations first (normal .gpsl files)
        var declsResult = Reader.parseDeclarationsWithPositions(text);
        
        // Only try expression if it's clearly not a declarations file
        // (i.e., declarations failed and text doesn't contain '=')
        Reader.ParseResultWithPositions<?> chosen = declsResult;
        
        List<Diagnostic> diags = new ArrayList<>(toDiagnostics(chosen.result()));
        
        // Cache successfully parsed declarations for go-to-definition
        if (chosen.result().isSuccess()) {
            var linkResult = Reader.linkWithPositions(declsResult);
            
            // Get the parsed declarations (even if linking partially failed)
            var declarations = (Declarations) ((ParseResult.Success<?>) chosen.result()).value();
            
            // Always cache the declarations so go-to-definition works for resolved symbols
            // Even if there are undefined symbols, the defined ones should still work
            parsedDocs.put(uri, new ParsedDocument(declarations, declsResult.positionMap()));
            
            // Add linking errors as diagnostics (e.g., undefined references)
            if (!linkResult.isSuccess()) {
                diags.addAll(toDiagnostics(linkResult));
            }
        } else {
            parsedDocs.remove(uri);
        }
        
        server.client().publishDiagnostics(new PublishDiagnosticsParams(uri, diags));
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
