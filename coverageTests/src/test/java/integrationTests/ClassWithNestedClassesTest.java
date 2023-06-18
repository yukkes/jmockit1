package integrationTests;

import static org.junit.Assert.*;

import org.junit.*;

public final class ClassWithNestedClassesTest extends CoverageTest {
    final ClassWithNestedClasses tested = null;

    @Test
    public void exerciseNestedClasses() {
        ClassWithNestedClasses.doSomething();
        ClassWithNestedClasses.methodContainingAnonymousClass(1);

        assertEquals(13, fileData.lineCoverageInfo.getExecutableLineCount());
        assertEquals(73, fileData.lineCoverageInfo.getCoveragePercentage());
        assertEquals(15, fileData.lineCoverageInfo.getTotalItems());
        assertEquals(11, fileData.lineCoverageInfo.getCoveredItems());
    }
}
