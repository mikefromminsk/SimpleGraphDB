import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;


public class DiskManager {

    private static final int Read = 0;
    private static final int Write = 1;
    private static final int CountActions = 1000;
    private static final long SizeTestFile = 100000;
    private static String InfinityDiskManager = "%ProgramFiles%\\InfinityDiskManager";

    private String MainPath;
    private String SecondaryPath;

    private Tree MainTree;
    private Tree SecondaryTree;

    private String NameManager;

    private WorkThread MainWorkThread;
    private WorkThread SecondaryWorkThread;

    public DiskManager(String path1, String path2, int Size) throws Throwable {
        if (!new File(path1).isDirectory() || !new File(path2).isDirectory())
            throw new Throwable("Директория не найдена!");

        InfinityDiskManager = System.getenv("ProgramFiles") + "\\InfinityDiskManager";

        Test(path1, path2);

        CreateRep(MainPath, Size);
        CreateRep(SecondaryPath, Size);

        MainTree = new Tree(MainPath);
        SecondaryTree = new Tree(SecondaryPath);

        NameManager = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + Size;
        CreateMetaData();

        MainWorkThread = new WorkThread();
        SecondaryWorkThread = new WorkThread();
    }

    public DiskManager(String NameManager) throws Throwable {
        InfinityDiskManager = System.getenv("ProgramFiles") + "\\InfinityDiskManager";

        if (!new File(InfinityDiskManager, NameManager + ".dat").isFile())
            throw new Throwable("Репозиторий не найден!");

        this.NameManager = NameManager;
        ReadMetaData();

        MainTree = new Tree(MainPath);
        SecondaryTree = new Tree(SecondaryPath);

        MainWorkThread = new WorkThread();
        SecondaryWorkThread = new WorkThread();
    }

    public void Add(String data) throws Throwable {
        MainWorkThread.AddWriteTreeAct(MainTree, data);
        SecondaryWorkThread.AddWriteTreeAct(SecondaryTree, data);
    }

    public String[] Read(String Hash) throws Throwable {
        MainWorkThread.AddReadTreeAct(MainTree, Hash);
        SecondaryWorkThread.AddReadTreeAct(MainTree, Hash);
        while (true) {
            if (MainWorkThread.getBuffer() != null && MainWorkThread.getBuffer().length != 0)
                return MainWorkThread.getBuffer();
            if (SecondaryWorkThread.getBuffer() != null) return SecondaryWorkThread.getBuffer();
        }
    }

    public void CreateInfinityFile(String name, int size) throws Throwable {
        new InfinityFile(MainPath + "\\" + name, size);
        new InfinityFile(SecondaryPath + "\\" + name, size);
    }

    public void AddInfinityData(String name, String data) throws Throwable {
        MainWorkThread.AddWriteInfinityAct(new InfinityFile(MainPath + "\\" + name), data);
        SecondaryWorkThread.AddWriteInfinityAct(new InfinityFile(SecondaryPath + "\\" + name), data);
    }

    public String ReadInfinity(String name, long Start, long Size) throws Throwable {
        MainWorkThread.AddReadInfinityAct(new InfinityFile(MainPath + "\\" + name), Start, Size);
        SecondaryWorkThread.AddReadInfinityAct(new InfinityFile(SecondaryPath + "\\" + name), Start, Size);
        while (true) {
            if (MainWorkThread.getBuffer() != null) return MainWorkThread.getBuffer()[0];
            if (SecondaryWorkThread.getBuffer() != null) return SecondaryWorkThread.getBuffer()[0];
        }
    }

    public String GetNameManager() {
        return NameManager;
    }


    private void CreateMetaData() throws Throwable {
        if (!new File(InfinityDiskManager).isDirectory())
            Files.createDirectory(Paths.get(InfinityDiskManager));

        FileOutputStream fos = new FileOutputStream(new File(InfinityDiskManager, NameManager + ".dat"));
        fos.write((MainPath + System.lineSeparator() + SecondaryPath).getBytes());
        fos.close();
    }

    private void ReadMetaData() throws Throwable {
        FileInputStream fis = new FileInputStream(new File(InfinityDiskManager, NameManager + ".dat"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        MainPath = reader.readLine();
        SecondaryPath = reader.readLine();

        reader.close();
        fis.close();
    }

    private void CreateRep(String path, int size) throws Throwable {
        new InfinityFile(path + "\\.meta", size);
        new InfinityFile(path + "\\.nodes", size);
        new InfinityFile(path + "\\.links", size);
        new InfinityFile(path + "\\.tree", size);
    }

    private void Test(String path1, String path2) throws Throwable {
        int[] actions = new int[CountActions];
        for (int i = 0; i < CountActions; i++)
            actions[i] = ThreadLocalRandom.current().nextInt(Read, Write + 1);

        if (timeСheck(path1, actions) >= timeСheck(path2, actions)) {
            MainPath = path1;
            SecondaryPath = path2;
        } else {
            MainPath = path2;
            SecondaryPath = path1;
        }
    }

    private long timeСheck(String path, int[] actions) throws Throwable {
        File testFile = new File(path, "test.ini");
        long startTime = System.currentTimeMillis();

        if (testFile.isFile())
            testFile.delete();
        testFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(testFile);
        for (long i = 0; i < SizeTestFile; i++)
            fos.write("0".getBytes());
        fos.close();


        RandomAccessFile writer = new RandomAccessFile(testFile, "rw");
        for (int i = 0; i < CountActions; i++) {
            writer.seek(ThreadLocalRandom.current().nextLong(0, SizeTestFile));

            if (actions[i] == Read)
                writer.read();
            else
                writer.write("0".getBytes());
        }
        writer.close();

        return System.currentTimeMillis() - startTime;
    }

    public void ChangeCacheSetting(long maxSizeCache, long maxFragmentCache) throws Throwable {
        MainTree.ChangeCacheSetting(maxSizeCache, maxFragmentCache);
        SecondaryTree.ChangeCacheSetting(maxSizeCache, maxFragmentCache);
    }

    public void Close() throws Throwable {
        MainTree.Close();
        SecondaryTree.Close();
    }
}
