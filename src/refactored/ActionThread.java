package refactored;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ActionThread {

    public ActionThread(String rootDir) {

    }

    public enum Action {
        READ,
        WRITE
    }

    public enum ActionResult {
        SUCCESS,
        ERROR,
        BLOCKED
    }

    private int threadsWaiting = 0;

    public void prepareReadAction() {
        threadsWaiting++;
    }

    synchronized ActionResult doAction(Action action, RandomAccessFile file, int offset, int length, byte[] result) {
        if (action == Action.READ) {
            try {
                file.read(result, offset, length);
                return ActionResult.SUCCESS;
            } catch (IOException e) {
                return ActionResult.ERROR;
            }
        } else {
            if (threadsWaiting > 0)
                return ActionResult.BLOCKED;
            try {
                file.write(result, offset, length);
                return ActionResult.SUCCESS;
            } catch (IOException e) {
                return ActionResult.ERROR;
            }
        }
    }

}
