package otherTests.testng;

import static org.testng.Assert.*;

import mockit.*;

import org.testng.annotations.*;

public final class TestNGCascadingInSetupTest {
    static class Foo {
        Bar getBar() {
            return null;
        }
    }

    static class Bar {
        String getValue() {
            return null;
        }
    }

    @Mocked
    Foo foo;

    @BeforeMethod
    public void recordValueForCascadedInstance() {
        new Expectations() {
            {
                foo.getBar().getValue();
                result = "test";
            }
        };
    }

    @Test
    public void useExpectationResultRecordedOnCascadedInstanceFromSetupMethod() {
        String value = foo.getBar().getValue();

        assertNotNull(value);
    }
}
