package refactored;


public class StringCellConst implements InfinityConstArrayCell {

    public String str;

    @Override
    public void parse(byte[] data) {
        str = new String(data);
    }

    @Override
    public byte[] build() {
        return str.getBytes();
    }

    @Override
    public int getSize() {
        return str.length();
    }
}