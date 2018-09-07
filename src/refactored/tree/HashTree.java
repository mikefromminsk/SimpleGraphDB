package refactored.tree;

import refactored.InfinityArray;
import refactored.InfinityConstArray;

public class HashTree extends InfinityConstArray {

    InfinityArray hashValues;
    public final static int MASK_SIZE = 4;
    public final static int LINKS_SIZE = MASK_SIZE * MASK_SIZE * Long.BYTES;
    public final static long HALF_LONG = Long.MAX_VALUE / 2L;
    TreeNode root;

    HashTree(String infinityFileID) {
        super(infinityFileID, TreeNode.SIZE);
        hashValues = new InfinityArray(infinityFileID + ".values");
        if (settings.sumFilesSize == 0) {
            root = new TreeNode("****".getBytes(), new long[HashTree.LINKS_SIZE]);
            add(root.getBytes());
        }else{
            root = new TreeNode(get(0));
        }
    }



    void put(String str) {
        byte[] hash = CRC16.getHash(str);
        TreeNode node = root;
        long link = root.links[hash[0]];
        byte[] prevMask = root.mask;
        while(link != 0 || link >= HALF_LONG){

        }
        if (link == 0){

        }else if (link >= HALF_LONG){

        }

    }

    long get(String str) {
        return 0;
    }
}
