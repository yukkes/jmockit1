package mockit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.junit.Test;

/**
 * The Class TestedClassWithFullMixedFieldDITest.
 */
public final class TestedClassWithFullMixedFieldDITest {

    /**
     * The Class TestedClass.
     */
    static class TestedClass {

        /** The dependency. */
        @Inject
        Dependency dependency;

        /** The text. */
        StringBuilder text;
    }

    /**
     * The Class Dependency.
     */
    static class Dependency {
        /** The value. */
        String value;
    }

    /**
     * The Class Dependency2.
     */
    static class Dependency2 {
    }

    /**
     * Verify that fields from JRE types are not initialized.
     *
     * @param tested
     *            the tested
     */
    @Test
    public void verifyThatFieldsFromJRETypesAreNotInitialized(@Tested(fullyInitialized = true) TestedClass tested) {
        assertNull(tested.text);
        assertNull(tested.dependency.value);
    }

    /**
     * The Class TestedClass2.
     */
    static class TestedClass2 {

        /** The dependency 1. */
        @Inject
        Dependency dependency1;

        /** The dependency 2. */
        Dependency2 dependency2;
    }

    /**
     * Verify that fields of user types are initialized even only some are annotated.
     *
     * @param tested
     *            the tested
     */
    @Test
    public void verifyThatFieldsOfUserTypesAreInitializedEvenOnlySomeAreAnnotated(
            @Tested(fullyInitialized = true) TestedClass2 tested) {
        assertNotNull(tested.dependency1);
        assertNotNull(tested.dependency2);
    }
}
