/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.invocation;

import javax.annotation.Nonnull;

import mockit.internal.util.StackTrace;

final class ExpectationError extends AssertionError {
    private static final long serialVersionUID = 1L;
    private String message;

    @Nonnull
    @Override
    public String toString() {
        return message;
    }

    void defineCause(@Nonnull String title, @Nonnull Throwable error) {
        message = title;
        StackTrace.filterStackTrace(this);
        error.initCause(this);
    }
}
