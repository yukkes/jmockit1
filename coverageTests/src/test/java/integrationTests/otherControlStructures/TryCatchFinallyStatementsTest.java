package integrationTests.otherControlStructures;

import org.junit.Test;

import integrationTests.CoverageTest;

public final class TryCatchFinallyStatementsTest extends CoverageTest {
    TryCatchFinallyStatements tested;

    @Test
    public void tryCatch() {
        tested.tryCatch();
    }

    @Test
    public void tryCatchWhichThrowsAndCatchesException() {
        tested.tryCatchWhichThrowsAndCatchesException();
    }
}
