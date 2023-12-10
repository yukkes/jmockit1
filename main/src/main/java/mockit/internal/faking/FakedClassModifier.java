/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import static mockit.asm.jvmConstants.Opcodes.ACONST_NULL;
import static mockit.asm.jvmConstants.Opcodes.ALOAD;
import static mockit.asm.jvmConstants.Opcodes.CHECKCAST;
import static mockit.asm.jvmConstants.Opcodes.DUP;
import static mockit.asm.jvmConstants.Opcodes.DUP_X1;
import static mockit.asm.jvmConstants.Opcodes.IFEQ;
import static mockit.asm.jvmConstants.Opcodes.IFNE;
import static mockit.asm.jvmConstants.Opcodes.IF_ACMPEQ;
import static mockit.asm.jvmConstants.Opcodes.ILOAD;
import static mockit.asm.jvmConstants.Opcodes.INSTANCEOF;
import static mockit.asm.jvmConstants.Opcodes.INVOKESTATIC;
import static mockit.asm.jvmConstants.Opcodes.INVOKEVIRTUAL;
import static mockit.asm.jvmConstants.Opcodes.IRETURN;
import static mockit.asm.jvmConstants.Opcodes.RETURN;
import static mockit.asm.jvmConstants.Opcodes.SIPUSH;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.MockUp;
import mockit.asm.classes.ClassReader;
import mockit.asm.controlFlow.Label;
import mockit.asm.jvmConstants.Access;
import mockit.asm.methods.MethodVisitor;
import mockit.asm.types.JavaType;
import mockit.asm.types.ReferenceType;
import mockit.internal.BaseClassModifier;
import mockit.internal.faking.FakeMethods.FakeMethod;
import mockit.internal.state.TestRun;
import mockit.internal.util.ClassLoad;

/**
 * Responsible for generating all necessary bytecode in the redefined (real) class. Such code will redirect calls made
 * on "real" methods to equivalent calls on the corresponding "fake" methods. The original code won't be executed by the
 * running JVM until the class redefinition is undone.
 * <p>
 * Methods in the real class with no corresponding fake methods are unaffected.
 * <p>
 * Any fields (static or not) in the real class remain untouched.
 */
final class FakedClassModifier extends BaseClassModifier {
    private static final int ABSTRACT_OR_SYNTHETIC = Access.ABSTRACT + Access.SYNTHETIC;

    @Nonnull
    private final FakeMethods fakeMethods;
    private final boolean useClassLoadingBridgeForUpdatingFakeState;
    @Nonnull
    private final Class<?> fakedClass;
    private FakeMethod fakeMethod;
    private boolean isConstructor;

    /**
     * Initializes the modifier for a given real/fake class pair.
     * <p>
     * The fake instance provided will receive calls for any instance methods defined in the fake class. Therefore, it
     * needs to be later recovered by the modified bytecode inside the real method. To enable this, the fake instance is
     * added to a global data structure made available through the {@link TestRun#getFake(String, Object)} method.
     *
     * @param cr
     *            the class file reader for the real class
     * @param realClass
     *            the class to be faked, or a base type of an implementation class to be faked
     * @param fake
     *            an instance of the fake class
     * @param fakeMethods
     *            contains the set of fake methods collected from the fake class; each fake method is identified by a
     *            pair composed of "name" and "desc", where "name" is the method name, and "desc" is the JVM internal
     *            description of the parameters; once the real class modification is complete this set will be empty,
     *            unless no corresponding real method was found for any of its method identifiers
     */
    FakedClassModifier(@Nonnull ClassReader cr, @Nonnull Class<?> realClass, @Nonnull MockUp<?> fake,
            @Nonnull FakeMethods fakeMethods) {
        super(cr);
        fakedClass = realClass;
        this.fakeMethods = fakeMethods;

        ClassLoader classLoaderOfRealClass = realClass.getClassLoader();
        useClassLoadingBridgeForUpdatingFakeState = ClassLoad.isClassLoaderWithNoDirectAccess(classLoaderOfRealClass);
        inferUseOfClassLoadingBridge(classLoaderOfRealClass, fake);
    }

