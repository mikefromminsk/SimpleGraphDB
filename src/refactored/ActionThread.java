package refactored;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActionThread implements Runnable {

    private static final boolean ACTION_READ = true;
    private static final boolean ACTION_WRITE = false;
    private int threadsWaiting = 0;
    private final Object syncWriteLoopObject = 1;
    private Map<RandomAccessFile, Map<Integer, CacheData>> cache = new HashMap<>();
    private ArrayList<CacheData> writeSequences = new ArrayList<>();

    public byte[] read(RandomAccessFile file, int offset, int length) {
        byte[] data = new byte[length];

        Map<Integer, CacheData> cachedFile = cache.get(file);
        if (cachedFile != null) {
            CacheData cachedData = cachedFile.get(offset);
            if (cachedData != null){
                System.arraycopy(cachedData.data, 0, data, 0, length);
                return data;
            }
        }
        threadsWaiting++;
        boolean success = doAction(ACTION_READ, file, offset, data);
        if (success)
            return data;
        return null;
    }

    public void write(RandomAccessFile file, long offset, byte[] data) {
        if (data == null || data.length == 0)
            return;
        // TODO merge strings not more 512 byte in mainThread and max in achieveTread

        Map<Integer, CacheData> cachedFile = cache.get(file);
        if (cachedFile == null) {
            cachedFile = new HashMap<>();
            cache.put(file, cachedFile);
        }

        CacheData cachedData = cachedFile.get(offset);
        if (cachedData == null) {
            cachedData = new CacheData(true, file, (int) offset, data);
            cachedFile.put((int) offset, cachedData);
        } else {
            cachedData.data = data;
            cachedData.isUpdated = true;
            writeSequences.remove(cachedData);
        }
        writeSequences.add(cachedData);

        synchronized (syncWriteLoopObject) {
            syncWriteLoopObject.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (threadsWaiting == 0 && writeSequences.size() > 0) {
                CacheData action = writeSequences.get(0);
                boolean success = doAction(ACTION_WRITE, action.file, action.offset, action.data);
                if (success) {
                    action.isUpdated = false;
                    writeSequences.remove(action);
                }
            } else {
                synchronized (syncWriteLoopObject) {
                    try {
                        syncWriteLoopObject.wait();
                    } catch (InterruptedException continueLoop) {
                    }
                }
            }
        }
    }

    synchronized boolean doAction(boolean actionType, RandomAccessFile file, int offset, byte[] data) {
        if (actionType == ACTION_READ) {
            try {
                file.seek(offset);
                file.read(data);
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                threadsWaiting--;
                if (threadsWaiting == 0) {
                    synchronized (syncWriteLoopObject) {
                        syncWriteLoopObject.notify();
                    }
                }
            }
        } else {
            try {
                file.seek(offset);
                file.write(data);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public void flush(RandomAccessFile file) {
        for (Iterator<CacheData> it = writeSequences.iterator(); it.hasNext(); ) {
            CacheData actionsBuffer = it.next();
            if (actionsBuffer.file == file) {
                doAction(ACTION_WRITE, actionsBuffer.file, actionsBuffer.offset, actionsBuffer.data);
                actionsBuffer.isUpdated = false;
                it.remove();
            }
        }
    }
}
