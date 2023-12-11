/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.mocking;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.util.TestMethod;

public final class ParameterTypeRedefinitions extends TypeRedefinitions {
    @Nonnull
    private final TestMethod testMethod;
    @Nonnull
    private final MockedType[] mockParameters;
    @Nonnull
    private final List<MockedType> injectableParameters;

    public ParameterTypeRedefinitions(@Nonnull TestMethod testMethod, @Nonnull Object[] parameterValues) {
        this.testMethod = testMethod;
        int n = testMethod.getParameterCount();
        mockParameters = new MockedType[n];
        injectableParameters = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            Object mock = parameterValues[i];
            createMockedTypeFromMockParameterDeclaration(i, mock);
        }

        InstanceFactory[] instanceFactories = redefineMockedTypes();
        instantiateMockedTypes(instanceFactories);
    }

    private void createMockedTypeFromMockParameterDeclaration(@Nonnegative int parameterIndex, @Nullable Object mock) {
        Type parameterType = testMethod.getParameterType(parameterIndex);
        Annotation[] annotationsOnParameter = testMethod.getParameterAnnotations(parameterIndex);
        Class<?> parameterImplementationClass = mock == null ? null : mock.getClass();
        MockedType mockedType = new MockedType(testMethod, parameterIndex, parameterType, annotationsOnParameter,
                parameterImplementationClass);

        if (mockedType.isMockableType()) {
            mockParameters[parameterIndex] = mockedType;
        }

        if (mockedType.injectable) {
            injectableParameters.add(mockedType);
            testMethod.setParameterValue(parameterIndex, mockedType.providedValue);
        }
    }

    @Nonnull
    private InstanceFactory[] redefineMockedTypes() {
        int n = mockParameters.length;
        InstanceFactory[] instanceFactories = new InstanceFactory[n];

        for (int i = 0; i < n; i++) {
            MockedType mockedType = mockParameters[i];

            if (mockedType != null) {
                instanceFactories[i] = redefineMockedType(mockedType);
            }
        }

        return instanceFactories;
    }

    @Nullable
    private InstanceFactory redefineMockedType(@Nonnull MockedType mockedType) {
        TypeRedefinition typeRedefinition = new TypeRedefinition(mockedType);
        InstanceFactory instanceFactory = typeRedefinition.redefineType();

        if (instanceFactory != null) {
            addTargetClass(mockedType);
        }

        return instanceFactory;
    }

    private void registerCaptureOfNewInstances(@Nonnull MockedType mockedType, @Nonnull Object originalInstance) {
        if (captureOfNewInstances == null) {
            captureOfNewInstances = new CaptureOfNewInstances();
        }

        captureOfNewInstances.registerCaptureOfNewInstances(mockedType, originalInstance);
        captureOfNewInstances.makeSureAllSubtypesAreModified(mockedType);
    }

    private void instantiateMockedTypes(@Nonnull InstanceFactory[] instanceFactories) {
        for (int paramIndex = 0; paramIndex < instanceFactories.length; paramIndex++) {
            InstanceFactory instanceFactory = instanceFactories[paramIndex];

            if (instanceFactory != null) {
                MockedType mockedType = mockParameters[paramIndex];
                @Nonnull
                Object mockedInstance = instantiateMockedType(mockedType, instanceFactory, paramIndex);
                testMethod.setParameterValue(paramIndex, mockedInstance);
                mockedType.providedValue = mockedInstance;
            }
        }
    }

    @Nonnull
    private Object instantiateMockedType(@Nonnull MockedType mockedType, @Nonnull InstanceFactory instanceFactory,
            @Nonnegative int paramIndex) {
        Object mock = testMethod.getParameterValue(paramIndex);

        if (mock == null) {
            mock = instanceFactory.create();
        }

        registerMock(mockedType, mock);

        if (mockedType.withInstancesToCapture()) {
            registerCaptureOfNewInstances(mockedType, mock);
        }

        return mock;
    }

    @Nonnull
    public List<MockedType> getInjectableParameters() {
        return injectableParameters;
    }
}
