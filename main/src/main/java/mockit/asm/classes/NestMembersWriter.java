package mockit.asm.classes;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import mockit.asm.constantPool.AttributeWriter;
import mockit.asm.constantPool.ConstantPoolGeneration;
import mockit.asm.util.ByteVector;

final class NestMembersWriter extends AttributeWriter {
    @Nonnegative
    private final int[] memberClassNameIndices;

    NestMembersWriter(@Nonnull ConstantPoolGeneration cp, @Nonnull String[] memberClassNames) {
        super(cp, "NestMembers");

        memberClassNameIndices = new int[memberClassNames.length];

        for (int i = 0; i < memberClassNames.length; i++) {
            memberClassNameIndices[i] = cp.newClass(memberClassNames[i]);
        }
    }

    @Nonnegative
    @Override
    public int getSize() {
        return 8 + 2 * memberClassNameIndices.length;
    }

    @Override
    public void put(@Nonnull ByteVector out) {
        int numberOfMembers = memberClassNameIndices.length;
        put(out, 2 + 2 * numberOfMembers);
        out.putShort(numberOfMembers);

        for (int memberClassNameIndex : memberClassNameIndices) {
            out.putShort(memberClassNameIndex);
        }
    }
}
