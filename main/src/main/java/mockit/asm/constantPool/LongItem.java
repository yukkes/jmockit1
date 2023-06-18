package mockit.asm.constantPool;

import static mockit.asm.jvmConstants.ConstantPoolTypes.LONG;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class LongItem extends LongValueItem {
    public LongItem(@Nonnegative int index) {
        super(index);
        type = LONG;
    }

    LongItem(@Nonnegative int index, @Nonnull LongItem item) {
        super(index, item);
    }
}
