package refactored;

public class DiskManager {

    private static DiskManager instance;

    public static DiskManager getInstance() {
        if (instance == null)
            instance = new DiskManager();
        return instance;
    }

    DiskManager(){

    }


}
