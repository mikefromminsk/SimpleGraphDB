package refactored;

import java.util.HashMap;
import java.util.Map;

public class InfinityArray extends InfinityFile {
    // TODO garbage collector

    private InfinityConstArray meta;
    public Map<Long, InfinityConstArray> garbageCollector = new HashMap<>();


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

    public byte[] get(long index) {
        MetaNode metaNode = new MetaNode();
        meta.get(index, metaNode);
        return read(metaNode.start, metaNode.length);
    }

    public String getString(long index) {
        return new String(get(index));
    }

    public void set(long index, byte[] data) {
        MetaNode metaNode = new MetaNode();
        meta.get(index, metaNode);
        int lastSectorLength = getSectorLength((int) metaNode.length);
        int newSectorLength = getSectorLength(data.length);
        if (newSectorLength > lastSectorLength) {
            MetaNode garbage = getGarbage(newSectorLength);
            addToGarbage(index, lastSectorLength);
            byte[] sectorWithData = new byte[newSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
            if (garbage == null) {
                metaNode.start = super.add(sectorWithData);
                metaNode.length = data.length;
                meta.set(index, metaNode);
            } else {
                write(garbage.start, sectorWithData);
                garbage.length = data.length;
                meta.set(index, garbage);
            }
        } else {
            byte[] result = new byte[lastSectorLength];
            System.arraycopy(data, 0, result, 0, data.length);
            write(metaNode.start, result);
        }
    }

    public void set(long index, String data) {
        set(index, data.getBytes());
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
            MetaNode metaNode = new MetaNode();
            long lastGarbageIndex = garbageBySize.getLong(0);
            if (lastGarbageIndex > 1) {
                long metaIndex = garbageBySize.getLong(lastGarbageIndex);
                meta.get(metaIndex, metaNode);
                garbageBySize.set(0, lastGarbageIndex - 1);
                return metaNode;
            }
        }
        return null;
    }

    public long add(byte[] data) {
        MetaNode metaNode = new MetaNode();
        byte[] sector = dataToSector(data);
        metaNode.start = super.add(sector);
        metaNode.length = data.length;
        return meta.add(metaNode);
    }

    public long add(String data) {
        return add(data.getBytes());
    }

    int getSectorLength(int dataLength) {
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
