import java.util.ArrayList;

public class WorkThread implements Runnable {

    private ActionThread at;
    private WriteBufferThread wb;
    private String[] buffer;
    private Thread workThread;

    private ArrayList<Act> acts;

    public WorkThread() {
        at = new ActionThread();
        wb = new WriteBufferThread(at);
        workThread = new Thread(this);
        acts = new ArrayList<Act>();
    }

    public void run() {
        while (acts.size() != 0) {
            if (acts.get(0).getActType() == Act.Write) {
                wb.AddTask(acts.get(0));
            } else {
                try {
                    at.AddAct(acts.get(0));
                    buffer = at.getBufferData();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            acts.remove(0);
        }
    }


    public void AddWriteTreeAct(Tree tree, String data) {
        acts.add(new Act(tree, Act.Write, data));
        StartThread();
    }

    public void AddReadTreeAct(Tree tree, String hash) {
        buffer = null;

        acts.add(new Act(tree, Act.Read, hash));
        StartThread();
    }

    public void AddWriteInfinityAct(InfinityFile file, String data) {
        acts.add(new Act(file, data));
        StartThread();
    }

    public void AddReadInfinityAct(InfinityFile file, long Start, long Size) {
        buffer = null;

        acts.add(new Act(file, Start, Size));
        StartThread();
    }

    public String[] getBuffer() {
        return buffer;
    }

    private void StartThread() {
        if (workThread.getState() == Thread.State.TERMINATED ||
                workThread.getState() == Thread.State.NEW) {
            workThread = new Thread(this);
            workThread.start();
        }
    }

}
