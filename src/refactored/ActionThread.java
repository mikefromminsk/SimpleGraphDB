package refactored;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ActionThread implements Runnable {

    static final boolean ACTION_READ = true;
    static final boolean ACTION_WRITE = false;
    private int threadsWaiting = 0;
    private final Object syncWriteLoopObject = 1;
    private ArrayList<WriteActionsBuffer> writeActionsBuffer = new ArrayList<>();

    public byte[] read(RandomAccessFile file, int offset, int length) {
        byte[] data = new byte[length];
        for (WriteActionsBuffer actionsBuffer : writeActionsBuffer) {
            if (actionsBuffer.file == file &&
                    offset >= actionsBuffer.offset &&
                    offset < actionsBuffer.offset + actionsBuffer.data.length) {
                int endOfCacheBlock = actionsBuffer.offset + actionsBuffer.data.length;
                int lengthInCashData = Math.min(endOfCacheBlock - offset, length);
                int offsetInCashData = offset - actionsBuffer.offset;
                System.arraycopy(actionsBuffer.data, offsetInCashData, data, 0, lengthInCashData);
                if (length == lengthInCashData) {
                    return data;
                } else {
                    byte[] rightData = read(file, offset + lengthInCashData, length - lengthInCashData);
                    System.arraycopy(rightData, 0, data, lengthInCashData - 1, rightData.length);
                    return data;
                }
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
        writeActionsBuffer.add(new WriteActionsBuffer(file, (int) offset, data));
        synchronized (syncWriteLoopObject) {
            syncWriteLoopObject.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (threadsWaiting == 0 && writeActionsBuffer.size() > 0) {
                WriteActionsBuffer action = writeActionsBuffer.get(0);
                boolean success = doAction(ACTION_WRITE, action.file, action.offset, action.data);
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

}
