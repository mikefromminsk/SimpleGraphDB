package refactored;

import java.nio.ByteBuffer;

public class InfinityArray extends InfinityFile {

    InfinityConstArray meta;
    public final static int META_SIZE = Long.SIZE * 2;
    // TODO garbage collector

    public InfinityArray(String infinityFileID) {
        super(infinityFileID);
        meta = new InfinityConstArray(infinityFileID + ".meta", META_SIZE);
    }

    public byte[] get(int index) {
        long[] metaOfIndex = Bytes.toLongArray(meta.get(index));
        long start = metaOfIndex[0];
        long length = metaOfIndex[1];
        return read(start, length);
    }

    public void set(int index, byte[] data) {
        long[] metaOfIndex = Bytes.toLongArray(meta.get(index));
        long start = metaOfIndex[0];
        long length = metaOfIndex[1];
        // TODO increase data to level of 2
        write(start, data);
    }

    public long add(byte[] data) {
        // TODO increase data to level of 2
        long[] newMetaData = new long[]{settings.sumFilesSize, data.length};
        long metaIndex = meta.add(Bytes.fromLongArray(newMetaData));
        super.add(data);
        return metaIndex;
    }

}
