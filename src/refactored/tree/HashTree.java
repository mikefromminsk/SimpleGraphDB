package refactored.tree;

import refactored.InfinityArray;
import refactored.InfinityConstArray;

public class HashTree extends InfinityConstArray {

    InfinityArray hashValues;
    public final static int MASK_SIZE = 4;
    public final static int LINKS_SIZE = MASK_SIZE * MASK_SIZE * Long.BYTES;

    HashTree(String infinityFileID) {
        super(infinityFileID, TreeNode.SIZE);
        hashValues = new InfinityArray(infinityFileID + ".values");
    }

    long get(String str) {
        return 0;
    }

    void put(String str) {

    }
}
