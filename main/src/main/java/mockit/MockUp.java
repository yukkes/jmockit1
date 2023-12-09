/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit;

import static java.lang.reflect.Modifier.isAbstract;

import static mockit.internal.util.GeneratedClasses.isGeneratedImplementationClassName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.classGeneration.ConcreteSubclass;
import mockit.internal.faking.CaptureOfFakedImplementations;
import mockit.internal.faking.FakeClassSetup;
import mockit.internal.faking.FakeClasses;
import mockit.internal.faking.FakedImplementationClass;
import mockit.internal.reflection.ConstructorReflection;
import mockit.internal.reflection.MockInvocationHandler;
import mockit.internal.startup.Startup;
import mockit.internal.state.TestRun;

/**
 * A base class used in the creation of a <em>fake</em> for an <em>external</em> type, which is usually a class from
 * some library or component used from the <em>internal</em> codebase of the system under test (SUT). Such fake classes
 * can be used as <em>fake implementations</em> for use in unit or integration tests. For example:
 *
 * <pre>{@code
 * public final class FakeSystem <strong>extends MockUp&lt;System></strong> {
 *    <strong>&#64;Mock</strong> public static long nanoTime() { return 123L; }
 * }
 * }</pre>
 *
 * One or more <em>fake methods</em> annotated {@linkplain Mock as such} must be defined in the concrete subclass. Each
 * <code>@Mock</code> method should have a matching method or constructor in the faked class. At runtime, the execution
 * of a faked method/constructor will get redirected to the corresponding fake method.
 * <p>
 * When the type to be faked is specified indirectly through a {@linkplain TypeVariable type variable}, then that type
 * is taken as a <em>base</em> type whose concrete implementation classes should <em>also</em> get faked. Example:
 *
 * <pre>{@code
 * &#64;Test
 * public &lt;<strong>BC extends SomeBaseClass</strong>> void someTest() {
 *     new MockUp&lt;<strong>BC</strong>>() {
 *        &#64;Mock int someMethod(int i) { return i + 1; }
 *     };
 *
 *     int i = new AConcreteSubclass().someMethod(1);
 *     assertEquals(2, i);
 * }
 * }</pre>
 *
 * @param <T>
 *            specifies the type to be faked; if a type variable is used, then all implementation classes extending or
 *            implementing that base type are also faked; if the type argument itself is a parameterized type, then only
 *            its raw type is considered
 *
 * @see #MockUp()
 * @see #MockUp(Class)
 * @see #getMockInstance()
 * @see #onTearDown()
 * @see #targetType
 * @see <a href="http://jmockit.github.io/tutorial/Faking.html#setUp" target="tutorial">Tutorial</a>
 */
public abstract class MockUp<T> {
    static {
        Startup.verifyInitialization();
    }

    /**
     * Holds the class or generic type targeted by this fake instance.
     */
    protected final Type targetType;

    @Nullable
    private final Class<?> fakedClass;
    @Nullable
    private T fakeInstance;

    /**
     * Applies the {@linkplain Mock fake methods} defined in the concrete subclass to the class specified through the
     * type parameter.
     *
     * @see #MockUp(Class)
     */
    protected MockUp() {
        targetType = getTypeToFake();
        Class<T> classToFake = null;

        if (targetType instanceof Class<?>) {
            // noinspection unchecked
            classToFake = (Class<T>) targetType;
        } else if (targetType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) targetType;
            // noinspection unchecked
            classToFake = (Class<T>) parameterizedType.getRawType();
        }

