package refactored;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ActionThread implements Runnable {

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

    private final Object syncWriteLoopObject = 1;

    private ArrayList<WriteActionsBuffer> writeActionsBuffer = new ArrayList<>();

    public void write(RandomAccessFile file, int offset, byte[] data) {
        if (data == null || data.length == 0)
            return;
        writeActionsBuffer.add(new WriteActionsBuffer(file, offset, data));
        synchronized (syncWriteLoopObject) {
            syncWriteLoopObject.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (threadsWaiting == 0 && writeActionsBuffer.size() > 0) {
                WriteActionsBuffer action = writeActionsBuffer.get(0);
                boolean success = doAction(ACTION_WRITE, action.file, action.offset, action.data.length, action.data);
                if (success)
                    writeActionsBuffer.remove(action);
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

    synchronized boolean doAction(boolean actionType, RandomAccessFile file, int offset, int length, byte[] data) {
        if (actionType == ACTION_READ) {
            try {
                file.read(data, offset, length);
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
                file.write(data, offset, length);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

}
