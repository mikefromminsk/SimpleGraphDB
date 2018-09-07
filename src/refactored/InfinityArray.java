package refactored;

import java.nio.ByteBuffer;

public class InfinityArray extends InfinityFile {

    InfinityConstArray meta;
    // TODO garbage collector

    public InfinityArray(String infinityFileID) {
        super(infinityFileID);
        meta = new InfinityConstArray(infinityFileID + ".meta", 16);
    }

    byte[] get(int index) {
        long[] metaOfIndex = Bytes.toLongArray(meta.get(index));
        long start = metaOfIndex[0];
        long length = metaOfIndex[1];
        return read(start, length);
    }

    void set(int index, byte[] data) {
        if (data != null && data.length != 0) {
            long[] metaOfIndex = Bytes.toLongArray(meta.get(index));
            long start = metaOfIndex[0];
            long length = metaOfIndex[1];
            // TODO increase data to level of 2
            write(start, data);
        }
    }

    void add(byte[] data) {
        if (data != null && data.length != 0) {
            // TODO increase data to level of 2
            long[] newMetaData = new long[]{settings.sumFilesSize, data.length};
            meta.add(Bytes.fromLongArray(newMetaData));
            super.add(data);
        }
    }

}
