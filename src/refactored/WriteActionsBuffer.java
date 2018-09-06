package refactored;

import java.io.RandomAccessFile;

public class WriteActionsBuffer{
    RandomAccessFile file;
    int offset;
    byte[] data;

    public WriteActionsBuffer(RandomAccessFile file, int offset, byte[] data) {
        this.file = file;
        this.offset = offset;
        this.data = data;
    }
}
