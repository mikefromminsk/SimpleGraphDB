package refactored;

import java.util.ArrayList;

public class WriteBufferThread implements Runnable {

    private ActionThread actionThread;
    private ArrayList<String> buffer;

    public WriteBufferThread(ActionThread actionThread) {
        this.actionThread = actionThread;
    }

    public void run() {
        while (true) {
            actionThread.write()
        }
    }

}



