package mockit;

import static org.junit.Assert.assertEquals;

import java.applet.Applet;
import java.awt.Component;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The Class MisusedFakingAPITest.
 */
public final class MisusedFakingAPITest {

    /** The thrown. */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * Fake same method twice with reentrant fakes from two different fake classes.
     */
    @Test
    public void fakeSameMethodTwiceWithReentrantFakesFromTwoDifferentFakeClasses() {
        new MockUp<Applet>() {
            @Mock
            int getComponentCount(Invocation inv) {
                int i = inv.proceed();
                return i + 1;
            }
        };

        int i = new Applet().getComponentCount();
        assertEquals(1, i);

        new MockUp<Applet>() {
            @Mock
            int getComponentCount(Invocation inv) {
                int j = inv.proceed();
                return j + 2;
            }
        };

        // Should return 3, but returns 5. Chaining mock methods is not supported.
        int j = new Applet().getComponentCount();
        assertEquals(5, j);
    }

    /**
     * The Class AppletFake.
     */
    static final class AppletFake extends MockUp<Applet> {

        /** The component count. */
        final int componentCount;

        /**
         * Instantiates a new applet fake.
         *
         * @param componentCount
         *            the component count
         */
        AppletFake(int componentCount) {
            this.componentCount = componentCount;
        }

        /**
         * Gets the component count.
         *
         * @param inv
         *            the inv
         *
         * @return the component count
         */
        @Mock
        int getComponentCount(Invocation inv) {
            return componentCount;
        }
    }

    /**
     * Apply the same fake for A class twice.
     */
    @Test
    public void applyTheSameFakeForAClassTwice() {
        new AppletFake(1);
        new AppletFake(2); // second application overrides the previous one

        assertEquals(2, new Applet().getComponentCount());
    }

    /**
     * Fake A private method.
     */
    @Test
    public void fakeAPrivateMethod() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unsupported fake for private method");
        thrown.expectMessage("Component");
        thrown.expectMessage("checkCoalescing()");
        thrown.expectMessage("found");

        new MockUp<Component>() {
            @Mock
            boolean checkCoalescing() {
                return false;
            }
        };
    }

    /**
     * Fake A private constructor.
     */
    @Test
    public void fakeAPrivateConstructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unsupported fake for private constructor");
        thrown.expectMessage("System#<init>()");
        thrown.expectMessage("found");

        new MockUp<System>() {
            @Mock
            void $init() {
            }
        };
    }
}
