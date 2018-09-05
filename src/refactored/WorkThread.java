package refactored;

import java.util.Random;

public class WorkThread implements Runnable {

    Random random = new Random();
    InfinityFile[] files = new InfinityFile[]
            {
                    new InfinityFile("file1"),
                    new InfinityFile("file2"),
                    new InfinityFile("file3")
            };

    @Override
    public void run() {
        while (true) {
            int mode = random.nextInt() % 3;
            int fileIndex = random.nextInt() % files.length;
            InfinityFile activeFile = files[fileIndex];
            switch (mode) {
                case 0: {
                    long start = random.nextLong();
                    int length = random.nextInt();
                    activeFile.read(start, length);
                    break;
                }
                case 1: {
                    long start = random.nextLong();
                    byte[] data = new byte[100];
                    random.nextBytes(data);
                    activeFile.write(start, data);
                    break;
                }
                case 2:
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
