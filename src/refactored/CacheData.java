package refactored;

import java.io.RandomAccessFile;

public class CacheData {
    int lastTime = (int) (System.currentTimeMillis() / 1000L);
    int saveTime = lastTime + 2;
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
