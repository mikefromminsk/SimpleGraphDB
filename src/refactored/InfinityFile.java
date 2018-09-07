package refactored;

import java.io.RandomAccessFile;

public class InfinityFile {
    protected InfinityFileSettings settings;
    // TODO add cache
    // TODO add secure

    InfinityFile(String infinityFileID) {
        settings = DiskManager.getInstance().getInfinityFileSettings(infinityFileID);
    }

    public byte[] read(long start, long length) {
        if (start + length > settings.sumFilesSize)
            return null;

        int readingFileIndex = (int) (start / settings.partSize);
        RandomAccessFile readingFile = settings.files.get(readingFileIndex);
        int startInFile = (int) (start - readingFileIndex * settings.partSize);

        // TODO read from 2 files

        return settings.mainThread.read(readingFile, startInFile, (int) length);
    }

    public void write(long start, byte[] data) {
        int readingFileIndex = (int) (start / settings.partSize);
        RandomAccessFile writeFile = settings.files.get(readingFileIndex);
        int startInFile = (int) (start - readingFileIndex * settings.partSize);

        // TODO write to 2 files

        settings.mainThread.write(writeFile, startInFile, data);
        if (settings.archThread != null)
            settings.archThread.write(writeFile, startInFile, data);
    }

    public long add(byte[] data) {
        RandomAccessFile writeFile = settings.files.get(settings.files.size() - 1);
        settings.mainThread.write(writeFile, settings.sumFilesSize, data);
        long lastMaxPosition = settings.sumFilesSize;
        settings.sumFilesSize += data.length;
        return lastMaxPosition;
    }

}
