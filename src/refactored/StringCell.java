package refactored;


public class StringCell implements InfinityArrayCell {

    public String str;

    @Override
    public void setData(byte[] data) {
        str = new String(data);
    }

    @Override
    public byte[] getBytes() {
        return str.getBytes();
    }

    @Override
    public int getSize() {
        return str.length();
    }
}