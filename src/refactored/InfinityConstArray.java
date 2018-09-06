package refactored;

public class InfinityConstArray extends InfinityFile {

    private final long cellSize;

    InfinityConstArray(String infinityFileID, long cellSize) {
        super(infinityFileID);
        this.cellSize = cellSize;
    }

    byte[] get(long index) {
        return read(index * cellSize, cellSize);
    }

    void set(int index, byte[] obj) {
        if (obj != null && obj.length == cellSize)
            write(index * cellSize, obj);
    }

    void add(byte[] obj) {
        if (obj != null && obj.length == cellSize)
            super.add(obj);
    }
}
