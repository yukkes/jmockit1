package mockit.asm;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import mockit.asm.constantPool.AttributeWriter;
import mockit.asm.constantPool.ConstantPoolGeneration;
import mockit.asm.util.ByteVector;

public final class SignatureWriter extends AttributeWriter {
    @Nonnegative
    private final int signatureIndex;

    public SignatureWriter(@Nonnull ConstantPoolGeneration cp, @Nonnull String signature) {
        super(cp, "Signature");
        signatureIndex = cp.newUTF8(signature);
    }

    @Nonnegative
    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public void put(@Nonnull ByteVector out) {
        super.put(out);
        out.putShort(signatureIndex);
    }
}
