package refactored;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

public class ActionThread implements Runnable {

    static final boolean ACTION_READ = true;
    static final boolean ACTION_WRITE = false;
    private int threadsWaiting = 0;
    private final Object syncWriteLoopObject = 1;
    private ArrayList<WriteActionsBuffer> writeActionsBuffer = new ArrayList<>();

    public byte[] read(RandomAccessFile file, int startOfData, int lengthOfData) {
        byte[] data = new byte[lengthOfData];
        int endOfData = startOfData + lengthOfData;
        for (WriteActionsBuffer actionsBuffer : writeActionsBuffer)
            if (actionsBuffer.file == file) {
                int startOfCache = actionsBuffer.offset;
                int endOfCache = actionsBuffer.offset + actionsBuffer.data.length;
                if (startOfData >= startOfCache && startOfData < endOfCache) {
                    int lengthInCashData = Math.min(endOfCache - startOfData, lengthOfData);
                    int offsetInCashData = startOfData - startOfCache;
                    System.arraycopy(actionsBuffer.data, offsetInCashData, data, 0, lengthInCashData);
                    if (lengthOfData == lengthInCashData) {
                        return data;
                    } else {
                        byte[] rightData = read(file, startOfData + lengthInCashData, lengthOfData - lengthInCashData);
                        System.arraycopy(rightData, 0, data, lengthInCashData, rightData.length);
                        return data;
                    }
                } else if (startOfData < startOfCache && endOfData > startOfCache) {
                    int lengthInCashData = Math.min(endOfData - startOfCache, endOfCache - startOfCache);
                    int offsetInResult = startOfCache - startOfData;
                    System.arraycopy(actionsBuffer.data, 0, data, offsetInResult, lengthInCashData);
                    byte[] leftData = read(file, startOfData, offsetInResult);
                    System.arraycopy(leftData, 0, data, 0, leftData.length);
                    if (endOfData > endOfCache){
                        byte[] rightData = read(file, endOfCache, endOfData - endOfCache);
                        offsetInResult = endOfCache - startOfData;
                        System.arraycopy(rightData, 0, data, offsetInResult, rightData.length);
                    }
                    return data;
                }
            }
        threadsWaiting++;
        boolean success = doAction(ACTION_READ, file, startOfData, data);
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
            //syncWriteLoopObject.notify();
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

    public void flush(RandomAccessFile file) {

        for (Iterator<WriteActionsBuffer> it = writeActionsBuffer.iterator(); it.hasNext(); ) {
            WriteActionsBuffer actionsBuffer = it.next();
            if (actionsBuffer.file == file) {
                doAction(ACTION_WRITE, actionsBuffer.file, actionsBuffer.offset, actionsBuffer.data);
                it.remove();
            }

        }
    }
}
