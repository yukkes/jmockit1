/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.invocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.BaseInvocation;
import mockit.internal.expectations.state.ExecutingTest;
import mockit.internal.state.TestRun;

final class DelegateInvocation extends BaseInvocation {
    @Nonnull
    private final InvocationArguments invocationArguments;

    DelegateInvocation(@Nullable Object invokedInstance, @Nonnull Object[] invokedArguments,
            @Nonnull ExpectedInvocation expectedInvocation, @Nonnull InvocationConstraints constraints) {
        super(invokedInstance, invokedArguments, constraints.invocationCount);
        invocationArguments = expectedInvocation.arguments;
    }

    @Nonnull
    @Override
    protected Member findRealMember() {
        return invocationArguments.getRealMethodOrConstructor();
    }

    @Override
    public void prepareToProceed() {
        ExecutingTest executingTest = TestRun.getExecutingTest();

        if (getInvokedMember() instanceof Constructor) {
            executingTest.markAsProceedingIntoRealImplementation();
        } else {
            executingTest.markAsProceedingIntoRealImplementation(this);
        }
    }

    @Override
    public void cleanUpAfterProceed() {
        TestRun.getExecutingTest().clearProceedingState();
    }
}
