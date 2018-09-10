package refactored;

public class InfinityArray extends InfinityFile {
    // TODO garbage collector

    private InfinityConstArray meta;

    public InfinityArray(String infinityFileID) {
        super(infinityFileID);
        meta = new InfinityConstArray(infinityFileID + ".meta", new MetaNode());
    }

    public byte[] get(int index) {
        MetaNode metaNode = new MetaNode();
        meta.get(index, metaNode);
        return read(metaNode.start, metaNode.length);
    }

    public void set(int index, byte[] data) {
        MetaNode metaNode = new MetaNode();
        meta.get(index, metaNode);
        int lastSectorLength = getSectorLength((int) metaNode.length);
        int newSectorLength = getSectorLength(data.length);
        if (newSectorLength > lastSectorLength) {
            // TODO get from gc
        } else {
            byte[] result = new byte[lastSectorLength];
            System.arraycopy(data, 0, result, 0, data.length);
            write(metaNode.start, result);
        }
    }

    public long add(byte[] data) {
        MetaNode metaNode = new MetaNode();
        metaNode.start = add(dataToSector(data));
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
