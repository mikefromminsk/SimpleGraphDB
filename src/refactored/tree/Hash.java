package refactored.tree;

import refactored.Bytes;
import refactored.InfinityArrayCell;

public class Hash implements InfinityArrayCell {

    public final static int SIZE = 3 * Long.BYTES;

    long first8Bytes;
    long keyIndex;
    long value;

    public Hash(long first8Bytes, long keyIndex, long value) {
        this.first8Bytes = first8Bytes;
        this.keyIndex = keyIndex;
        this.value = value;
    }

    @Override
    public void setData(byte[] data) {
        long[] array = Bytes.toLongArray(data);
        first8Bytes = array[0];
        keyIndex = array[1];
        value = array[2];
    }

    public byte[] getBytes() {
        long[] data = new long[3];
        data[0] = first8Bytes;
        data[1] = keyIndex;
        data[2] = value;
        return Bytes.fromLongArray(data);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