        if (classToFake != null) {
            fakedClass = redefineClassOrImplementInterface(classToFake);
        } else {
            Type[] typesToFake = ((TypeVariable<?>) targetType).getBounds();

            fakedClass = typesToFake.length > 1
                    ? new FakedImplementationClass<T>(this).createImplementation(typesToFake)
                    : new CaptureOfFakedImplementations(this, typesToFake[0]).apply();
        }
    }

    @Nullable
    private MockUp<?> findPreviouslyFakedClassIfFakeAlreadyApplied() {
        FakeClasses fakeClasses = TestRun.getFakeClasses();
        return fakeClasses.findPreviouslyAppliedFake(this);
    }

    /**
     * Gets the type to fake.
     *
     * @return the type to fake
     */
    @Nonnull
    private Type getTypeToFake() {
        Class<?> currentClass = getClass();

        do {
            Type superclass = currentClass.getGenericSuperclass();

            if (superclass instanceof ParameterizedType) {
                return ((ParameterizedType) superclass).getActualTypeArguments()[0];
            }

            if (superclass == MockUp.class) {
                throw new IllegalArgumentException("No target type");
            }

            currentClass = (Class<?>) superclass;
        } while (true);
    }

    @Nonnull
    private Class<?> redefineClassOrImplementInterface(@Nonnull Class<T> classToFake) {
        if (classToFake.isInterface()) {
            return createInstanceOfFakedImplementationClass(classToFake, targetType);
        }

        Class<T> realClass = classToFake;

        if (isAbstract(classToFake.getModifiers())) {
            classToFake = new ConcreteSubclass<T>(classToFake).generateClass();
        }

        redefineMethods(realClass, classToFake, targetType);
        return classToFake;
    }

    @Nonnull
    private Class<T> createInstanceOfFakedImplementationClass(@Nonnull Class<T> classToFake,
            @Nullable Type typeToFake) {
        FakedImplementationClass<T> fakedImplementationClass = new FakedImplementationClass<>(this);
        return fakedImplementationClass.createImplementation(classToFake, typeToFake);
    }

    private void redefineMethods(@Nonnull Class<T> realClass, @Nonnull Class<T> classToFake,
            @Nullable Type genericFakedType) {
        FakeClassSetup fakeSetup = new FakeClassSetup(realClass, classToFake, genericFakedType, this);
        fakeSetup.redefineMethods();
    }

    /**
     * Applies the {@linkplain Mock fake methods} defined in the fake class to the given class.
     * <p>
     * In most cases, the {@linkplain #MockUp() constructor with no parameters} can be used. This variation is useful
     * when the type to be faked is not known at compile time. For example, it can be used with an {@linkplain Mock
     * $advice} method and the <code>fakes</code> system property in order to have an aspect-like fake implementation
     * applicable to any class; it can then be applied at the beginning of the test run with the desired target class
     * being specified in the test run configuration.
     *
     * @param targetClass
     *            the target class
     *
     * @see #MockUp()
     */
    protected MockUp(Class<?> targetClass) {
        targetType = targetClass;
        MockUp<?> previousFake = findPreviouslyFakedClassIfFakeAlreadyApplied();

        if (previousFake != null) {
            fakedClass = previousFake.fakedClass;
            return;
        }

        if (targetClass.isInterface()) {
            // noinspection unchecked
            fakedClass = createInstanceOfFakedImplementationClass((Class<T>) targetClass, targetClass);
        } else {
            fakedClass = targetClass;
            // noinspection unchecked
            Class<T> realClass = (Class<T>) targetClass;
            redefineMethods(realClass, realClass, null);
            fakeInstance = null;
        }
    }

    /**
     * Applies the {@linkplain Mock fake methods} defined in the fake class to the type specified through the type
     * parameter, but only affecting the given instance.
     * <p>
     * In most cases, the constructor with no parameters should be adequate. This variation can be used when fake data
     * or behavior is desired only for a particular instance, with other instances remaining unaffected; or when
     * multiple fake objects carrying different states are desired, with one fake instance per real instance.
     * <p>
     * If {@link #getMockInstance()} later gets called on this fake instance, it will return the instance that was given
     * here.
     *
     * @param targetInstance
     *            a real instance of the type to be faked, meant to be the only one of that type that should be affected
     *            by this fake instance
     *
     * @see #MockUp()
     * @see #MockUp(Class)
     */
    protected MockUp(T targetInstance) {
        @SuppressWarnings("unchecked")
        Class<T> classToFake = (Class<T>) targetInstance.getClass();
        targetType = classToFake;
        fakedClass = classToFake;
        redefineMethods(classToFake, classToFake, classToFake);

        setFakeInstance(targetInstance);
    }

    private void setFakeInstance(@Nonnull T fakeInstance) {
        TestRun.getFakeClasses().addFake(this, fakeInstance);
        this.fakeInstance = fakeInstance;
    }

    /**
     * Returns the mock instance exclusively associated with this fake instance. If the faked type was an interface,
     * then said instance is the one that was automatically created when the fake was applied. If it was a class, and no
     * such instance is currently associated with this (stateful) fake object, then a new <em>uninitialized</em>
     * instance of the faked class is created and returned, becoming associated with the fake.
     * <p>
     * In any case, for a given fake instance this method will always return the same fake instance.
     *
     * @return Mocked instances
     */
    public final T getMockInstance() {
        if (fakeInstance == null && fakedClass != null) {
            @SuppressWarnings("unchecked")
            T newInstance = (T) createFakeInstance(fakedClass);
            fakeInstance = newInstance;
        }

        return fakeInstance;
    }

    @Nonnull
    private Object createFakeInstance(@Nonnull Class<?> fakedClass) {
        String fakedClassName = fakedClass.getName();

        if (isGeneratedImplementationClassName(fakedClassName)) {
            return ConstructorReflection.newInstanceUsingPublicDefaultConstructor(fakedClass);
        }

        if (Proxy.isProxyClass(fakedClass)) {
            return MockInvocationHandler.newMockedInstance(fakedClass);
        }

        return ConstructorReflection.newUninitializedInstance(fakedClass);
    }

    /**
     * An empty method that can be overridden in a fake class that wants to be notified whenever the fake is
     * automatically torn down. Tear down happens when the fake goes out of scope: at the end of the test when applied
     * inside a test, at the end of the test class when applied before the test class, or at the end of the test run
     * when applied through the "<code>fakes</code>" system property.
     * <p>
     * By default, this method does nothing.
     */
    protected void onTearDown() {
    }
}
