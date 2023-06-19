package integrationTests;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.ConstructorProperties;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;

import org.junit.Test;

public final class MiscellaneousTest {
    @Test
    public void methodWithIINCWideInstruction() {
        int i = 0;
        i += 1000; // compiled to opcode iinc_w
        assert i == 1000;
    }

    @Retention(RUNTIME)
    public @interface Dummy {
        Class<?> value();
    }

    @Dummy(String.class)
    static class AnnotatedClass {
    }

    @Test
    public void havingAnnotationWithClassValue(@Injectable AnnotatedClass dummy) {
        assertNotNull(dummy);
    }

    @Test
    public void verifyAnnotationsArePreserved() throws Exception {
        Constructor<ClassWithAnnotations> constructor = ClassWithAnnotations.class.getDeclaredConstructor();

        assertTrue(constructor.isAnnotationPresent(ConstructorProperties.class));
    }

    @Test
    public void mockingAnAnnotation(@Tested @Mocked AnAnnotation mockedAnnotation) {
        assertNull(mockedAnnotation.value());
    }
}
