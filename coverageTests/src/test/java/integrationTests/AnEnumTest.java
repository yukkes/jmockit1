package integrationTests;

import static org.junit.Assert.*;

import org.junit.*;

public final class AnEnumTest extends CoverageTest {
    AnEnum tested;

    @Test
    public void useAnEnum() {
        tested = AnEnum.OneValue;

        assertEquals(50, fileData.lineCoverageInfo.getCoveragePercentage());
    }
}
