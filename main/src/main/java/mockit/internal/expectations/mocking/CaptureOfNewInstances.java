/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.mocking;

import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.asm.classes.ClassReader;
import mockit.asm.types.JavaType;
import mockit.internal.BaseClassModifier;
import mockit.internal.capturing.CaptureOfImplementations;
import mockit.internal.startup.Startup;
import mockit.internal.state.MockFixture;
import mockit.internal.state.TestRun;

public final class CaptureOfNewInstances extends CaptureOfImplementations<MockedType> {
    @Nonnull
    private final List<Class<?>> baseTypes;

    CaptureOfNewInstances() {
        baseTypes = new ArrayList<>();
    }

    @Nonnull
    @Override
    protected BaseClassModifier createModifier(@Nullable ClassLoader cl, @Nonnull ClassReader cr,
            @Nonnull Class<?> baseType, @Nullable MockedType typeMetadata) {
        MockedClassModifier modifier = new MockedClassModifier(cl, cr, typeMetadata);
        String baseTypeDesc = JavaType.getInternalName(baseType);
        modifier.setClassNameForCapturedInstanceMethods(baseTypeDesc);
        return modifier;
    }

    @Override
    protected void redefineClass(@Nonnull Class<?> realClass, @Nonnull byte[] modifiedClass) {
        ClassDefinition newClassDefinition = new ClassDefinition(realClass, modifiedClass);
        Startup.redefineMethods(newClassDefinition);

        MockFixture mockFixture = TestRun.mockFixture();
        mockFixture.addRedefinedClass(newClassDefinition);
        mockFixture.registerMockedClass(realClass);
    }

    void registerCaptureOfNewInstances(@Nonnull MockedType typeMetadata) {
        Class<?> baseType = typeMetadata.getClassType();

        if (!typeMetadata.isFinalFieldOrParameter()) {
            makeSureAllSubtypesAreModified(typeMetadata);
        }

        if (!baseTypes.contains(baseType)) {
            baseTypes.add(baseType);
        }
    }

    void makeSureAllSubtypesAreModified(@Nonnull MockedType typeMetadata) {
        Class<?> baseType = typeMetadata.getClassType();
        makeSureAllSubtypesAreModified(baseType, typeMetadata.fieldFromTestClass, typeMetadata);
    }

    public boolean captureNewInstance(@Nonnull Object mock) {
        Class<?> mockedClass = mock.getClass();
        return !baseTypes.contains(mockedClass) && isWithCapturing(mockedClass);
    }

    private boolean isWithCapturing(@Nonnull Class<?> mockedClass) {
        Class<?>[] interfaces = mockedClass.getInterfaces();

        for (Class<?> anInterface : interfaces) {
            if (baseTypes.contains(anInterface)) {
                return true;
            }
        }

        Class<?> superclass = mockedClass.getSuperclass();
        return superclass != Object.class && (baseTypes.contains(superclass) || isWithCapturing(superclass));
    }

    void cleanUp() {
        baseTypes.clear();
    }
}
