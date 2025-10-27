package gpsl.lsp;

import gpsl.syntax.Reader;
import gpsl.syntax.model.Declarations;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;
import org.junit.jupiter.api.Test;
import rege.reader.infra.ParseResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for document symbols (outline view) feature.
 */
class DocumentSymbolTest {

    @Test
    void testSimpleDeclarations() {
        String input = "p = request q = grant safety = always (p -> eventually q)";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(3, symbols.size());
        
        // Check first symbol
        assertEquals("p", symbols.get(0).getName());
        assertEquals(SymbolKind.Variable, symbols.get(0).getKind());
        assertNull(symbols.get(0).getChildren());
        
        // Check second symbol
        assertEquals("q", symbols.get(1).getName());
        assertEquals(SymbolKind.Variable, symbols.get(1).getKind());
        
        // Check third symbol
        assertEquals("safety", symbols.get(2).getName());
        assertEquals(SymbolKind.Variable, symbols.get(2).getKind());
    }

    @Test
    void testExternalDeclaration() {
        String input = "external *= always p;";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("external", symbols.get(0).getName());
        assertEquals("external", symbols.get(0).getDetail());
    }

    @Test
    void testLetExpression() {
        String input = "formula = let x = true, y = false in x || y";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("formula", symbols.get(0).getName());
        
        // Check children (let bindings)
        List<DocumentSymbol> children = symbols.get(0).getChildren();
        assertNotNull(children);
        assertEquals(2, children.size());
        
        assertEquals("x", children.get(0).getName());
        assertEquals(SymbolKind.Variable, children.get(0).getKind());
        
        assertEquals("y", children.get(1).getName());
        assertEquals(SymbolKind.Variable, children.get(1).getKind());
    }

    @Test
    void testAutomaton() {
        String input = "automaton = states s0, s1; initial s0; accept s1; s0 [true] s1; s1 [true] s0";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("automaton", symbols.get(0).getName());
        
        // Check children (transitions only - states don't have positions in the AST)
        List<DocumentSymbol> children = symbols.get(0).getChildren();
        assertNotNull(children, "Automaton should have children (transitions)");
        
        // Should have 2 transitions
        assertEquals(2, children.size(), "Should have 2 transitions");
        
        // All children should be transitions (Method kind)
        for (DocumentSymbol child : children) {
            assertEquals(SymbolKind.Method, child.getKind(), "All automaton children should be transitions");
            // Transition names should be in format "source → target"
            assertTrue(child.getName().contains("→"));
        }
    }

    @Test
    void testAutomatonWithPriority() {
        String input = "automaton = states s0, s1; initial s0; accept s1; s0 5 [true] s1";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        
        List<DocumentSymbol> children = symbols.get(0).getChildren();
        assertNotNull(children);
        
        // Find the transition with priority
        DocumentSymbol transition = children.stream()
            .filter(s -> s.getKind() == SymbolKind.Method)
            .findFirst()
            .orElseThrow();
        
        assertTrue(transition.getName().contains("s0"));
        assertTrue(transition.getName().contains("s1"));
        assertEquals("priority 5", transition.getDetail());
    }

    @Test
    void testComplexNesting() {
        String input = "p = let a = true in a q = let b = false, c = true in b && c";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(2, symbols.size());
        
        // First declaration has 1 let binding
        assertEquals(1, symbols.get(0).getChildren().size());
        assertEquals("a", symbols.get(0).getChildren().get(0).getName());
        
                // Second declaration has 2 let bindings
        assertEquals(2, symbols.get(1).getChildren().size());
        assertEquals("b", symbols.get(1).getChildren().get(0).getName());
        assertEquals("c", symbols.get(1).getChildren().get(1).getName());
    }

    @Test
    void testNestedLetExpressions() {
        // Test: nested2 = let outer = p in let inner = outer and q in inner or r
        // Should show: nested2 with children [outer, inner]
        String input = "nested2 = let outer = p in let inner = outer and q in inner or r";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("nested2", symbols.get(0).getName());
        
        // Should have 2 children: outer and "in" (representing the nested context)
        List<DocumentSymbol> children = symbols.get(0).getChildren();
        assertNotNull(children);
        assertEquals(2, children.size(), "nested2 should have 'outer' and 'in' as children");
        
        assertEquals("outer", children.get(0).getName());
        assertEquals("in", children.get(1).getName());
        
        // The "in" should have "inner" as a child
        List<DocumentSymbol> inChildren = children.get(1).getChildren();
        assertNotNull(inChildren);
        assertEquals(1, inChildren.size());
        assertEquals("inner", inChildren.get(0).getName());
    }

