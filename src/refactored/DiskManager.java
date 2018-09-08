package refactored;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiskManager {

    private static DiskManager instance;
    private IniFile properties = null;
    private List<ActionThread> actionThreads = new ArrayList<>();
    private ActionThread mainThread;
    private ActionThread archThread;

    public final static File dbDir = new File("SimpleGraphDB");
    public final static File propertiesFile = new File(dbDir, "settings.properties");

    public static DiskManager getInstance() {
        if (instance == null)
            instance = new DiskManager();
        return instance;
    }

    private DiskManager() {
        // TODO double save settings
        // TODO problem when DiskManager init without saving data rights

        if (!dbDir.isDirectory())
            dbDir.mkdir();

        try {
            properties = new IniFile(propertiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        return false;
    }

    private boolean saveProps() {
        return false;
    }

    public InfinityFileSettings getInfinityFileSettings(String infinityFileID) {
        return new InfinityFileSettings(properties.getSection(infinityFileID), mainThread, archThread);
    }


}
