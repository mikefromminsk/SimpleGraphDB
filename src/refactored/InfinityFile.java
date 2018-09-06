package refactored;

import java.io.RandomAccessFile;

public class InfinityFile {
    InfinityFileSettings settings;

    InfinityFile(String infinityFileID) {
        settings = DiskManager.getInstance().getInfinityFileSettings(infinityFileID);
    }

    byte[] read(long start, int length) {
        if (start + length > settings.sumFilesSize)
            return null;

        int readingFileIndex = (int) (start / settings.partSize);
        RandomAccessFile readingFile = settings.files.get(readingFileIndex);
        int startInFile = (int) (start - readingFileIndex * settings.partSize);

        return settings.mainThread.read(readingFile, startInFile, length);
    }

    void write(long start, byte[] data) {
        if (data == null || data.length == 0)
            return;

        int readingFileIndex = (int) (start / settings.partSize);
        RandomAccessFile readingFile = settings.files.get(readingFileIndex);
        int startInFile = (int) (start - readingFileIndex * settings.partSize);

        settings.mainThread.write(readingFile, startInFile, data);
        settings.archThread.write(readingFile, startInFile, data);
    }

}
