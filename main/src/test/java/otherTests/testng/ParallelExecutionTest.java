package otherTests.testng;

import static org.testng.Assert.*;

import java.util.concurrent.atomic.*;

import org.testng.annotations.*;

// Just to make sure no NPEs or other exceptions occur from JMockit-TestNG integration.
public final class ParallelExecutionTest {
    final AtomicInteger counter = new AtomicInteger();

    @Test(threadPoolSize = 4, invocationCount = 10)
    public void parallelExecution() {
        counter.incrementAndGet();
    }

    @AfterClass
    public void checkCounter() {
        assertEquals(counter.get(), 10);
    }
}
