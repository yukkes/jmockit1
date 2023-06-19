package integrationTests;

import org.junit.Test;

public final class ClassWithNestedEnumTest {
    @Test
    public void useNestedEnumFromNestedClass() {
        ClassWithNestedEnum.NestedClass.useEnumFromOuterClass();
    }
}
