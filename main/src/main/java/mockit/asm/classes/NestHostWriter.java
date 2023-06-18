package mockit.asm.classes;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import mockit.asm.constantPool.AttributeWriter;
import mockit.asm.constantPool.ConstantPoolGeneration;
import mockit.asm.util.ByteVector;

final class NestHostWriter extends AttributeWriter {
    @Nonnegative
    private final int hostClassNameIndex;

    NestHostWriter(@Nonnull ConstantPoolGeneration cp, @Nonnull String hostClassName) {
        super(cp, "NestHost");
        hostClassNameIndex = cp.newClass(hostClassName);
    }

    @Nonnegative
    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public void put(@Nonnull ByteVector out) {
        super.put(out);
        out.putShort(hostClassNameIndex);
    }
}
