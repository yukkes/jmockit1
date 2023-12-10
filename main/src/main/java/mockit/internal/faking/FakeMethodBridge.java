/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.Invocation;
import mockit.internal.ClassLoadingBridge;
import mockit.internal.reflection.MethodReflection;
import mockit.internal.reflection.ParameterReflection;
import mockit.internal.state.TestRun;
import mockit.internal.util.TypeDescriptor;

public final class FakeMethodBridge extends ClassLoadingBridge {
    @Nonnull
    public static final ClassLoadingBridge MB = new FakeMethodBridge();

    private FakeMethodBridge() {
        super("$FMB");
    }

    @Nullable
    @Override
    public Object invoke(@Nullable Object fakedInstance, Method method, @Nonnull Object[] args) throws Throwable {
        String fakeClassDesc = (String) args[0];
        String fakedClassDesc = (String) args[1];
        String fakeDesc = (String) args[4];

        Object fake = TestRun.getFake(fakeClassDesc, fakedInstance);

        if (notToBeMocked(fakedInstance, fakedClassDesc)) {
            return Void.class;
        }

        String fakeName = (String) args[3];
        int fakeStateIndex = (Integer) args[5];
        Object[] fakeArgs = extractArguments(6, args);

        return callFake(fakedInstance, fake, fakedClassDesc, fakeName, fakeDesc, fakeStateIndex, fakeArgs);
    }

    @Nullable
    private static Object callFake(@Nullable Object fakedInstance, @Nonnull Object fake, @Nonnull String fakedClassDesc,
            @Nonnull String fakeOrFakedName, @Nonnull String fakeOrFakedDesc, int fakeStateIndex,
            @Nonnull Object[] fakeArgs) throws Throwable {
        Class<?> fakeClass = fake.getClass();

        if (fakeStateIndex < 0) {
            return executeSimpleFakeMethod(fakeClass, fake, fakeOrFakedName, fakeOrFakedDesc, fakeArgs);
        }

        FakeState fakeState = TestRun.getFakeStates().getFakeState(fake, fakeStateIndex);

        if (!fakeState.fakeMethod.hasInvocationParameter()) {
            return executeFakeMethodWithoutInvocationArgument(fakeState, fakeClass, fake, fakeOrFakedDesc, fakeArgs);
        }

        if (fakeState.shouldProceedIntoRealImplementation(fakedInstance, fakedClassDesc)) {
            return Void.class;
        }

        return executeFakeMethodWithInvocationArgument(fakeState, fakeClass, fake, fakedInstance, fakedClassDesc,
                fakeOrFakedName, fakeOrFakedDesc, fakeArgs);
    }

    @Nullable
    private static Object executeSimpleFakeMethod(@Nonnull Class<?> fakeClass, @Nullable Object fake,
            @Nonnull String fakeOrFakedName, @Nonnull String fakeOrFakedDesc, @Nonnull Object[] fakeArgs)
            throws Throwable {
        Class<?>[] paramClasses = TypeDescriptor.getParameterTypes(fakeOrFakedDesc);
        return MethodReflection.invokeWithCheckedThrows(fakeClass, fake, fakeOrFakedName, paramClasses, fakeArgs);
    }

    @Nullable
    private static Object executeFakeMethodWithoutInvocationArgument(@Nonnull FakeState fakeState,
            @Nonnull Class<?> fakeClass, @Nullable Object fake, @Nonnull String fakeOrFakedDesc,
            @Nonnull Object[] fakeArgs) throws Throwable {
        Class<?>[] paramClasses = TypeDescriptor.getParameterTypes(fakeOrFakedDesc);
        Method fakeMethod = fakeState.getFakeMethod(fakeClass, paramClasses);
        return MethodReflection.invokeWithCheckedThrows(fake, fakeMethod, fakeArgs);
    }

    @Nullable
    private static Object executeFakeMethodWithInvocationArgument(@Nonnull FakeState fakeState,
            @Nonnull Class<?> fakeClass, @Nullable Object fake, @Nullable Object fakedInstance,
            @Nonnull String fakedClassDesc, @Nonnull String fakedName, @Nonnull String fakedDesc,
            @Nonnull Object[] fakeArgs) throws Throwable {
        Class<?>[] paramClasses;
        Method fakeMethod;
        FakeInvocation invocation;
        Object[] executionArgs;

        if (fakeState.fakeMethod.hasInvocationParameterOnly()) {
            paramClasses = new Class[] { Invocation.class };
            fakeMethod = fakeState.getFakeMethod(fakeClass, paramClasses);
            invocation = new FakeInvocation(fakedInstance, fakeArgs, fakeState, fakedClassDesc, fakedName, fakedDesc);
            executionArgs = new Object[] { invocation };
        } else {
            paramClasses = TypeDescriptor.getParameterTypes(fakedDesc);
            fakeMethod = fakeState.getFakeMethod(fakeClass, paramClasses);

            // noinspection AssignmentToMethodParameter
            fakedDesc = fakeState.fakeMethod.fakeDescWithoutInvocationParameter;
            invocation = new FakeInvocation(fakedInstance, fakeArgs, fakeState, fakedClassDesc, fakedName, fakedDesc);
            executionArgs = ParameterReflection.argumentsWithExtraFirstValue(fakeArgs, invocation);
        }

        Object result = MethodReflection.invokeWithCheckedThrows(fake, fakeMethod, executionArgs);
        return invocation.shouldProceedIntoConstructor() ? Void.class : result;
    }
}
