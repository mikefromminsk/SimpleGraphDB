package refactored;

public class LongCell implements InfinityArrayCell {

    long value = 0;

    @Override
    public void setData(byte[] data) {
        value = Bytes.toLong(data);
    }

    public void setData(long data) {
        value = data;
    }

    @Override
    public byte[] getBytes() {
        return Bytes.fromLong(value);
    }

    @Override
    public int getSize() {
        return Long.BYTES;
    }
}
