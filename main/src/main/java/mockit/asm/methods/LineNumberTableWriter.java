package mockit.asm.methods;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.asm.constantPool.AttributeWriter;
import mockit.asm.constantPool.ConstantPoolGeneration;
import mockit.asm.controlFlow.Label;
import mockit.asm.util.ByteVector;

/**
 * Writes the bytecode for the "LineNumberTable" method code attribute.
 */
final class LineNumberTableWriter extends AttributeWriter {
    /**
     * Number of entries in the <code>LineNumberTable</code> attribute.
     */
    @Nonnegative
    private int lineNumberCount;

    /**
     * The <code>LineNumberTable</code> attribute.
     */
    @Nullable
    private ByteVector lineNumbers;

    LineNumberTableWriter(@Nonnull ConstantPoolGeneration cp) {
        super(cp);
    }

    void addLineNumber(@Nonnegative int line, @Nonnull Label start) {
        if (lineNumbers == null) {
            setAttribute("LineNumberTable");
            lineNumbers = new ByteVector();
        }

        lineNumberCount++;
        lineNumbers.putShort(start.position);
        lineNumbers.putShort(line);
    }

    boolean hasLineNumbers() {
        return lineNumbers != null;
    }

    @Nonnegative
    @Override
    public int getSize() {
        return lineNumbers == null ? 0 : 8 + lineNumbers.getLength();
    }

    @Override
    public void put(@Nonnull ByteVector out) {
        if (lineNumbers != null) {
            put(out, 2 + lineNumbers.getLength());
            out.putShort(lineNumberCount);
            out.putByteVector(lineNumbers);
        }
    }
}
