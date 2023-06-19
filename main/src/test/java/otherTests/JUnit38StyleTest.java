package otherTests;

import mockit.ClassWithObjectOverrides;

import junit.framework.TestCase;

/**
 * The Class JUnit38StyleTest.
 */
public final class JUnit38StyleTest extends TestCase {
    @Override
    public void setUp() {
        useClassMockedInPreviousJUnit4TestClass();
    }

    /**
     * Test use class mocked in previous J unit 4 test class.
     */
    public void testUseClassMockedInPreviousJUnit4TestClass() {
        useClassMockedInPreviousJUnit4TestClass();
    }

    /**
     * Use class mocked in previous J unit 4 test class.
     */
    void useClassMockedInPreviousJUnit4TestClass() {
        ClassWithObjectOverrides test = new ClassWithObjectOverrides("test");
        // noinspection UseOfObsoleteAssert
        assertEquals("test", test.toString());
    }
}