    private void inferUseOfClassLoadingBridge(@Nullable ClassLoader classLoaderOfRealClass, @Nonnull Object fake) {
        setUseClassLoadingBridge(classLoaderOfRealClass);

        if (!useClassLoadingBridge && !isPublic(fake.getClass().getModifiers())) {
            useClassLoadingBridge = true;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, @Nonnull String name, @Nonnull String desc, @Nullable String signature,
            @Nullable String[] exceptions) {
        if ((access & ABSTRACT_OR_SYNTHETIC) != 0) {
            if (isAbstract(access)) {
                // Marks a matching fake method (if any) as having the corresponding faked method.
                fakeMethods.findMethod(access, name, desc, signature);
            }

            return cw.visitMethod(access, name, desc, signature, exceptions);
        }

        isConstructor = "<init>".equals(name);

        if (isConstructor && isFakedSuperclass() || !hasFake(access, name, desc, signature)) {
            return cw.visitMethod(access, name, desc, signature, exceptions);
        }

        startModifiedMethodVersion(access, name, desc, signature, exceptions);

        if (isNative(methodAccess)) {
            generateCodeForInterceptedNativeMethod();
            return methodAnnotationsVisitor;
        }

        return copyOriginalImplementationWithInjectedInterceptionCode();
    }

    private boolean hasFake(int access, @Nonnull String name, @Nonnull String desc, @Nullable String signature) {
        String fakeName = getCorrespondingFakeName(name);
        fakeMethod = fakeMethods.findMethod(access, fakeName, desc, signature);
        return fakeMethod != null;
    }

    @Nonnull
    private static String getCorrespondingFakeName(@Nonnull String name) {
        if ("<init>".equals(name)) {
            return "$init";
        }

        if ("<clinit>".equals(name)) {
            return "$clinit";
        }

        return name;
    }

    private boolean isFakedSuperclass() {
        return fakedClass != fakeMethods.getRealClass();
    }

    private void generateCodeForInterceptedNativeMethod() {
        generateCallToUpdateFakeState();
        generateCallToFakeMethod();
        generateMethodReturn();
        mw.visitMaxStack(1); // dummy value, real one is calculated by ASM
    }

    @Override
    protected void generateInterceptionCode() {
        Label startOfRealImplementation = null;

        if (!isStatic(methodAccess) && !isConstructor && isFakedSuperclass()) {
            Class<?> targetClass = fakeMethods.getRealClass();

            if (fakedClass.getClassLoader() == targetClass.getClassLoader()) {
                startOfRealImplementation = new Label();
                mw.visitVarInsn(ALOAD, 0);
                mw.visitTypeInsn(INSTANCEOF, JavaType.getInternalName(targetClass));
                mw.visitJumpInsn(IFEQ, startOfRealImplementation);
            }
        }

        generateCallToUpdateFakeState();

        if (isConstructor) {
            generateConditionalCallForFakedConstructor();
        } else {
            generateConditionalCallForFakedMethod(startOfRealImplementation);
        }
    }

    private void generateCallToUpdateFakeState() {
        if (useClassLoadingBridgeForUpdatingFakeState) {
            generateCallToControlMethodThroughClassLoadingBridge();
            mw.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        } else {
            mw.visitLdcInsn(fakeMethods.getFakeClassInternalName());
            generateCodeToPassThisOrNullIfStaticMethod();
            mw.visitIntInsn(SIPUSH, fakeMethod.getIndexForFakeState());
            mw.visitMethodInsn(INVOKESTATIC, "mockit/internal/state/TestRun", "updateFakeState",
                    "(Ljava/lang/String;Ljava/lang/Object;I)Z", false);
        }
    }

    private void generateCallToControlMethodThroughClassLoadingBridge() {
        generateCodeToObtainInstanceOfClassLoadingBridge(FakeBridge.MB);

        // First and second "invoke" arguments:
        generateCodeToPassThisOrNullIfStaticMethod();
        mw.visitInsn(ACONST_NULL);

        // Create array for call arguments (third "invoke" argument):
        generateCodeToCreateArrayOfObject(2);

        int i = 0;
        generateCodeToFillArrayElement(i, fakeMethods.getFakeClassInternalName());
        i++;
        generateCodeToFillArrayElement(i, fakeMethod.getIndexForFakeState());

        generateCallToInvocationHandler();
    }

    private void generateConditionalCallForFakedMethod(@Nullable Label startOfRealImplementation) {
        if (startOfRealImplementation == null) {
            // noinspection AssignmentToMethodParameter
            startOfRealImplementation = new Label();
        }

        mw.visitJumpInsn(IFEQ, startOfRealImplementation);
        generateCallToFakeMethod();
        generateMethodReturn();
        mw.visitLabel(startOfRealImplementation);
    }

    private void generateConditionalCallForFakedConstructor() {
        generateCallToFakeMethod();

        int jumpInsnOpcode;

        if (shouldUseClassLoadingBridge()) {
            mw.visitLdcInsn(VOID_TYPE);
            jumpInsnOpcode = IF_ACMPEQ;
        } else {
            jumpInsnOpcode = fakeMethod.hasInvocationParameter() ? IFNE : IFEQ;
        }

        Label startOfRealImplementation = new Label();
        mw.visitJumpInsn(jumpInsnOpcode, startOfRealImplementation);
        mw.visitInsn(RETURN);
        mw.visitLabel(startOfRealImplementation);
    }

    private void generateCallToFakeMethod() {
        if (shouldUseClassLoadingBridge()) {
            generateCallToFakeMethodThroughClassLoadingBridge();
        } else {
            generateDirectCallToFakeMethod();
        }
    }

    private boolean shouldUseClassLoadingBridge() {
        return useClassLoadingBridge || !fakeMethod.isPublic();
    }

    private void generateCallToFakeMethodThroughClassLoadingBridge() {
        generateCodeToObtainInstanceOfClassLoadingBridge(FakeMethodBridge.MB);

        // First and second "invoke" arguments:
        boolean isStatic = generateCodeToPassThisOrNullIfStaticMethod();
        mw.visitInsn(ACONST_NULL);

        // Create array for call arguments (third "invoke" argument):
        JavaType[] argTypes = JavaType.getArgumentTypes(methodDesc);
        generateCodeToCreateArrayOfObject(6 + argTypes.length);

        int i = 0;
        generateCodeToFillArrayElement(i, fakeMethods.getFakeClassInternalName());
        i++;
        generateCodeToFillArrayElement(i, classDesc);
        i++;
        generateCodeToFillArrayElement(i, methodAccess);
        i++;

        if (fakeMethod.hasInvocationParameterOnly()) {
            generateCodeToFillArrayElement(i, methodName);
            i++;
            generateCodeToFillArrayElement(i, methodDesc);
        } else {
            generateCodeToFillArrayElement(i, fakeMethod.name);
            i++;
            generateCodeToFillArrayElement(i, fakeMethod.desc);
        }
        i++;

        generateCodeToFillArrayElement(i, fakeMethod.getIndexForFakeState());
        i++;

        generateCodeToFillArrayWithParameterValues(argTypes, i, isStatic ? 0 : 1);
        generateCallToInvocationHandler();
    }

    private void generateDirectCallToFakeMethod() {
        String fakeClassDesc = fakeMethods.getFakeClassInternalName();
        int invokeOpcode;

        if (fakeMethod.isStatic()) {
            invokeOpcode = INVOKESTATIC;
        } else {
            generateCodeToObtainFakeInstance(fakeClassDesc);
            invokeOpcode = INVOKEVIRTUAL;
        }

        boolean canProceedIntoConstructor = generateArgumentsForFakeMethodInvocation();
        mw.visitMethodInsn(invokeOpcode, fakeClassDesc, fakeMethod.name, fakeMethod.desc, false);

        if (canProceedIntoConstructor) {
            mw.visitMethodInsn(INVOKEVIRTUAL, "mockit/internal/faking/FakeInvocation", "shouldProceedIntoConstructor",
                    "()Z", false);
        }
    }

    private void generateCodeToObtainFakeInstance(@Nonnull String fakeClassDesc) {
        mw.visitLdcInsn(fakeClassDesc);
        generateCodeToPassThisOrNullIfStaticMethod();
        mw.visitMethodInsn(INVOKESTATIC, "mockit/internal/state/TestRun", "getFake",
                "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", false);
        mw.visitTypeInsn(CHECKCAST, fakeClassDesc);
    }

    private boolean generateArgumentsForFakeMethodInvocation() {
        String fakedDesc = fakeMethod.hasInvocationParameterOnly() ? methodDesc
                : fakeMethod.fakeDescWithoutInvocationParameter;
        JavaType[] argTypes = JavaType.getArgumentTypes(fakedDesc);
        int varIndex = isStatic(methodAccess) ? 0 : 1;
        boolean canProceedIntoConstructor = false;

        if (fakeMethod.hasInvocationParameter()) {
            generateCallToCreateNewFakeInvocation(argTypes, varIndex);

            // When invoking a constructor, the invocation object will need to be consulted for proceeding:
            if (isConstructor) {
                mw.visitInsn(fakeMethod.isStatic() ? DUP : DUP_X1);
                canProceedIntoConstructor = true;
            }
        }

        if (!fakeMethod.hasInvocationParameterOnly()) {
            passArgumentsForFakeMethodCall(argTypes, varIndex);
        }

        return canProceedIntoConstructor;
    }

    private void generateCallToCreateNewFakeInvocation(@Nonnull JavaType[] argTypes,
            @Nonnegative int initialParameterIndex) {
        generateCodeToPassThisOrNullIfStaticMethod();

        int argCount = argTypes.length;

        if (argCount == 0) {
            mw.visitInsn(ACONST_NULL);
        } else {
            generateCodeToCreateArrayOfObject(argCount);
            generateCodeToFillArrayWithParameterValues(argTypes, 0, initialParameterIndex);
        }

        mw.visitLdcInsn(fakeMethods.getFakeClassInternalName());
        mw.visitIntInsn(SIPUSH, fakeMethod.getIndexForFakeState());
        mw.visitLdcInsn(classDesc);
        mw.visitLdcInsn(methodName);
        mw.visitLdcInsn(methodDesc);

        mw.visitMethodInsn(INVOKESTATIC, "mockit/internal/faking/FakeInvocation", "create",
                "(Ljava/lang/Object;[Ljava/lang/Object;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)"
                        + "Lmockit/internal/faking/FakeInvocation;",
                false);
    }

    private void passArgumentsForFakeMethodCall(@Nonnull JavaType[] argTypes, @Nonnegative int varIndex) {
        boolean forGenericMethod = fakeMethod.isForGenericMethod();

        for (JavaType argType : argTypes) {
            int opcode = argType.getOpcode(ILOAD);
            mw.visitVarInsn(opcode, varIndex);

            if (forGenericMethod && argType instanceof ReferenceType) {
                String typeDesc = ((ReferenceType) argType).getInternalName();
                mw.visitTypeInsn(CHECKCAST, typeDesc);
            }

            varIndex += argType.getSize();
        }
    }

    private void generateMethodReturn() {
        if (shouldUseClassLoadingBridge() || fakeMethod.isAdvice) {
            generateReturnWithObjectAtTopOfTheStack(methodDesc);
        } else {
            JavaType returnType = JavaType.getReturnType(methodDesc);
            mw.visitInsn(returnType.getOpcode(IRETURN));
        }
    }
}
