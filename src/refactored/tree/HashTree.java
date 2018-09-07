package refactored.tree;

import refactored.Bytes;
import refactored.InfinityArray;
import refactored.InfinityConstArray;

public class HashTree extends InfinityConstArray {

    InfinityArray hashKeys;
    InfinityArray hashes;
    public final static int MASK_SIZE = 4;
    public final static int LINKS_SIZE = MASK_SIZE * MASK_SIZE * Long.BYTES;
    public final static long HALF_LONG = Long.MAX_VALUE / 2L;
    TreeNode root;

    HashTree(String infinityFileID) {
        super(infinityFileID, TreeNode.SIZE);
        hashKeys = new InfinityArray(infinityFileID + ".keys");
        hashes = new InfinityArray(infinityFileID + ".hashes");
        if (settings.sumFilesSize == 0) {
            root = new TreeNode("****".getBytes(), new long[HashTree.LINKS_SIZE]);
            add(root.getBytes());
        } else {
            root = new TreeNode(get(0));
        }
    }

    void put(String str, long value) {
        byte[] hash = CRC16.getHash(str);
        // TODO delete new and use only 3 objects to search
        TreeNode prevNode = null;
        long prevLink = 0;
        TreeNode node = root;
        long link = root.links[hash[0]];
        int i = 0;
        for (; i < MASK_SIZE && link != 0 || link < HALF_LONG; i++) {
            prevNode = node;
            prevLink = link;
            node = new TreeNode(get(link));
            link = node.links[hash[i]];
        }
        if (link == 0) {
            byte[] first8Bytes = new byte[Long.BYTES];
            System.arraycopy(str.getBytes(), 0, first8Bytes, 0, Math.min(str.length(), Long.BYTES));
            long keyIndex = hashKeys.add(str.getBytes());
            Hash newHash = new Hash(Bytes.toLong(first8Bytes), keyIndex, value);
            long hashIndex = hashes.add(newHash.getBytes());
            node.setLink(this, link, hash[i], hashIndex + HALF_LONG);
        } else {

        }

    }

    long get(String str) {
        return 0;
    }
}
