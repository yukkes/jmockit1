/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.injection;

import static java.lang.reflect.Modifier.isFinal;

import static mockit.internal.reflection.FieldReflection.getFieldValue;
import static mockit.internal.reflection.FieldReflection.setFieldValue;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.Tested;
import mockit.internal.util.TypeConversion;

final class TestedField extends TestedObject {
    @Nonnull
    private final Field testedField;

    TestedField(@Nonnull InjectionState injectionState, @Nonnull Field field, @Nonnull Tested metadata) {
        super(injectionState, metadata, field.getDeclaringClass(), field.getName(), field.getGenericType(),
                field.getType());
        testedField = field;
    }

    boolean isFromBaseClass(@Nonnull Class<?> testClass) {
        return testedField.getDeclaringClass() != testClass;
    }

    @Override
    boolean alreadyInstantiated(@Nonnull Object testClassInstance) {
        return isAvailableDuringSetup() && getFieldValue(testedField, testClassInstance) != null;
    }

    @Nullable
    @Override
    Object getExistingTestedInstanceIfApplicable(@Nonnull Object testClassInstance) {
        Object testedObject = null;

        if (!createAutomatically) {
            Class<?> targetClass = testedField.getType();
            testedObject = getFieldValue(testedField, testClassInstance);

            if (testedObject == null || isNonInstantiableType(targetClass, testedObject)) {
                String providedValue = metadata.value();

                if (!providedValue.isEmpty()) {
                    testedObject = TypeConversion.convertFromString(targetClass, providedValue);
                }

                createAutomatically = testedObject == null && !isFinal(testedField.getModifiers());
            }
        }

        return testedObject;
    }

    @Override
    void setInstance(@Nonnull Object testClassInstance, @Nullable Object testedInstance) {
        setFieldValue(testedField, testClassInstance, testedInstance);
    }
}
