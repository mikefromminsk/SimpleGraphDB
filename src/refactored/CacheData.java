package refactored;

import java.io.RandomAccessFile;

public class CacheData {
    boolean isUpdated;
    RandomAccessFile file;
    int offset;
    byte[] data;

    public CacheData(boolean isUpdated, RandomAccessFile file, int offset, byte[] data) {
        this.isUpdated = isUpdated;
        this.file = file;
        this.offset = offset;
        this.data = data;
    }
}
