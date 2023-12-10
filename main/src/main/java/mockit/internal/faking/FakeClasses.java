/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.MockUp;
import mockit.internal.util.ClassLoad;

public final class FakeClasses {
    private static final Field INVOKED_INSTANCE_FIELD;
    private static final Method ON_TEAR_DOWN_METHOD;

    static {
        try {
            INVOKED_INSTANCE_FIELD = MockUp.class.getDeclaredField("invokedInstance");
            INVOKED_INSTANCE_FIELD.setAccessible(true);

            ON_TEAR_DOWN_METHOD = MockUp.class.getDeclaredMethod("onTearDown");
            ON_TEAR_DOWN_METHOD.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void notifyOfTearDown(@Nonnull MockUp<?> mockUp) {
        try {
            ON_TEAR_DOWN_METHOD.invoke(mockUp);
        } catch (IllegalAccessException ignore) {
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
    }

    public static final class MockUpInstances {
        @Nonnull
        public final MockUp<?> initialMockUp;
        boolean hasMockupsForSingleInstances;

        MockUpInstances(@Nonnull MockUp<?> initialMockUp) {
            this.initialMockUp = initialMockUp;
            hasMockupsForSingleInstances = false;
        }

        public boolean hasMockUpsForSingleInstances() {
            return hasMockupsForSingleInstances;
        }

        void notifyMockUpOfTearDown() {
            notifyOfTearDown(initialMockUp);
        }
    }

    @Nonnull
    private final Map<String, MockUp<?>> startupMocks;
    @Nonnull
    private final Map<Class<?>, MockUpInstances> mockupClassesToMockupInstances;
    @Nonnull
    private final Map<Object, MockUp<?>> mockedToMockupInstances;
    @Nonnull
    public final FakeStates fakeStates;

    public FakeClasses() {
        startupMocks = new IdentityHashMap<>(8);
        mockupClassesToMockupInstances = new IdentityHashMap<>();
        mockedToMockupInstances = new IdentityHashMap<>();
        fakeStates = new FakeStates();
    }

    public void addFake(@Nonnull String mockClassDesc, @Nonnull MockUp<?> mockUp) {
        startupMocks.put(mockClassDesc, mockUp);
    }

    public void addFake(@Nonnull MockUp<?> mockUp) {
        Class<?> mockUpClass = mockUp.getClass();
        MockUpInstances newData = new MockUpInstances(mockUp);
        mockupClassesToMockupInstances.put(mockUpClass, newData);
    }

    public void addFake(@Nonnull MockUp<?> mockUp, @Nonnull Object mockedInstance) {
        MockUp<?> previousMockup = mockedToMockupInstances.put(mockedInstance, mockUp);
        assert previousMockup == null;

        MockUpInstances mockUpInstances = mockupClassesToMockupInstances.get(mockUp.getClass());
        mockUpInstances.hasMockupsForSingleInstances = true;
    }

    @Nullable
    public MockUp<?> getFake(@Nonnull String mockUpClassDesc, @Nullable Object mockedInstance) {
        if (mockedInstance != null) {
            MockUp<?> mockUpForSingleInstance = mockedToMockupInstances.get(mockedInstance);

            if (mockUpForSingleInstance != null) {
                return mockUpForSingleInstance;
            }
        }

        MockUp<?> startupMock = startupMocks.get(mockUpClassDesc);

        if (startupMock != null) {
            return startupMock;
        }

        Class<?> mockUpClass = ClassLoad.loadByInternalName(mockUpClassDesc);
        MockUpInstances mockUpInstances = mockupClassesToMockupInstances.get(mockUpClass);
        Object invokedInstance = mockedInstance;

        if (mockedInstance == null) {
            invokedInstance = Void.class;
        } else if (mockUpInstances.hasMockUpsForSingleInstances()) {
            return null;
        }

        try {
            INVOKED_INSTANCE_FIELD.set(mockUpInstances.initialMockUp, invokedInstance);
        } catch (IllegalAccessException ignore) {
        }

        return mockUpInstances.initialMockUp;
    }

    @Nullable
    public MockUpInstances findPreviouslyAppliedMockUps(@Nonnull MockUp<?> newMockUp) {
        Class<?> mockUpClass = newMockUp.getClass();
        MockUpInstances mockUpInstances = mockupClassesToMockupInstances.get(mockUpClass);

        if (mockUpInstances != null && mockUpInstances.hasMockupsForSingleInstances) {
            fakeStates.copyFakeStates(mockUpInstances.initialMockUp, newMockUp);
        }

        return mockUpInstances;
    }

    private void discardMockupInstances(@Nonnull Map<Object, MockUp<?>> previousMockInstances) {
        if (!previousMockInstances.isEmpty()) {
            mockedToMockupInstances.entrySet().retainAll(previousMockInstances.entrySet());
        } else if (!mockedToMockupInstances.isEmpty()) {
            mockedToMockupInstances.clear();
        }
    }

    private void discardMockupInstancesExceptPreviousOnes(@Nonnull Map<Class<?>, Boolean> previousMockupClasses) {
        updatePreviousMockups(previousMockupClasses);

        for (Entry<Class<?>, MockUpInstances> mockupClassAndInstances : mockupClassesToMockupInstances.entrySet()) {
            Class<?> mockupClass = mockupClassAndInstances.getKey();

            if (!previousMockupClasses.containsKey(mockupClass)) {
                MockUpInstances mockUpInstances = mockupClassAndInstances.getValue();
                mockUpInstances.notifyMockUpOfTearDown();
            }
        }

        mockupClassesToMockupInstances.keySet().retainAll(previousMockupClasses.keySet());
    }

    private void updatePreviousMockups(@Nonnull Map<Class<?>, Boolean> previousMockupClasses) {
        for (Entry<Class<?>, Boolean> mockupClassAndData : previousMockupClasses.entrySet()) {
            Class<?> mockupClass = mockupClassAndData.getKey();
            MockUpInstances mockUpData = mockupClassesToMockupInstances.get(mockupClass);
            mockUpData.hasMockupsForSingleInstances = mockupClassAndData.getValue();
        }
    }

    private void discardAllMockupInstances() {
        if (!mockupClassesToMockupInstances.isEmpty()) {
            for (MockUpInstances mockUpInstances : mockupClassesToMockupInstances.values()) {
                mockUpInstances.notifyMockUpOfTearDown();
            }

            mockupClassesToMockupInstances.clear();
        }
    }

    public void discardStartupFakes() {
        for (MockUp<?> startupMockup : startupMocks.values()) {
            notifyOfTearDown(startupMockup);
        }
    }

    public final class SavePoint {
        @Nonnull
        private final Map<Object, MockUp<?>> previousMockInstances;
        @Nonnull
        private final Map<Class<?>, Boolean> previousMockupClasses;

        public SavePoint() {
            previousMockInstances = new IdentityHashMap<>(mockedToMockupInstances);
            previousMockupClasses = new IdentityHashMap<>();

            for (Entry<Class<?>, MockUpInstances> mockUpClassAndData : mockupClassesToMockupInstances.entrySet()) {
                Class<?> mockUpClass = mockUpClassAndData.getKey();
                MockUpInstances mockUpData = mockUpClassAndData.getValue();
                previousMockupClasses.put(mockUpClass, mockUpData.hasMockupsForSingleInstances);
            }
        }

        public void rollback() {
            discardMockupInstances(previousMockInstances);

            if (!previousMockupClasses.isEmpty()) {
                discardMockupInstancesExceptPreviousOnes(previousMockupClasses);
            } else {
                discardAllMockupInstances();
            }
        }
    }
}
