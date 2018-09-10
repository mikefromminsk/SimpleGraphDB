package refactored;

public class InfinityConstArray extends InfinityFile {

    public InfinityConstArray(String infinityFileID) {
        super(infinityFileID);
    }

    public void get(long index, InfinityConstArrayCell dest) {
        byte[] readiedData = read(index * dest.getSize(), dest.getSize());
        dest.setData(readiedData);
    }

    LongCell longCell = new LongCell();
    public long getLong(long index) {
        get(index, longCell);
        return longCell.value;
    }

    public void set(long index, InfinityConstArrayCell obj) {
        write(index * obj.getSize(), obj.getBytes());
    }


    public void set(long index, long value) {
        longCell.setData(value);
        set(index, longCell);
    }

    public long add(InfinityConstArrayCell obj) {
        long lastMaxPosition = super.add(obj.getBytes());
        return  lastMaxPosition / obj.getSize();
    }

    public long add(long value) {
        longCell.value = value;
        long lastMaxPosition = super.add(longCell.getBytes());
        return  lastMaxPosition / longCell.getSize();
    }
}
