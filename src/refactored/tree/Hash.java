package refactored.tree;

import refactored.Bytes;

public class Hash {
    public final static int SIZE = 8 * 3;
    long first8Bytes;
    long key;
    long value;

    public Hash(long first8Bytes, long key, long value) {
        this.first8Bytes = first8Bytes;
        this.key = key;
        this.value = value;
    }

    public byte[] getBytes() {
        long[] data = new long[3];
        data[0] = first8Bytes;
        data[1] = key;
        data[2] = value;
        return Bytes.fromLongArray(data);
    }
}
