package refactored;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;

public class InfinityFileSettings {

    public final static String INFINITY_FILE_PART_PREFIX = "part";
    public long partSize;
    public long sumFilesSize = 0;
    public ActionThread mainThread;
    private ArrayList<RandomAccessFile> files = new ArrayList<>();

    public InfinityFileSettings(String infinityFileID, IniFile properties, Integer partSize, ActionThread mainThread, String activeDiskDir) {
        this.mainThread = mainThread;
        this.partSize = partSize;
/*

        Map<String, String> fileSettings = properties.getSection(INFINITY_FILE_PART_PREFIX);
        if (fileSettings == null){
            String
            properties.put(infinityFileID, INFINITY_FILE_PART_PREFIX + "0", );
        }

        for (int i = 0; settings.containsKey(INFINITY_FILE_PART_PREFIX + i); i++) {
            try {
                RandomAccessFile file = new RandomAccessFile(settings.get("part" + i), "rw");
                files.add(file);
                sumFilesSize += file.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/

    }

    RandomAccessFile getFile(int index){
        // TODO create file in action thread

        return files.get(index);
    }
}
