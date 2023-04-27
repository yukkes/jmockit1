package mockit.integration.junit4;

import mockit.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({ MockDependencyTest.class, UseDependencyTest.class })
public final class TestSuiteWithBeforeClass {
    @BeforeClass
    public static void setUpSuiteWideFakes() {
        new MockUp<AnotherDependency>() {
            @Mock
            boolean alwaysTrue() {
                return false;
            }
        };

        AnotherDependency.mockedAtSuiteLevel = true;
    }
}
