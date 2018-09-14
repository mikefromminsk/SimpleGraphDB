package refactored;

public class MetaNode implements InfinityArrayCell {

    public long start;
    public long length;
    public long accessKey;

    @Override
    public void setData(byte[] data) {
        long[] metaOfIndex = Bytes.toLongArray(data);
        start = metaOfIndex[0];
        length = metaOfIndex[1];
        accessKey = metaOfIndex[2];
    }

    @Override
    public byte[] getBytes() {
        long[] data = new long[2];
        data[0] = start;
        data[1] = length;
        data[2] = accessKey;
        return Bytes.fromLongArray(data);
    }

    @Override
    public int getSize() {
        return 3 * Long.BYTES;
    }
}