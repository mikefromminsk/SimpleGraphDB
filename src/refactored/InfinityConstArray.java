package refactored;

public class InfinityConstArray extends InfinityFile {

    private final long cellSize;

    public InfinityConstArray(String infinityFileID, long cellSize) {
        super(infinityFileID);
        this.cellSize = cellSize;
    }

    protected byte[] get(long index) {
        return read(index * cellSize, cellSize);
    }

    protected void set(int index, byte[] obj) {
        if (obj != null && obj.length == cellSize)
            write(index * cellSize, obj);
    }

    protected void add(byte[] obj) {
        if (obj != null && obj.length == cellSize)
            super.add(obj);
    }
}
