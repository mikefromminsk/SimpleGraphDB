package refactored.tree;

import refactored.Bytes;

import java.util.ArrayList;
import java.util.Arrays;

public class HashVariants {
    byte[] mask;
    ArrayList<Hash> hashs;

    public HashVariants(byte[] data) {
        this.mask = Arrays.copyOfRange(data, 0, HashTree.MASK_SIZE - 1);
        int hashVariantsCount = (data.length - HashTree.MASK_SIZE) / Hash.SIZE;
        for (int i = 0; i < hashVariantsCount; i++) {
            int startHashData = HashTree.MASK_SIZE + i * Hash.SIZE - 1;
            long[] hashData = Bytes.toLongArray(Arrays.copyOfRange(data, startHashData, startHashData + Hash.SIZE));
            hashs.add(new Hash(hashData[0], hashData[1], hashData[2]));
        }
    }

    byte[] toBytes() {
        byte[] data = new byte[HashTree.MASK_SIZE + hashs.size() * Hash.SIZE];
        System.arraycopy(mask, 0, data, 0, HashTree.MASK_SIZE);
        for (int i = 0; i < hashs.size(); i++)
            System.arraycopy(hashs.get(i).getBytes(), 0, data, HashTree.MASK_SIZE + i * Hash.SIZE - 1, Hash.SIZE);
        return data;
    }
}
