package gpsl.syntax.dimacs_cnf;

import gpsl.syntax.model.Expression;
import gpsl.syntax.model.Factory;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTest {

    private final Reader reader = new Reader(Factory.instance());

    @Test
    void readsValidDimacsFile() throws Exception {
        String dimacs = """
                c sample problem
                p cnf 3 2
                1 -2 0
                -1 3 0
                """;
        var expression = reader.read(new StringReader(dimacs));

        assertInstanceOf(Expression.class, expression);
    }

    @Test
    void failsWhenHeaderIsMissing() {
        String dimacs = "1 -2 0";

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> reader.read(new StringReader(dimacs)));
        assertTrue(ex.getMessage().contains("header"));
    }

    @Test
    void failsOnMalformedHeader() {
        String dimacs = "p dnf 3 1\n1 0";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reader.read(new StringReader(dimacs)));
        assertTrue(ex.getMessage().toLowerCase().contains("invalid dimacs header"));
    }

    @Test
    void failsWhenClauseMissingTerminator() {
        String dimacs = "p cnf 2 1\n1 2";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reader.read(new StringReader(dimacs)));
        assertTrue(ex.getMessage().contains("Clause not terminated"));
    }

    @Test
    void failsWhenUndeclaredVariableUsed() {
        String dimacs = "p cnf 1 1\n2 0";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reader.read(new StringReader(dimacs)));
        assertTrue(ex.getMessage().contains("undeclared variable"));
    }
}
