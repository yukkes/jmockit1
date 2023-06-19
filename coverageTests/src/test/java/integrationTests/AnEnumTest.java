package integrationTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class AnEnumTest extends CoverageTest {
    AnEnum tested;

    @Test
    public void useAnEnum() {
        tested = AnEnum.OneValue;

        assertEquals(100, fileData.lineCoverageInfo.getCoveragePercentage());
    }
}
