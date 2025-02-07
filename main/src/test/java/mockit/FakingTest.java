package mockit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Panel;
import java.lang.reflect.Constructor;
import java.rmi.RMISecurityException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import javax.accessibility.AccessibleContext;
import javax.sound.midi.Patch;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicColorChooserUI;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The Class FakingTest.
 */
public final class FakingTest {

    /** The thrown. */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * Attempt to apply fake without the target type.
     */
    @Test
    public void attemptToApplyFakeWithoutTheTargetType() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No target type");

        new MockUp() {
        };
    }

    // Fakes for classes
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fake A class.
     */
    @Test
    public void fakeAClass() {
        new MockUp<Panel>() {
            @Mock
            int getComponentCount() {
                return 123;
            }
        };

        assertEquals(123, new Panel().getComponentCount());
    }

    /**
     * The Class Main.
     */
    static final class Main {

        /** The Constant atomicCount. */
        static final AtomicIntegerFieldUpdater<Main> atomicCount = AtomicIntegerFieldUpdater.newUpdater(Main.class,
                "count");

        /** The count. */
        volatile int count;

        /** The max. */
        int max = 2;

        /**
         * Increment.
         *
         * @return true, if successful
         */
        boolean increment() {
            while (true) {
                int currentCount = count;

                if (currentCount >= max) {
                    return false;
                }

                if (atomicCount.compareAndSet(this, currentCount, currentCount + 1)) {
                    return true;
                }
            }
        }
    }

    /**
     * Fake A given class.
     */
    @Test
    public void fakeAGivenClass() {
        final Main main = new Main();

        new MockUp<AtomicIntegerFieldUpdater<?>>(Main.atomicCount.getClass()) {
            boolean second;

            @Mock
            public boolean compareAndSet(Object obj, int expect, int update) {
                assertSame(main, obj);
                assertEquals(0, expect);
                assertEquals(1, update);

                if (second) {
                    return true;
                }

                second = true;
                return false;
            }
        };

        assertTrue(main.increment());
    }

    /**
     * Attempt to fake given class but pass null.
     */
    @Test
    public void attemptToFakeGivenClassButPassNull() {
        thrown.expect(NullPointerException.class);

        new MockUp<Panel>(null) {
        };
    }

    /**
     * The Class FakeForGivenClass.
     */
    @SuppressWarnings("rawtypes")
    static class FakeForGivenClass extends MockUp {

        /**
         * Instantiates a new fake for given class.
         */
        @SuppressWarnings("unchecked")
        FakeForGivenClass() {
            super(Panel.class);
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        @Mock
        String getName() {
            return "mock";
        }
    }

    /**
     * Fake given class using named fake.
     */
    @Test
    public void fakeGivenClassUsingNamedFake() {
        new FakeForGivenClass();

        String s = new Panel().getName();

        assertEquals("mock", s);
    }

    // Fakes for other situations
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Attempt to fake class and interface at once.
     *
     * @param <M>
     *            the generic type
     */
    @Test
    public <M extends Panel & Runnable> void attemptToFakeClassAndInterfaceAtOnce() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Unable to capture");

        new MockUp<M>() {
            @Mock
            String getName() {
                return "";
            }

            @Mock
            void run() {
            }
        };
    }

    /**
     * Fake using invocation parameters.
     */
    @Test
    public void fakeUsingInvocationParameters() {
        new MockUp<Panel>() {
            @Mock
            void $init(Invocation inv) {
                Panel it = inv.getInvokedInstance();
                assertNotNull(it);
            }

            @Mock
            int getBaseline(Invocation inv, int w, int h) {
                return inv.proceed();
            }
        };

        int i = new Panel().getBaseline(20, 15);

        assertEquals(-1, i);
    }

    /**
     * The Class PublicNamedFakeWithNoInvocationParameters.
     */
    public static class PublicNamedFakeWithNoInvocationParameters extends MockUp<Panel> {

        /** The executed. */
        boolean executed;

        /**
         * $init.
         */
        @Mock
        public void $init() {
            executed = true;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        @Mock
        public String getName() {
            return "test";
        }
    }

    /**
     * Public named fake with no invocation parameter.
     */
    @Test
    public void publicNamedFakeWithNoInvocationParameter() {
        PublicNamedFakeWithNoInvocationParameters fake = new PublicNamedFakeWithNoInvocationParameters();

        Panel applet = new Panel();
        assertTrue(fake.executed);

        String name = applet.getName();
        assertEquals("test", name);
    }

    /**
     * Faking of annotated class.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @SuppressWarnings("deprecation")
    public void fakingOfAnnotatedClass() throws Exception {
        new MockUp<RMISecurityException>() {
            @Mock
            void $init(String s) {
                assertNotNull(s);
            }
        };

        assertTrue(RMISecurityException.class.isAnnotationPresent(Deprecated.class));

        Constructor<RMISecurityException> aConstructor = RMISecurityException.class
                .getDeclaredConstructor(String.class);
        assertTrue(aConstructor.isAnnotationPresent(Deprecated.class));

        Deprecated deprecated = aConstructor.getAnnotation(Deprecated.class);
        assertNotNull(deprecated);
    }

    /**
     * Fake same class twice using separate fakes.
     */
    @Test
    public void fakeSameClassTwiceUsingSeparateFakes() {
        Panel a = new Panel();

        class Fake1 extends MockUp<Panel> {
            @Mock
            void addNotify() {
            }
        }
        new Fake1();
        a.addNotify();

        new MockUp<Panel>() {
            @Mock
            AccessibleContext getAccessibleContext() {
                return null;
            }
        };
        a.addNotify(); // still faked
        a.getAccessibleContext();
    }

    /**
     * Fake constructor of inner class.
     */
    @Test
    public void fakeConstructorOfInnerClass() {
        final BasicColorChooserUI outer = new BasicColorChooserUI();
        final boolean[] constructed = { false };

        new MockUp<BasicColorChooserUI.PropertyHandler>() {
            @Mock
            void $init(BasicColorChooserUI o) {
                assertSame(outer, o);
                constructed[0] = true;
            }
        };

        outer.new PropertyHandler();
        assertTrue(constructed[0]);
    }

    /**
     * Call fake method from AWT event dispatching thread.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void callFakeMethodFromAWTEventDispatchingThread() throws Exception {
        new MockUp<Panel>() {
            @Mock
            int getComponentCount() {
                return 10;
            }
        };

        SwingUtilities.invokeAndWait(() -> {
            int i = new Panel().getComponentCount();
            assertEquals(10, i);
        });
    }

    /**
     * The Class JRESubclass.
     */
    static final class JRESubclass extends Patch {
        /**
         * Instantiates a new JRE subclass.
         *
         * @param i
         *            the i
         * @param j
         *            the j
         */
        JRESubclass(int i, int j) {
            super(i, j);
        }
    }

    /**
     * Anonymous fake for JRE subclass having fake method for JRE method.
     */
    @Test
    public void anonymousFakeForJRESubclassHavingFakeMethodForJREMethod() {
        new MockUp<JRESubclass>() {
            @Mock
            int getBank() {
                return 123;
            }
        };

        Patch t = new JRESubclass(1, 2);
        int i = t.getBank();

        assertEquals(123, i);
    }

    /** The fake torn down. */
    static Boolean fakeTornDown;

    /**
     * The Class FakeWithActionOnTearDown.
     */
    static final class FakeWithActionOnTearDown extends MockUp<Panel> {
        @Override
        protected void onTearDown() {
            fakeTornDown = true;
        }
    }

    /**
     * Perform action on fake tear down.
     */
    @Test
    public void performActionOnFakeTearDown() {
        fakeTornDown = false;
        new FakeWithActionOnTearDown();
        assertFalse(fakeTornDown);
    }

    /**
     * Verify fake applied in test was torn down.
     */
    @AfterClass
    public static void verifyFakeAppliedInTestWasTornDown() {
        assertTrue(fakeTornDown == null || fakeTornDown);
    }

    /**
     * Fake varargs method with proceeding fake method which passes replacement arguments.
     */
    @Test
    public void fakeVarargsMethodWithProceedingFakeMethodWhichPassesReplacementArguments() {
        new MockUp<ProcessBuilder>() {
            @Mock
            ProcessBuilder command(Invocation inv, String... command) {
                String[] newArgs = { "replaced" };
                return inv.proceed((Object) newArgs);
            }
        };

        new ProcessBuilder().command("test", "something");
    }
}
