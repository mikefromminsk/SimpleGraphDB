package refactored;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;

public class InfinityFileSettings {

    public long partSize;
    public long maxCacheSize;
    public long maxCacheObjectCount;
    public ArrayList<RandomAccessFile> files = new ArrayList<>();
    public long sumFilesSize = 0;
    public ActionThread mainThread;
    public ActionThread archThread;

    public InfinityFileSettings(Map<String, String> settings, ActionThread mainThread, ActionThread archThread) {
        this.mainThread = mainThread;
        this.archThread = archThread;

        partSize = Long.parseLong(settings.getOrDefault("partSize", "10"));
        maxCacheSize = Long.parseLong(settings.getOrDefault("maxCacheSize", "10"));
        maxCacheObjectCount = Long.parseLong(settings.getOrDefault("maxCacheObjectCount", "10"));

        for (int i = 0; settings.containsKey("part" + i); i++) {
            try {
                RandomAccessFile file = new RandomAccessFile(settings.get("part" + i), "rw");
                files.add(file);
                sumFilesSize += file.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
