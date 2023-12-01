/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import mockit.MockUp;
import mockit.internal.util.ClassLoad;

public final class FakeClasses {
    private static final Method ON_TEAR_DOWN_METHOD;
    static {
        try {
            ON_TEAR_DOWN_METHOD = MockUp.class.getDeclaredMethod("onTearDown");
            ON_TEAR_DOWN_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void notifyOfTearDown(@Nonnull MockUp<?> fake) {
        try {
            ON_TEAR_DOWN_METHOD.invoke(fake);
        } catch (IllegalAccessException ignore) {
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
    }

    @Nonnull
    private final Map<String, MockUp<?>> startupFakes;
    @Nonnull
    private final Map<Class<?>, MockUp<?>> fakeClassesToFakeInstances;
    @Nonnull
    private final Map<Object, MockUp<?>> fakedToFakeInstances;
    @Nonnull
    public final FakeStates fakeStates;

    public FakeClasses() {
        startupFakes = new IdentityHashMap<>(8);
        fakeClassesToFakeInstances = new IdentityHashMap<>();
        fakedToFakeInstances = new IdentityHashMap<>();
        fakeStates = new FakeStates();
    }

    void addFake(@Nonnull String fakeClassDesc, @Nonnull MockUp<?> fake) {
        startupFakes.put(fakeClassDesc, fake);
    }

    void addFake(@Nonnull MockUp<?> fake) {
        Class<?> fakeClass = fake.getClass();
        fakeClassesToFakeInstances.put(fakeClass, fake);
    }

    public void addFake(@Nonnull MockUp<?> fake, @Nonnull Object fakedInstance) {
        MockUp<?> previousFake = fakedToFakeInstances.put(fakedInstance, fake);
        assert previousFake == null;
    }

    @Nonnull
    public MockUp<?> getFake(@Nonnull String fakeClassDesc) {
        MockUp<?> startupFake = startupFakes.get(fakeClassDesc);

        if (startupFake != null) {
            return startupFake;
        }

        Class<?> fakeClass = ClassLoad.loadByInternalName(fakeClassDesc);
        return fakeClassesToFakeInstances.get(fakeClass);
    }

    public void discardStartupFakes() {
        for (MockUp<?> startupFake : startupFakes.values()) {
            notifyOfTearDown(startupFake);
        }
    }

    private void discardFakeInstances(@Nonnull Map<Object, MockUp<?>> previousFakeInstances) {
        if (!previousFakeInstances.isEmpty()) {
            fakedToFakeInstances.entrySet().retainAll(previousFakeInstances.entrySet());
        } else if (!fakedToFakeInstances.isEmpty()) {
            fakedToFakeInstances.clear();
        }
    }

    public final class SavePoint {
        @Nonnull
        private final Map<Object, MockUp<?>> previousFakeInstances;
        @Nonnull
        private final Map<Class<?>, Boolean> previousFakeClasses;

        public SavePoint() {
            previousFakeInstances = new IdentityHashMap<>(fakedToFakeInstances);
            previousFakeClasses = new IdentityHashMap<>();

            for (Entry<Class<?>, MockUp<?>> fakeClassAndInstance : fakeClassesToFakeInstances.entrySet()) {
                Class<?> fakeClass = fakeClassAndInstance.getKey();
                previousFakeClasses.put(fakeClass, false);
            }
        }

        public void rollback() {
            discardFakeInstances(previousFakeInstances);

            if (previousFakeClasses.isEmpty()) {
                discardAllFakeInstances();
            } else {
                discardFakeInstancesExceptPreviousOnes();
            }
        }

        private void discardAllFakeInstances() {
            if (!fakeClassesToFakeInstances.isEmpty()) {
                for (MockUp<?> fakeInstance : fakeClassesToFakeInstances.values()) {
                    notifyOfTearDown(fakeInstance);
                }

                fakeClassesToFakeInstances.clear();
            }
        }

        private void discardFakeInstancesExceptPreviousOnes() {
            for (Entry<Class<?>, MockUp<?>> fakeClassAndInstances : fakeClassesToFakeInstances.entrySet()) {
                Class<?> fakeClass = fakeClassAndInstances.getKey();

                if (!previousFakeClasses.containsKey(fakeClass)) {
                    MockUp<?> fakeInstance = fakeClassAndInstances.getValue();
                    notifyOfTearDown(fakeInstance);
                }
            }

            fakeClassesToFakeInstances.keySet().retainAll(previousFakeClasses.keySet());
        }
    }
}
