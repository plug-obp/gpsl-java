package gpsl.syntax.hashcons;

import gpsl.syntax.model.Builder;
import gpsl.syntax.model.Factory;
import gpsl.syntax.model.Reference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NamelessEqualityTest {
    Factory f = Factory.instance();
    Builder b = new Builder(f);
    @Test
    void testNegation() {
        var n1 = f.negation("!",f.t());
        var n2 = f.negation("not", f.t());

        assertNotEquals(n1, n2);

        assertTrue(NamelessEquality.same(n1, n2));
    }

    @Test
    void testDeclarations() {
        var ed1 = f.internal("a", f.t());
        var ed2 = f.internal("b", f.t());
        var ed3 = f.internal("b", f.t());
        var ed4 = f.internal("a", f.t());

        var d1 = f.declarations(ed1, ed2);
        var d2 = f.declarations(ed3, ed4);

        assertNotEquals(d1, d2);
        assertTrue(NamelessEquality.same(d1, d2));

        var d3 = f.declarations(ed3, f.internal("a", f.f()));
        assertNotEquals(d1, d3);
        assertFalse(NamelessEquality.same(d1, d3));
    }

    @Test
    void testReferences() {
        var r1 = (Reference)f.reference("a");
        var r2 = (Reference)f.reference("b");

        assertNotEquals(r1, r2);
        assertFalse(NamelessEquality.same(r1, r2));

        var r3 = f.reference("a");
        assertEquals(r1, r3);
        assertTrue(NamelessEquality.same(r1, r3));

        r1.setExpression(f.t());
        r2.setExpression(f.t());

        assertNotEquals(r1, r2);
        assertTrue(NamelessEquality.same(r1, r2));
    }
}