    @Test
    void testDeeplyNestedLets() {
        // Test: x = let a = true in let b = a in let c = b in c
        // Should show: x > a, in > b, in > c (hierarchical)
        String input = "x = let a = true in let b = a in let c = b in c";
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("x", symbols.get(0).getName());
        
        // x should have 2 children: a and "in"
        List<DocumentSymbol> xChildren = symbols.get(0).getChildren();
        assertNotNull(xChildren);
        assertEquals(2, xChildren.size());
        assertEquals("a", xChildren.get(0).getName());
        assertEquals("in", xChildren.get(1).getName());
        
        // First "in" should have 2 children: b and another "in"
        List<DocumentSymbol> in1Children = xChildren.get(1).getChildren();
        assertNotNull(in1Children);
        assertEquals(2, in1Children.size());
        assertEquals("b", in1Children.get(0).getName());
        assertEquals("in", in1Children.get(1).getName());
        
        // Second "in" should have 1 child: c
        List<DocumentSymbol> in2Children = in1Children.get(1).getChildren();
        assertNotNull(in2Children);
        assertEquals(1, in2Children.size());
        assertEquals("c", in2Children.get(0).getName());
    }

    @Test
    void testMultilinePipeAtom() {
        // Test that multiline pipe-delimited atoms have proper ranges
        String input = """
                multilinePipe = |this is a
                multiline atom that spans
                several lines|
                """;
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("multilinePipe", symbols.get(0).getName());
        
        // The full range should span multiple lines (from line 0 to line 2)
        var range = symbols.get(0).getRange();
        assertNotNull(range);
        assertTrue(range.getEnd().getLine() > range.getStart().getLine(), 
                   "Multiline atom range should span multiple lines. Start line=" + range.getStart().getLine() + 
                   ", End line=" + range.getEnd().getLine());
        
        // The selection range should be just the name "multilinePipe" on the first line
        var selectionRange = symbols.get(0).getSelectionRange();
        assertEquals(range.getStart().getLine(), selectionRange.getStart().getLine(), 
                     "Selection range should be on the same line as start");
        assertEquals(range.getStart().getCharacter(), selectionRange.getStart().getCharacter(),
                     "Selection range should start at the beginning of the name");
    }

    @Test
    void testMultilineQuoteAtom() {
        // Test that multiline quote-delimited atoms have proper ranges
        String input = """
                multilineQuote = "this is a
                multiline quoted atom
                spanning multiple lines"
                """;
        
        var parseResult = Reader.parseDeclarationsWithPositions(input);
        assertTrue(parseResult.result() instanceof ParseResult.Success);
        
        var success = (ParseResult.Success<Declarations>) parseResult.result();
        Declarations decls = success.value();
        
        DocumentSymbolVisitor visitor = new DocumentSymbolVisitor();
        List<DocumentSymbol> symbols = decls.accept(visitor, parseResult.positionMap());
        
        assertEquals(1, symbols.size());
        assertEquals("multilineQuote", symbols.get(0).getName());
        
        // The full range should span multiple lines (from line 0 to line 2)
        var range = symbols.get(0).getRange();
        assertNotNull(range);
        assertTrue(range.getEnd().getLine() > range.getStart().getLine(), 
                   "Multiline atom range should span multiple lines. Start line=" + range.getStart().getLine() + 
                   ", End line=" + range.getEnd().getLine());
        
        // The selection range should be just the name "multilineQuote" on the first line
        var selectionRange = symbols.get(0).getSelectionRange();
        assertEquals(range.getStart().getLine(), selectionRange.getStart().getLine(), 
                     "Selection range should be on the same line as start");
        assertEquals(range.getStart().getCharacter(), selectionRange.getStart().getCharacter(),
                     "Selection range should start at the beginning of the name");
    }
}

