import java.util.ArrayList;

public class ActionThread implements Runnable {

    private ArrayList<String> Buffer;

    public static final int Perhaps = 0;
    public static final int Impossible = 1;

    ArrayList<Act> Acts;
    Thread ActionThreadth;

    Thread secondaryThread;

    Object locker;

    public ActionThread() {
        Acts = new ArrayList<Act>();
        ActionThreadth = new Thread(this);
        Buffer = new ArrayList<String>();
        this.secondaryThread = Thread.currentThread();
        this.locker = new Object();
    }

    public void run() {
        while (Acts.size() != 0) {
            if (Acts.get(0).getActType() == Act.Write) {
                try {
                    if (Acts.get(0).getTypeFile() == Act.Tree) {
                        Tree tree = (Tree) Acts.get(0).getTreeOrInfinity();
                        tree.Add(Acts.get(0).getData());
                    } else {
                        InfinityFile ifi = (InfinityFile) Acts.get(0).getTreeOrInfinity();
                        ifi.Add(Acts.get(0).getData());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (Acts.get(0).getTypeFile() == Act.Tree) {
                        Tree tree = (Tree) Acts.get(0).getTreeOrInfinity();
                        String[] arr = tree.Read(Acts.get(0).getData());
                        for (String el : arr) {
                            Buffer.add(el);
                        }
                    } else {
                        InfinityFile ifi = (InfinityFile) Acts.get(0).getTreeOrInfinity();
                        Buffer.add(ifi.Read(Acts.get(0).getStart(), Acts.get(0).getSize()));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    while (secondaryThread.getState() != Thread.State.WAITING) ;
                    synchronized (locker) {
                        locker.notify();
                    }
                }
            }
            Acts.remove(0);
        }
    }

    public String[] getBufferData() throws Throwable {
        String[] arr = Buffer.toArray(new String[Buffer.size()]);
        Buffer.clear();
        return arr;
    }

    public void ClearBuffer() {
        Buffer.clear();
    }

    public int AddAct(Act act) throws Throwable {
        secondaryThread = Thread.currentThread();

        if (act.getActType() == Act.Read || Acts.size() == 0 || Acts.get(Acts.size() - 1).getActType() == Act.Write)
            Acts.add(act);
        else
            return Impossible;

        if (!ActionThreadth.isAlive()) {
            ActionThreadth = new Thread(this);
            ActionThreadth.start();
        }

        if (act.getActType() == Act.Read)
            synchronized (locker) {
                locker.wait();
            }

        return Perhaps;
    }
}

class Act {

    public Act(Tree tree, int ActType, String Data) {
        this.TreeOrInfinity = tree;
        this.ActType = ActType;
        this.TypeFile = Tree;
        this.Data = Data;
    }

    public Act(InfinityFile file, String Data) {
        this.TreeOrInfinity = file;
        this.ActType = Write;
        this.TypeFile = InfinityFile;
        this.Data = Data;
    }

    public Act(InfinityFile file, long Start, long Size) {
        this.TreeOrInfinity = file;
        this.ActType = Read;
        this.TypeFile = InfinityFile;
        this.Start = Start;
        this.Size = Size;
    }

    public Object getTreeOrInfinity() {
        return TreeOrInfinity;
    }

    public int getActType() {
        return ActType;
    }

    public String getData() {
        return Data;
    }

    public int getTypeFile() {
        return TypeFile;
    }

    public long getStart() {
        return Start;
    }

    public long getSize() {
        return Size;
    }

    private Object TreeOrInfinity;
    private int ActType;
    private String Data;
    private int TypeFile;
    private long Start;
    private long Size;

    public static final int Read = 0;
    public static final int Write = 1;

    public static final int Tree = 0;
    public static final int InfinityFile = 1;
}
