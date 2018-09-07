package refactored.tree;

import refactored.Bytes;

public class Hash {
    public final static int SIZE = 8 * 3;
    long first8Bytes;
    long keyIndex;
    long value;

    public Hash(long first8Bytes, long keyIndex, long value) {
        this.first8Bytes = first8Bytes;
        this.keyIndex = keyIndex;
        this.value = value;
    }

    public byte[] getBytes() {
        long[] data = new long[3];
        data[0] = first8Bytes;
        data[1] = keyIndex;
        data[2] = value;
        return Bytes.fromLongArray(data);
    }
}
