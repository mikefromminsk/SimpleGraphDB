package refactored.tree;

import refactored.Bytes;
import refactored.InfinityArray;
import refactored.InfinityConstArray;

public class Tree extends InfinityConstArray {
    // TODO delete new and use only 3 objects to search
    // TODO HALF_LONG value change to max tree node count
    private final static long HALF_LONG = Long.MAX_VALUE / 2L;

    private InfinityArray keys;
    private InfinityArray hashes;

    Tree(String infinityFileID) {
        super(infinityFileID);
        keys = new InfinityArray(infinityFileID + ".keys");
        hashes = new InfinityArray(infinityFileID + ".hashes");
        if (fileData.sumFilesSize == 0)
            add(new TreeNode("****".getBytes(), new long[TreeNode.LINKS_COUNT]));
    }

    void put(String str, byte[] hash, long value) {
        TreeNode node = new TreeNode();
        long prevIndex = Long.MAX_VALUE;
        long nodeIndex = 0;
        int i = 0;
        int maskLength = TreeNode.SIZE;
        while (i < maskLength) {
            get(nodeIndex, node);
            byte hashChar = hash[i];
            byte nodeChar = node.mask[i];
            while (nodeChar == hashChar && i + 1 < maskLength) {
                i++;
                hashChar = hash[i];
                nodeChar = node.mask[i];
            }
            if (nodeChar == '*') {
                long link = node.links[hashChar];
                if (link < HALF_LONG) {
                    nodeIndex = link;
                } else if (link == 0) {
                    node.links[hashChar] = hashes.add(new HashVariants(hash, newHash(str, value)));
                    set(nodeIndex, node);
                    return;
                } else /* hashIndex >= HALF_LONG */ {
                    long hashVariantIndex = link - HALF_LONG;
                    HashVariants hashVariants = new HashVariants();
                    hashes.get(hashVariantIndex, hashVariants);
                    long first8Bytes = Bytes.toLong(str.substring(0, 7).getBytes());
                    boolean findKey = false;
                    for (Hash hashl : hashVariants.hashes)
                        if (hashl.first8Bytes == first8Bytes)
                            if (str.equals(keys.getString(hashl.keyIndex))) {
                                hashl.value = value;
                                findKey = true;
                                break;
                            }
                    if (!findKey) {
                        long keyIndex = keys.add(str);
                        Hash newHash = new Hash(first8Bytes, keyIndex, value);
                        hashVariants.hashes.add(newHash);
                    }
                    hashes.set(hashVariantIndex, hashVariants);
                    return;
                }
            } else {
                byte[] newMask = "****".getBytes();
                System.arraycopy(node.mask, 0, newMask, 0, i);
                long[] links = new long[TreeNode.LINKS_COUNT];
                links[nodeChar] = nodeIndex;
                links[hashChar] = hashes.add(new HashVariants(hash, newHash(str, value))) + HALF_LONG;
                TreeNode newNode = new TreeNode(newMask, links);
                long newIndex = add(newNode);
                if (prevIndex != Long.MAX_VALUE) {
                    get(prevIndex, node);
                    node.links[i - 1] = newIndex;
                    set(prevIndex, node);
                }
                return;
            }
            prevIndex = nodeIndex;
        }
    }

    Hash newHash(String key, long value){
        long first8Bytes = Bytes.toLong(key.substring(0, 7).getBytes());
        long keyIndex = keys.add(key);
        return new Hash(first8Bytes, keyIndex, value);
    }

}
