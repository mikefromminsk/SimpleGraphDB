package refactored;

public class InfinityConstArray extends InfinityFile {

    private InfinityConstArrayCell cell;

    public InfinityConstArray(String infinityFileID, InfinityConstArrayCell cell) {
        super(infinityFileID);
        this.cell = cell;
    }

    public void get(long index, InfinityConstArrayCell dest) {
        byte[] readiedData = read(index * cell.getSize(), cell.getSize());
        dest.setData(readiedData);
    }

    public void set(int index, InfinityConstArrayCell obj) {
        write(index * cell.getSize(), obj.getBytes());
    }

    public long add(InfinityConstArrayCell obj) {
        long lastMaxPosition = super.add(obj.getBytes());
        return  lastMaxPosition / obj.getSize();
    }
}
