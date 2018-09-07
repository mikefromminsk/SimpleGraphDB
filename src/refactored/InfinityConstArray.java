package refactored;

public class InfinityConstArray extends InfinityFile {

    private final long cellSize;

    public InfinityConstArray(String infinityFileID, long cellSize) {
        super(infinityFileID);
        this.cellSize = cellSize;
    }

    public byte[] get(long index) {
        return read(index * cellSize, cellSize);
    }

    public void set(int index, byte[] obj) {
        write(index * cellSize, obj);
    }

    public long add(byte[] obj) {
        return super.add(obj) / cellSize;
    }
}
