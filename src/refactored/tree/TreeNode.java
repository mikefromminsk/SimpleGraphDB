package refactored.tree;

import refactored.Bytes;
import refactored.InfinityArrayCell;

import java.util.Arrays;

public class TreeNode implements InfinityArrayCell {


    public final static int MASK_SIZE = 4;
    public final static int LINKS_COUNT = MASK_SIZE * MASK_SIZE;
    public final static int LINKS_SIZE = LINKS_COUNT * Long.BYTES;
    public final static int SIZE = MASK_SIZE + LINKS_SIZE;

    byte[] mask;
    long[] links;

    public TreeNode() {
    }

    public TreeNode(byte[] data) {
        setData(data);
    }

    public TreeNode(byte[] mask, long[] links) {
        this.mask = mask;
        this.links = links;
    }

    @Override
    public void setData(byte[] data) {
        this.mask = Arrays.copyOfRange(data, 0, MASK_SIZE - 1);
        this.links = Bytes.toLongArray(Arrays.copyOfRange(data, MASK_SIZE, SIZE - 1));
    }

    @Override
    public byte[] getBytes() {
        byte[] data = new byte[MASK_SIZE + links.length * Long.BYTES];
        System.arraycopy(mask, 0, data, 0, MASK_SIZE);
        System.arraycopy(Bytes.fromLongArray(links), 0, data, MASK_SIZE - 1, LINKS_SIZE);
        return data;
    }

    @Override
    public int getSize() {
        return SIZE;
    }

}
