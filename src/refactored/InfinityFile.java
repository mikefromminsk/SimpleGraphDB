package refactored;

import java.io.RandomAccessFile;

public class InfinityFile {
    public final static int MAX_STORAGE_DATA_IN_DB = 2048;
    protected InfinityFileSettings settings;
    // TODO add cache
    // TODO add secure

    InfinityFile(String infinityFileID) {
        settings = DiskManager.getInstance().getInfinityFileSettings(infinityFileID);
    }

    public byte[] read(long start, long length) {
        long end = start + length;
        if (end > settings.sumFilesSize)
            return null;

        if (length > MAX_STORAGE_DATA_IN_DB){
            // TODO return file descriptor
        }

        int startFileIndex = (int) (start / settings.partSize);
        int endFileIndex = (int) (end / settings.partSize);
        if (startFileIndex == endFileIndex) {
            RandomAccessFile readingFile = settings.getFile(startFileIndex);
            int startInFile = (int) (start % settings.partSize);
            return settings.mainThread.read(readingFile, startInFile, (int) length);
        } else {
            RandomAccessFile firstFile = settings.getFile(startFileIndex);
            RandomAccessFile secondFile = settings.getFile(endFileIndex);
            int lengthInSecondFile = (int) (end % settings.partSize);
            int lengthInFirstFile = (int) (length - lengthInSecondFile);
            int startInFirstFile = (int) (start % settings.partSize);
            int startInSecondFile = 0;
            byte[] dataFromFirstFile = settings.mainThread.read(firstFile, startInFirstFile, lengthInFirstFile);
            byte[] dataFromSecondFile = settings.mainThread.read(secondFile, startInSecondFile, lengthInSecondFile);
            return Bytes.concat(dataFromFirstFile, dataFromSecondFile);
        }
    }

    public void write(long start, byte[] data) {
        long length = data.length;
        long end = start + length;
        if (start > settings.sumFilesSize)
            return;

        if (length > MAX_STORAGE_DATA_IN_DB){
            // TODO save to file system
        }

        int startFileIndex = (int) (start / settings.partSize);
        int endFileIndex = (int) (end / settings.partSize);

        RandomAccessFile firstWriteFile = settings.getFile(startFileIndex);
        RandomAccessFile secondWriteFile = settings.getFile(endFileIndex);

        settings.sumFilesSize += data.length;

        if (startFileIndex == endFileIndex) {
            int startInFile = (int) (start - startFileIndex * settings.partSize);
            settings.mainThread.write(firstWriteFile, startInFile, data);
            // TODO archive thread
        } else {
            int lengthInSecondFile = (int) (end % settings.partSize);
            int lengthInFirstFile = (int) (length - lengthInSecondFile);
            int startInFirstFile = (int) (start % settings.partSize);
            int startInSecondFile = 0;
            byte[] dataToFirstFile = new byte[lengthInFirstFile];
            byte[] dataToSecondFile = new byte[lengthInSecondFile];
            settings.mainThread.write(firstWriteFile, startInFirstFile, dataToFirstFile);
            settings.mainThread.write(secondWriteFile, startInSecondFile, dataToSecondFile);
            // TODO archive thread
        }
    }

    public long add(byte[] data) {
        long lastMaxPosition = settings.sumFilesSize;
        write(lastMaxPosition, data);
        return lastMaxPosition;
    }

}
