/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.classGeneration;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import mockit.asm.classes.ClassReader;
import mockit.asm.classes.ClassVisitor;
import mockit.internal.ClassFile;
import mockit.internal.util.ClassLoad;
import mockit.internal.util.GeneratedClasses;
import mockit.internal.util.Utilities;

/**
 * Allows the creation of new implementation classes for interfaces and abstract classes.
 */
public abstract class ImplementationClass<T> {
    @Nonnull
    protected final Class<?> sourceClass;
    @Nonnull
    protected String generatedClassName;

    protected ImplementationClass(@Nonnull Type mockedType) {
        this(Utilities.getClassType(mockedType));
    }

    protected ImplementationClass(@Nonnull Class<?> mockedClass) {
        this(mockedClass, GeneratedClasses.getNameForGeneratedClass(mockedClass, null));
    }

    protected ImplementationClass(@Nonnull Class<?> sourceClass, @Nonnull String desiredClassName) {
        this.sourceClass = sourceClass;
        generatedClassName = desiredClassName;
    }

    @Nonnull
    public final Class<T> generateClass() {
        ClassReader classReader = ClassFile.createReaderOrGetFromCache(sourceClass);

        ClassVisitor modifier = createMethodBodyGenerator(classReader);
        classReader.accept(modifier);

        return defineNewClass(modifier);
    }

    @Nonnull
    protected abstract ClassVisitor createMethodBodyGenerator(@Nonnull ClassReader cr);

    @Nonnull
    private Class<T> defineNewClass(@Nonnull ClassVisitor modifier) {
        final ClassLoader parentLoader = ClassLoad.getClassLoaderWithAccess(sourceClass);
        final byte[] modifiedClassfile = modifier.toByteArray();

        try {
            return (Class<T>) new ClassLoader(parentLoader) {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    if (!name.equals(generatedClassName)) {
                        return parentLoader.loadClass(name);
                    }

                    return defineClass(name, modifiedClassfile, 0, modifiedClassfile.length);
                }
            }.findClass(generatedClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to define class: " + generatedClassName, e);
        }
    }
}
