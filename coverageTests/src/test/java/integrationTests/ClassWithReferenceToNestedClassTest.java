package integrationTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class ClassWithReferenceToNestedClassTest extends CoverageTest {
    final ClassWithReferenceToNestedClass tested = null;

    @Test
    public void exerciseOnePathOfTwo() {
        ClassWithReferenceToNestedClass.doSomething();

        assertEquals(4, fileData.lineCoverageInfo.getExecutableLineCount());
        assertEquals(25, fileData.lineCoverageInfo.getCoveragePercentage());
        assertEquals(4, fileData.lineCoverageInfo.getTotalItems());
        assertEquals(1, fileData.lineCoverageInfo.getCoveredItems());
    }
}
