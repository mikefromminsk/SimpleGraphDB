package refactored;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InfinityArray extends InfinityFile {

    // TODO add secure

    private InfinityConstArray meta;
    Map<Long, InfinityConstArray> garbageCollector = new HashMap<>();


    public InfinityArray(String infinityFileID) {
        super(infinityFileID);
        meta = new InfinityConstArray(infinityFileID + ".meta");
        Map<String, String> garbage = DiskManager.getInstance().properties.getSection(infinityFileID + ".garbage");
        if (garbage != null)
            for (String key : garbage.keySet()) {
                Long sectorSize = Long.valueOf(key);
                InfinityConstArray garbageBySize = new InfinityConstArray(garbage.get(key));
                garbageCollector.put(sectorSize, garbageBySize);
            }
    }

    private MetaNode getMetaNode(long index) {
        MetaNode metaNode = new MetaNode();
        meta.get(index, metaNode);
        return metaNode;
    }

    public InfinityArrayCell get(long index, InfinityArrayCell dest) {
        MetaNode metaNode = getMetaNode(index);
        byte[] readiedData = read(metaNode.start, metaNode.length);
        decodeData(readiedData, metaNode.accessKey);
        dest.parse(readiedData);
        return dest;
    }

    private StringCellConst stringCell = new StringCellConst();
    public String getString(long index) {
        get(index, stringCell);
        return stringCell.str;
    }

    private static Random randomOfAccessKeys = new Random(System.currentTimeMillis());
    private static Random random = new Random();

    public static long encodeData(byte[] data) {
        long accessKey = randomOfAccessKeys.nextLong();
        random.setSeed(accessKey);
        byte[] gamma = new byte[data.length];
        random.nextBytes(gamma);
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) ((data[i] + gamma[i]) % 256);
        return accessKey;
    }

    public static void decodeData(byte[] data, long accessKey){
        random.setSeed(accessKey);
        byte[] gamma = new byte[data.length];
        random.nextBytes(gamma);
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) ((256 + (data[i] - gamma[i])) % 256);
    }

    public void set(long index, byte[] data) {
        MetaNode metaNode = getMetaNode(index);
        int lastSectorLength = getSectorLength((int) metaNode.length);
        int newSectorLength = getSectorLength(data.length);
        if (newSectorLength > lastSectorLength) {
            MetaNode garbage = getGarbage(newSectorLength);
            addToGarbage(index, lastSectorLength);
            byte[] sectorWithData = new byte[newSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
            long newAccessKey = encodeData(sectorWithData);
            if (garbage == null) {
                metaNode.start = super.add(sectorWithData);
                metaNode.length = data.length;
                metaNode.accessKey = newAccessKey;
                meta.set(index, metaNode);
            } else {
                write(garbage.start, sectorWithData);
                garbage.length = data.length;
                garbage.accessKey = newAccessKey;
                meta.set(index, garbage);
            }
        } else {
            byte[] sectorWithData = new byte[lastSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
            metaNode.accessKey = encodeData(sectorWithData);
            meta.set(index, metaNode);
            write(metaNode.start, sectorWithData);
        }
    }

    public void set(long index, String data) {
        stringCell.str = data;
        set(index, stringCell);
    }

    public void set(long index, InfinityArrayCell cell) {
        set(index, cell.build());
    }

    public void addToGarbage(long index, long sectorSize) {
        InfinityConstArray garbageBySize = garbageCollector.get(sectorSize);
        if (garbageBySize == null) {
            String garbageName = infinityFileID + ".garbage";
            String garbageNameWithSize = garbageName + sectorSize;
            garbageBySize = new InfinityConstArray(garbageNameWithSize);
            garbageBySize.add(1);
            garbageBySize.add(index);
            garbageCollector.put(sectorSize, garbageBySize);
            DiskManager.getInstance().properties.put(garbageName, "" + sectorSize, garbageNameWithSize);
        } else {
            long lastContentIndex = garbageBySize.getLong(0);
            if (lastContentIndex < garbageBySize.fileData.sumFilesSize / Long.BYTES) {
                garbageBySize.set(lastContentIndex + 1, index);
            } else {
                garbageBySize.add(index);
            }
            garbageBySize.set(0, lastContentIndex + 1);
        }
    }

    public MetaNode getGarbage(long sectorSize) {
        InfinityConstArray garbageBySize = garbageCollector.get(sectorSize);
        if (garbageBySize != null) {
            long lastGarbageIndex = garbageBySize.getLong(0);
            if (lastGarbageIndex > 1) {
                garbageBySize.set(0, lastGarbageIndex - 1);
                return getMetaNode(garbageBySize.getLong(lastGarbageIndex));
            }
        }
        return null;
    }

    public long add(byte[] data) {
        MetaNode metaNode = new MetaNode();
        byte[] sector = dataToSector(data);
        long newAccessKey = encodeData(sector);
        metaNode.start = super.add(sector);
        metaNode.length = data.length;
        metaNode.accessKey = newAccessKey;
        return meta.add(metaNode);
    }

    public long add(String data) {
        return add(data.getBytes());
    }

    public long add(InfinityArrayCell cell) {
        return add(cell.build());
    }

    private int getSectorLength(int dataLength) {
        int sectorSize = 1;
        while (sectorSize < dataLength)
            sectorSize *= 2;
        return sectorSize;
    }

    byte[] dataToSector(byte[] data) {
        byte[] result = new byte[getSectorLength(data.length)];
        System.arraycopy(data, 0, result, 0, data.length);
        return result;
    }
}
