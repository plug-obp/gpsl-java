package gpsl.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LSP go-to-definition feature.
 * These tests verify that references correctly jump to their declarations
 * with proper scope resolution.
 */
class GoToDefinitionTest {
    
    private GPSLLanguageServer server;
    private GPSLTextDocumentService textDocumentService;
    
    @BeforeEach
    void setUp() {
        server = new GPSLLanguageServer();
        textDocumentService = (GPSLTextDocumentService) server.getTextDocumentService();
        
        // Connect a simple mock client
        server.connect(new MockLanguageClient());
    }
    
    // Simple mock client that does nothing
    private static class MockLanguageClient implements LanguageClient {
        @Override
        public void telemetryEvent(Object object) {}
        
        @Override
        public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {}
        
        @Override
        public void showMessage(MessageParams messageParams) {}
        
        @Override
        public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
            return CompletableFuture.completedFuture(null);
        }
        
        @Override
        public void logMessage(MessageParams message) {}
    }
    
    @Test
    void testSimpleTopLevelReference() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            p = true
            q = false
            result = p and q
            """;
        
        // Open document
        openDocument(uri, text);
        
        // Click on 'p' in "result = p and q" (line 2, column 9)
        Position position = new Position(2, 9);
        var result = getDefinition(uri, position);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Location location = result.get(0);
        assertEquals(uri, location.getUri());
        
        // Should jump to "p = true" on line 0
        assertEquals(0, location.getRange().getStart().getLine());
    }
    
    @Test
    void testLetExpressionBinding() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            result = let x = true in x and false
            """;
        
        openDocument(uri, text);
        
        // Click on 'x' in "x and false" (line 0, column 25)
        Position position = new Position(0, 25);
        var result = getDefinition(uri, position);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Location location = result.get(0);
        // Should jump to "x = true" in the let binding
        assertEquals(0, location.getRange().getStart().getLine());
        assertTrue(location.getRange().getStart().getCharacter() < 25);
    }
    
    @Test
    void testNestedLetExpression() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            result = let 
                x = true,
                y = false,
                z = x and true
            in 
                z xor x
            """;
        
        openDocument(uri, text);
        
        // Click on first 'x' in "z xor x" (line 5, column 10)
        Position position = new Position(5, 10);
        var result = getDefinition(uri, position);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Location location = result.get(0);
        // Should jump to "x = true" on line 1
        assertEquals(1, location.getRange().getStart().getLine());
    }
    
    @Test
    void testLetExpressionWithSameNameInBody() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            result = let 
                safety = true,
                liveness = false,
                spec = safety and liveness
            in
                spec
            """;
        
        openDocument(uri, text);
        
        // Click on 'safety' in "spec = safety and liveness" (line 3, column 11)
        Position position = new Position(3, 11);
        var result = getDefinition(uri, position);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Location location = result.get(0);
        // Should jump to "safety = true" on line 1
        assertEquals(1, location.getRange().getStart().getLine());
    }
    
    @Test
    void testMultipleReferencesToSameName() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            p = true
            q = p and p
            r = p or q
            """;
        
        openDocument(uri, text);
        
        // Click on first 'p' in "q = p and p" (line 1, column 4)
        Position firstP = new Position(1, 4);
        var result1 = getDefinition(uri, firstP);
        
        assertNotNull(result1);
        assertEquals(1, result1.size());
        assertEquals(0, result1.get(0).getRange().getStart().getLine());
        
        // Click on second 'p' in "q = p and p" (line 1, column 10)
        Position secondP = new Position(1, 10);
        var result2 = getDefinition(uri, secondP);
        
        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals(0, result2.get(0).getRange().getStart().getLine());
        
        // Click on 'p' in "r = p or q" (line 2, column 4)
        Position thirdP = new Position(2, 4);
        var result3 = getDefinition(uri, thirdP);
        
        assertNotNull(result3);
        assertEquals(1, result3.size());
        assertEquals(0, result3.get(0).getRange().getStart().getLine());
    }
    
    @Test
    void testReferenceInAutomaton() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            p = |p|
            q = |q|
            internalHelper = p and q
            myNFA = nfa
                states s0, s1, s2;
                initial s0;
                accept s2;
                s0 [true] s1;
                s1 [internalHelper] s2;
                s2 [false] s0
            """;
        
        openDocument(uri, text);
        
        // Click on 'internalHelper' in "s1 [internalHelper] s2" (line 8, column 8)
        Position position = new Position(8, 8);
        var result = getDefinition(uri, position);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Location location = result.get(0);
        // Should jump to "internalHelper = p and q" on line 2
        assertEquals(2, location.getRange().getStart().getLine());
    }
    
    @Test
    void testNoDefinitionForUndefinedReference() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            result = undefined and true
            """;
        
        openDocument(uri, text);
        
        // Click on 'undefined' (line 0, column 9)
        Position position = new Position(0, 9);
        var result = getDefinition(uri, position);
        
        // Should return empty list for undefined reference
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testNoDefinitionOnAtom() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            p = |some atom|
            """;
        
        openDocument(uri, text);
        
        // Click on the atom (line 0, column 6)
        Position position = new Position(0, 6);
        var result = getDefinition(uri, position);
        
        // Atoms are not references, should return empty
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testNoDefinitionOnLiteral() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            p = true and false
            """;
        
        openDocument(uri, text);
        
        // Click on 'true' literal (line 0, column 4)
        Position position = new Position(0, 4);
        var result = getDefinition(uri, position);
        
        // Literals are not references, should return empty
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testDefinitionAfterDocumentChange() throws Exception {
        String uri = "file:///test.gpsl";
        String originalText = """
            p = true
            result = p and false
            """;
        
        openDocument(uri, originalText);
        
        // First check - should find definition
        Position position = new Position(1, 9);
        var result1 = getDefinition(uri, position);
        assertNotNull(result1);
        assertEquals(1, result1.size());
        
        // Change document
        String newText = """
            p = false
            q = true
            result = p and q
            """;
        
        changeDocument(uri, newText);
        
        // Click on 'p' in new document (line 2, column 9)
        Position newPosition = new Position(2, 9);
        var result2 = getDefinition(uri, newPosition);
        
        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals(0, result2.get(0).getRange().getStart().getLine());
    }
    
    @Test
    void testDefinitionWithSyntaxError() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            p = true
            q = p and
            """;
        
        openDocument(uri, text);
        
        // Even with syntax error, previously valid definitions should not be available
        Position position = new Position(1, 4);
        var result = getDefinition(uri, position);
        
        // Should return empty because parsing failed
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    // Helper methods
    
    private void openDocument(String uri, String text) {
        var params = new DidOpenTextDocumentParams();
        var textDocument = new TextDocumentItem();
        textDocument.setUri(uri);
        textDocument.setLanguageId("gpsl");
        textDocument.setVersion(1);
        textDocument.setText(text);
        params.setTextDocument(textDocument);
        
        textDocumentService.didOpen(params);
    }
    
    private void changeDocument(String uri, String text) {
        var params = new DidChangeTextDocumentParams();
        var versionedId = new VersionedTextDocumentIdentifier();
        versionedId.setUri(uri);
        versionedId.setVersion(2);
        params.setTextDocument(versionedId);
        
        var change = new TextDocumentContentChangeEvent();
        change.setText(text);
        params.setContentChanges(List.of(change));
        
        textDocumentService.didChange(params);
    }
    
    private List<Location> getDefinition(String uri, Position position) throws Exception {
        var params = new DefinitionParams();
        var textDocId = new TextDocumentIdentifier(uri);
        params.setTextDocument(textDocId);
        params.setPosition(position);
        
        CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> future = 
            textDocumentService.definition(params);
        
        Either<List<? extends Location>, List<? extends LocationLink>> result = future.get();
        
        if (result.isLeft()) {
            @SuppressWarnings("unchecked")
            List<Location> locations = (List<Location>) result.getLeft();
            return locations;
        } else {
            // Convert LocationLink to Location if needed
            return result.getRight().stream()
                .map(link -> new Location(link.getTargetUri(), link.getTargetRange()))
                .toList();
        }
    }
    
    @Test
    void testUndefinedSymbolsShowDiagnosticsButDefinedSymbolsWork() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            x = true
            y = x and undefinedVar
            z = y
            """;
        
        openDocument(uri, text);
        
        // Should be able to go to definition for 'x' even though 'undefinedVar' is undefined
        Position positionX = new Position(1, 4); // 'x' in "y = x and undefinedVar"
        var resultX = getDefinition(uri, positionX);
        
        assertNotNull(resultX);
        assertEquals(1, resultX.size());
        assertEquals(0, resultX.get(0).getRange().getStart().getLine()); // Should jump to "x = true"
        
        // Should be able to go to definition for 'y'
        Position positionY = new Position(2, 4); // 'y' in "z = y"
        var resultY = getDefinition(uri, positionY);
        
        assertNotNull(resultY);
        assertEquals(1, resultY.size());
        assertEquals(1, resultY.get(0).getRange().getStart().getLine()); // Should jump to "y = x and undefinedVar"
    }
    
    @Test
    void testDoublePipeOperator() throws Exception {
        String uri = "file:///test.gpsl";
        String text = """
            pp = true
            qq = false
            test = pp || qq
            """;
        
        openDocument(uri, text);
        
        // Should be able to go to definition for 'pp' (left side of ||)
        Position positionPP = new Position(2, 7); // 'pp' in "test = pp || qq"
        var resultPP = getDefinition(uri, positionPP);
        
        assertNotNull(resultPP);
        assertEquals(1, resultPP.size());
        assertEquals(0, resultPP.get(0).getRange().getStart().getLine()); // Should jump to "pp = true"
        
        // Should be able to go to definition for 'qq' (right side of ||)
        Position positionQQ = new Position(2, 14); // 'qq' in "test = pp || qq"
        var resultQQ = getDefinition(uri, positionQQ);
        
        assertNotNull(resultQQ);
        assertEquals(1, resultQQ.size());
        assertEquals(1, resultQQ.get(0).getRange().getStart().getLine()); // Should jump to "qq = false"
    }
    
    @Test
    void testMultipleConsecutiveLinesWithPipeOperator() throws Exception {
        String uri = "file:///test/multi_pipe.gpsl";
        String text = """
            pp = true
            qq = false
            disj2 = pp || qq
            disj3 = pp || qq
            """;
        
        openDocument(uri, text);
        
        // Test go-to-definition for 'qq' on first line with ||
        Position positionQQ = new Position(2, 14); // 'qq' in "disj2 = pp || qq"
        var resultQQ = getDefinition(uri, positionQQ);
        
        assertNotNull(resultQQ, "Should find definition for 'qq'");
        assertEquals(1, resultQQ.size(), "Should find exactly one definition for 'qq'");
        assertEquals(1, resultQQ.get(0).getRange().getStart().getLine()); // Should jump to "qq = false"
        
        // Test go-to-definition for 'pp' on first line
        Position positionPP = new Position(2, 8); // 'pp' in "disj2 = pp || qq"
        var resultPP = getDefinition(uri, positionPP);
        
        assertNotNull(resultPP, "Should find definition for 'pp'");
        assertEquals(1, resultPP.size());
        assertEquals(0, resultPP.get(0).getRange().getStart().getLine()); // Should jump to "pp = true"
        
        // Test go-to-definition for 'qq' on second line with ||
        Position positionQQ2 = new Position(3, 14); // 'qq' in "disj3 = pp || qq"
        var resultQQ2 = getDefinition(uri, positionQQ2);
        
        assertNotNull(resultQQ2, "Should find definition for 'qq' with double pipe");
        assertEquals(1, resultQQ2.size());
        assertEquals(1, resultQQ2.get(0).getRange().getStart().getLine()); // Should jump to "qq = false"
    }
}
