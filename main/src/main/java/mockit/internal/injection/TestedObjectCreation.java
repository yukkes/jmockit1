/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.injection;

import static java.lang.reflect.Modifier.isAbstract;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.asm.classes.ClassReader;
import mockit.asm.classes.ClassVisitor;
import mockit.internal.classGeneration.ImplementationClass;
import mockit.internal.expectations.mocking.SubclassGenerationModifier;
import mockit.internal.injection.constructor.ConstructorInjection;
import mockit.internal.injection.constructor.ConstructorSearch;
import mockit.internal.injection.full.FullInjection;
import mockit.internal.state.TestRun;

public final class TestedObjectCreation {
    @Nonnull
    private final InjectionState injectionState;
    @Nullable
    private final FullInjection fullInjection;
    @Nonnull
    final TestedClass testedClass;

    TestedObjectCreation(@Nonnull InjectionState injectionState, @Nullable FullInjection fullInjection,
            @Nonnull Type declaredType, @Nonnull Class<?> declaredClass) {
        this.injectionState = injectionState;
        this.fullInjection = fullInjection;
        Class<?> actualTestedClass = isAbstract(declaredClass.getModifiers())
                ? generateSubclass(declaredType, declaredClass) : declaredClass;
        testedClass = new TestedClass(declaredType, actualTestedClass);
    }

    @Nonnull
    private static Class<?> generateSubclass(@Nonnull final Type testedType, @Nonnull final Class<?> abstractClass) {
        Class<?> generatedSubclass = new ImplementationClass<Object>(abstractClass) {
            @Nonnull
            @Override
            protected ClassVisitor createMethodBodyGenerator(@Nonnull ClassReader cr) {
                return new SubclassGenerationModifier(abstractClass, testedType, cr, generatedClassName, true);
            }
        }.generateClass();

        TestRun.mockFixture().registerMockedClass(generatedSubclass);
        return generatedSubclass;
    }

    public TestedObjectCreation(@Nonnull InjectionState injectionState, @Nullable FullInjection fullInjection,
            @Nonnull Class<?> implementationClass) {
        this.injectionState = injectionState;
        this.fullInjection = fullInjection;
        testedClass = new TestedClass(implementationClass, implementationClass);
    }

    @Nullable
    public Object create(boolean required, boolean needToConstruct) {
        ConstructorSearch constructorSearch = new ConstructorSearch(injectionState, testedClass, fullInjection != null);
        Constructor<?> constructor = constructorSearch.findConstructorToUse();

        if (constructor == null) {
            String description = constructorSearch.getDescription();
            throw new IllegalArgumentException(
                    "No constructor in tested class that can be satisfied by available tested/injectable values"
                            + description);
        }

        ConstructorInjection constructorInjection = new ConstructorInjection(injectionState, fullInjection,
                constructor);
        return constructorInjection.instantiate(constructorSearch.parameterProviders, testedClass, required,
                needToConstruct);
    }
}
