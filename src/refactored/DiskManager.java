package refactored;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiskManager {

    private static final String PROPERTIES_FILE_NAME = "disk_manager_main.prop";
    private static DiskManager instance;
    private IniFile properties = null;
    private List<ActionThread> actionThreads = new ArrayList<>();
    private ActionThread mainThread;
    private ActionThread archThread;

    public static DiskManager getInstance() {
        if (instance == null)
            instance = new DiskManager();
        return instance;
    }

    private DiskManager() {
        if (!loadProps()) {
            if (!saveProps())
                loadProps();
        } else {
            Map<String, String> disks = properties.getSection("disks");
            for (String rootDir : disks.values())
                actionThreads.add(new ActionThread(rootDir));
            diskTesting();
        }
    }

    public void addDisk(String rootDir) {
        properties.put("disks", "" + actionThreads.size() + 1, rootDir);
        actionThreads.add(new ActionThread(rootDir));
        diskTesting();
    }

    public void diskTesting() {
        // TODO testing of all disks
        if (actionThreads.size() > 0) {
            mainThread = actionThreads.get(0);
            archThread = actionThreads.get(0);
        }
    }

    private boolean loadProps() {
        try {
            properties.load(PROPERTIES_FILE_NAME);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private boolean saveProps() {
        try {
            properties.save(PROPERTIES_FILE_NAME);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public InfinityFileSettings getInfinityFileSettings(String infinityFileID) {
        return new InfinityFileSettings(properties.getSection(infinityFileID));
    }


}
