package integrationTests;

import static org.junit.Assert.*;

import org.junit.*;

public final class InterfaceWithExecutableCodeTest extends CoverageTest {
    InterfaceWithExecutableCode tested;

    @Test
    public void exerciseExecutableLineInInterface() {
        assertTrue(InterfaceWithExecutableCode.N > 0);

        assertLines(11, 11, 1);
        assertLine(11, 1, 1, 1);
    }
}
