package refactored;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiskManager {

    private static DiskManager instance;
    public IniFile properties = null;
    public ActionThread mainThread;

    public final static File dbDir = new File("SimpleGraphDB");
    public final static File propertiesFile = new File(dbDir, "settings.properties");
    public Integer partSize;

    public static DiskManager getInstance() {
        if (instance == null) {
            try {
                instance = new DiskManager();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public final static String DISK_MANAGER_SECTION = "_manager_";
    public final static String DISK_MANAGER_PART_SIZE_KEY = "part_size";

    private DiskManager() throws FileNotFoundException {
        // TODO double save settings
        // TODO problem when DiskManager init without saving data rights

        if (!dbDir.isDirectory())
            if (!dbDir.mkdir())
                throw new FileNotFoundException();
        try {
            properties = new IniFile(propertiesFile);
            if (properties.getSection(DISK_MANAGER_SECTION) == null)
                initProperties(properties);
            loadProperties(properties);

            mainThread = new ActionThread();
            Thread thread = new Thread(mainThread);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProperties(IniFile properties) {
        this.partSize = properties.getInt(DISK_MANAGER_SECTION, DISK_MANAGER_PART_SIZE_KEY, 4096);
    }

    private void initProperties(IniFile properties) {
        properties.put(DISK_MANAGER_SECTION, DISK_MANAGER_PART_SIZE_KEY, "4096");
    }

    public void addDisk(String rootDir) {
    }

    public void diskTesting() {
        // TODO testing of all disks
    }

}
