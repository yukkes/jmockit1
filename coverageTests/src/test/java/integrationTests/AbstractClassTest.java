package integrationTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public final class AbstractClassTest extends CoverageTest {
    AbstractClassWithNoExecutableLines tested;

    @Before
    public void setUp() {
        tested = new AbstractClassWithNoExecutableLines() {
            @Override
            void doSomething(String s, boolean b) {
            }

            @Override
            int returnValue() {
                return 0;
            }
        };
    }

    @Test
    public void useAbstractClass() {
        tested.doSomething("test", true);
        tested.returnValue();

        assertEquals(1, fileData.lineCoverageInfo.getExecutableLineCount());
        assertLines(6, 6, 1);
        assertEquals(100, fileData.lineCoverageInfo.getCoveragePercentage());
    }
}
