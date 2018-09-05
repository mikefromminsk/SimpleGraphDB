package refactored;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ActionThread {

    public ActionThread(String rootDir) {

    }

    static final boolean ACTION_READ = true;
    static final boolean ACTION_WRITE = false;

    private int threadsWaiting = 0;

    public byte[] read(RandomAccessFile file, int offset, int length) {
        threadsWaiting++;
        byte[] data = new byte[length];
        boolean success = doAction(ACTION_READ, file, offset, length, data);
        if (success)
            return data;
        return null;
    }

    public boolean write(RandomAccessFile file, int offset, byte[] data) {
        if (threadsWaiting > 0)
            return false;
        return doAction(ACTION_WRITE, file, offset, data.length, data);
    }

    synchronized boolean doAction(boolean actionType, RandomAccessFile file, int offset, int length, byte[] result) {
        if (actionType == ACTION_READ) {
            try {
                file.read(result, offset, length);
                threadsWaiting--;
                return true;
            } catch (IOException e) {
                threadsWaiting--;
                return false;
            }
        } else {
            try {
                file.write(result, offset, length);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

}
