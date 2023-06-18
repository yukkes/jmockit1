/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.injection.constructor;

import static mockit.internal.util.Utilities.getClassType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.injection.InjectionProvider;

final class ConstructorParameter extends InjectionProvider {
    @Nonnull
    private final Class<?> classOfDeclaredType;
    @Nonnull
    private final Annotation[] annotations;
    @Nullable
    private final Object value;

    ConstructorParameter(@Nonnull Type declaredType, @Nonnull Annotation[] annotations, @Nonnull String name,
            @Nullable Object value) {
        super(declaredType, name);
        classOfDeclaredType = getClassType(declaredType);
        this.annotations = annotations;
        this.value = value;
    }

    @Nonnull
    @Override
    public Class<?> getClassOfDeclaredType() {
        return classOfDeclaredType;
    }

    @Nonnull
    @Override
    public Annotation[] getAnnotations() {
        return annotations;
    }

    @Nullable
    @Override
    public Object getValue(@Nullable Object owner) {
        return value;
    }

    @Override
    public String toString() {
        return "parameter " + super.toString();
    }
}
