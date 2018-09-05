package refactored;

public class InfinityFile {
    InfinityFileSettings settings;

    InfinityFile(String infinityFileID) {
        settings = DiskManager.getInstance().getInfinityFileSettings(infinityFileID);
    }


    byte[] read(long start, int length){

        return null;
    }

    void write(long start, byte[] data){

    }

    void edit(){

    }

}
