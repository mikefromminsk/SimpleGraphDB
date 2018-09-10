package refactored;

import java.util.HashMap;
import java.util.Map;

public class InfinityArray extends InfinityFile {
    // TODO garbage collector

    private InfinityConstArray meta;
    private Map<Long, InfinityConstArray> garbageCollector = new HashMap<>();


    public InfinityArray(String infinityFileID) {
        super(infinityFileID);
        meta = new InfinityConstArray(infinityFileID + ".meta", new MetaNode());
        Map<String, String> garbage = DiskManager.getInstance().properties.getSection(infinityFileID + ".garbage");
        for (String key : garbage.keySet()) {
            Long sectorSize = Long.valueOf(key);
            InfinityConstArray garbageBySize = new InfinityConstArray(garbage.get(key), new MetaNode());
            garbageCollector.put(sectorSize, garbageBySize);
        }
    }

    public byte[] get(long index) {
        MetaNode metaNode = new MetaNode();
        meta.get(index, metaNode);
        byte[] data = read(metaNode.start, metaNode.length);
        return data;
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

    private void addToGarbage(long index, long sectorSize) {
        InfinityConstArray garbageBySize = garbageCollector.get(index);
        LongCell longCell = new LongCell();
        if (garbageBySize == null) {
            String newGarbageFileID = infinityFileID + ".garbage" + sectorSize;
            DiskManager.getInstance().properties.put(infinityFileID + ".garbage", "" + sectorSize, newGarbageFileID);
            garbageBySize = new InfinityConstArray(newGarbageFileID, longCell);
            longCell.setData(0);
            garbageBySize.add(longCell);
            longCell.setData(index);
            garbageBySize.add(longCell);
            garbageCollector.put(sectorSize, garbageBySize);
        } else {
            garbageBySize.get(0, longCell);
            long contentLength = longCell.value;
            if (contentLength < garbageBySize.fileData.sumFilesSize){
                longCell.setData(index);
                long newIndex = contentLength / longCell.getSize();
                garbageBySize.set(newIndex, longCell);
            }else{
                longCell.setData(index);
                garbageBySize.add(longCell);
            }
            longCell.setData(contentLength + longCell.getSize());
            garbageBySize.set(0, longCell);
        }
    }

    private MetaNode getGarbage(int index) {
        InfinityConstArray garbageBySize = garbageCollector.get(index);
        LongCell longCell = new LongCell();
        if (garbageBySize != null) {
            garbageBySize.get(0, longCell);
            long lastGarbage = longCell.value - longCell.getSize();
            garbageBySize.get(lastGarbage, longCell);
            MetaNode metaNode = new MetaNode();
            meta.get(longCell.value, metaNode);
            longCell.setData(lastGarbage);
            garbageBySize.set(0, longCell);
            return metaNode;
        }
        return null;
    }

    public long add(byte[] data) {
        MetaNode metaNode = new MetaNode();
        metaNode.start = super.add(dataToSector(data));
        metaNode.length = data.length;
        return meta.add(metaNode);
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
