package refactored;

public class MetaNode implements InfinityConstArrayCell {

    public long start;
    public long length;

    @Override
    public void setData(byte[] data) {
        long[] metaOfIndex = Bytes.toLongArray(data);
        start = metaOfIndex[0];
        length = metaOfIndex[1];
    }

    @Override
    public byte[] getBytes() {
        long[] data = new long[2];
        data[0] = start;
        data[1] = length;
        return Bytes.fromLongArray(data);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}