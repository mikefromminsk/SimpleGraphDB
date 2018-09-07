package refactored;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class Bytes {

    public static byte[] fromInt(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int toInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] fromLong(int value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    public static long toLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] fromChar(char value) {
        byte[] result = new byte[2];
        result[0] = (byte) ((value & 0xFF00) >> 8);
        result[1] = (byte) (value & 0x00FF);
        return result;
    }

    public static char toChar(byte[] bytes) {
        return (char) (((bytes[0] & 0x00FF) << 8) + (bytes[1] & 0x00FF));
    }

    public static int[] toIntArray(byte[] bytes) {
        IntBuffer intBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        return new int[intBuf.remaining()];
    }

    public static byte[] fromIntArray(int[] value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(value.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(value);
        return byteBuffer.array();
    }

    public static long[] toLongArray(byte[] bytes) {
        LongBuffer intBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asLongBuffer();
        return new long[intBuf.remaining()];
    }

    public static byte[] fromLongArray(long[] value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(value.length * 8);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        longBuffer.put(value);
        return byteBuffer.array();
    }

    public static byte[] fromString(String mask) {
        return mask.getBytes();
    }
}
