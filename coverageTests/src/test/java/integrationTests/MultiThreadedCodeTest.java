package integrationTests;

import org.junit.*;

public final class MultiThreadedCodeTest extends CoverageTest {
    MultiThreadedCode tested;

    @Test
    public void nonBlockingOperation() throws Exception {
        Thread worker = MultiThreadedCode.nonBlockingOperation();
        worker.join();

        assertLines(14, 19, 4);
        assertLine(14, 1, 1, 1);
        assertLine(18, 1, 1, 1);
        assertLine(19, 1, 1, 1);
    }
}
