/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.util.ClassLoad;

/**
 * Holds state associated with mock class containing {@linkplain mockit.Mock annotated mocks}.
 */
public final class FakeStates {
    private static final Pattern SPACE = Pattern.compile(" ");

    /**
     * For each mockup instance and each {@code @Mock} method containing the {@code Invocation} parameter or an
     * invocation count constraint, a runtime state will be kept here.
     */
    @Nonnull
    private final Map<Object, List<FakeState>> mockUpsToFakeStates;
    @Nonnull
    private final Map<Object, List<FakeState>> startupFakesToFakeStates;

    public FakeStates() {
        startupFakesToFakeStates = new IdentityHashMap<>(2);
        mockUpsToFakeStates = new IdentityHashMap<>(8);
    }

    void addStartupFakeAndItsFakeStates(@Nonnull Object mockUp, @Nonnull List<FakeState> fakeStates) {
        startupFakesToFakeStates.put(mockUp, fakeStates);
    }

    void addFakeAndItsFakeStates(@Nonnull Object mockUp, @Nonnull List<FakeState> fakeStates) {
        mockUpsToFakeStates.put(mockUp, fakeStates);
    }

    public void copyFakeStates(@Nonnull Object previousFake, @Nonnull Object newFake) {
        List<FakeState> fakeStates = mockUpsToFakeStates.get(previousFake);

        if (fakeStates != null) {
            List<FakeState> copiedFakeStates = new ArrayList<>(fakeStates.size());

            for (FakeState fakeState : fakeStates) {
                copiedFakeStates.add(new FakeState(fakeState));
            }

            mockUpsToFakeStates.put(newFake, copiedFakeStates);
        }
    }

    public void removeClassState(@Nonnull Class<?> redefinedClass,
            @Nullable String internalNameForOneOrMoreMockClasses) {
        removeFakeStates(redefinedClass);

        if (internalNameForOneOrMoreMockClasses != null) {
            if (internalNameForOneOrMoreMockClasses.indexOf(' ') < 0) {
                removeFakeStates(internalNameForOneOrMoreMockClasses);
            } else {
                String[] mockClassesInternalNames = SPACE.split(internalNameForOneOrMoreMockClasses);

                for (String mockClassInternalName : mockClassesInternalNames) {
                    removeFakeStates(mockClassInternalName);
                }
            }
        }
    }

    private void removeFakeStates(@Nonnull Class<?> redefinedClass) {
        Iterator<List<FakeState>> itr = mockUpsToFakeStates.values().iterator();

        while (itr.hasNext()) {
            List<FakeState> fakeStates = itr.next();
            FakeState fakeState = fakeStates.get(0);

            if (fakeState.getRealClass() == redefinedClass) {
                fakeStates.clear();
                itr.remove();
            }
        }
    }

    private void removeFakeStates(@Nonnull String mockClassInternalName) {
        Class<?> mockUpClass = ClassLoad.loadClass(mockClassInternalName.replace('/', '.'));
        Iterator<Entry<Object, List<FakeState>>> itr = mockUpsToFakeStates.entrySet().iterator();

        while (itr.hasNext()) {
            Entry<Object, List<FakeState>> mockUpAndFakeStates = itr.next();
            Object mockUp = mockUpAndFakeStates.getKey();

            if (mockUp.getClass() == mockUpClass) {
                itr.remove();
            }
        }
    }

    public boolean updateFakeState(@Nonnull Object mockUp, int fakeStateIndex) {
        FakeState fakeState = getFakeState(mockUp, fakeStateIndex);
        return fakeState.update();
    }

    @Nonnull
    FakeState getFakeState(@Nonnull Object mockUp, int fakeStateIndex) {
        List<FakeState> fakeStates = startupFakesToFakeStates.get(mockUp);

        if (fakeStates == null) {
            fakeStates = mockUpsToFakeStates.get(mockUp);
        }

        FakeState fakeState = fakeStates.get(fakeStateIndex);
        assert fakeState != null;
        return fakeState;
    }
}
