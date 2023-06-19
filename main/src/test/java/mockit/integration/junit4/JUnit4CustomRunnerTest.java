package mockit.integration.junit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;

import mockit.Injectable;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

@RunWith(JUnit4CustomRunnerTest.CustomRunner.class)
public final class JUnit4CustomRunnerTest {
    public static final class CustomRunner extends BlockJUnit4ClassRunner {
        public CustomRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        @Override
        protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic,
                List<Throwable> errors) {
        }

        @Override
        protected Object createTest() throws Exception {
            testCount++;
            return super.createTest();
        }
    }

    static int testCount;

    @Before
    public void setUp() {
        assertTrue("Unexpected test count: " + testCount, testCount == 1 || testCount == 2);
    }

    @Test
    public void withAnnotatedParameters(@Mocked Runnable runnable, @Injectable Dependency dep) {
        assertNotNull(runnable);
        assertNotNull(dep);
    }

    @Test
    public void withNonAnnotatedParameters(Runnable runnable, Dependency dep) {
        assertNull(runnable);
        assertNull(dep);
    }
}
