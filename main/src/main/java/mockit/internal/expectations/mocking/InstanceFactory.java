/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.mocking;

import java.lang.reflect.Constructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.util.StackTrace;

import org.objenesis.ObjenesisHelper;

import sun.reflect.ReflectionFactory;

/**
 * Factory for the creation of new mocked instances, and for obtaining/clearing the last instance created. There are
 * separate subclasses dedicated to mocked interfaces and mocked classes.
 */
public abstract class InstanceFactory {
    @SuppressWarnings("UseOfSunClasses")
    public static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();
    public static final Constructor<?> OBJECT_CONSTRUCTOR;
    static {
        try {
            OBJECT_CONSTRUCTOR = Object.class.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private final Class<?> concreteClass;
    @Nullable
    Object lastInstance;

    InstanceFactory(@Nonnull Class<?> concreteClass) {
        this.concreteClass = concreteClass;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    final <T> T newUninitializedConcreteClassInstance() {
        try {
            return (T) ObjenesisHelper.newInstance(concreteClass);
        } catch (Exception e) {
            StackTrace.filterStackTrace(e);
            e.printStackTrace();
            throw e;
        }
    }

    @Nonnull
    public abstract Object create();

    @Nullable
    public final Object getLastInstance() {
        return lastInstance;
    }

    public abstract void clearLastInstance();

    static final class InterfaceInstanceFactory extends InstanceFactory {
        @Nullable
        private Object emptyProxy;

        InterfaceInstanceFactory(@Nonnull Object emptyProxy) {
            super(emptyProxy.getClass());
            this.emptyProxy = emptyProxy;
        }

        @Nonnull
        @Override
        public Object create() {
            if (emptyProxy == null) {
                emptyProxy = newUninitializedConcreteClassInstance();
            }

            lastInstance = emptyProxy;
            return emptyProxy;
        }

        @Override
        public void clearLastInstance() {
            emptyProxy = null;
            lastInstance = null;
        }
    }

    static final class ClassInstanceFactory extends InstanceFactory {
        ClassInstanceFactory(@Nonnull Class<?> concreteClass) {
            super(concreteClass);
        }

        @Override
        @Nonnull
        public Object create() {
            lastInstance = newUninitializedConcreteClassInstance();
            return lastInstance;
        }

        @Override
        public void clearLastInstance() {
            lastInstance = null;
        }
    }
}
