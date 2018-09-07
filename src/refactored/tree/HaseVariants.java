package refactored.tree;

import refactored.Bytes;

import java.util.ArrayList;
import java.util.Arrays;

public class HaseVariants {
    String mask;
    ArrayList<Hash> links;

    public HaseVariants(byte[] data) {
        this.mask = new String(Arrays.copyOfRange(data, 0, HashTree.MASK_SIZE - 1));
        int hashVariantsCount = (data.length - HashTree.MASK_SIZE) / Hash.SIZE;
        for (int i = 0; i < hashVariantsCount; i++) {
            int startHashData = HashTree.MASK_SIZE + i * Hash.SIZE - 1;
            long[] hashData = Bytes.toLongArray(Arrays.copyOfRange(data, startHashData, startHashData + Hash.SIZE));
            links.add(new Hash(hashData[0], hashData[1], hashData[2]));
        }
    }

    byte[] toBytes() {
        byte[] data = new byte[HashTree.MASK_SIZE + links.size() * Hash.SIZE];
        System.arraycopy(mask.getBytes(), 0, data, 0, HashTree.MASK_SIZE);
        for (int i = 0; i < links.size(); i++)
            System.arraycopy(links.get(i).getBytes(), 0, data, HashTree.MASK_SIZE + i * Hash.SIZE - 1, Hash.SIZE);
        return data;
    }
}
