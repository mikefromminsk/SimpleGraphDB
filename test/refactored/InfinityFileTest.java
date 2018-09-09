package refactored;

import junit.framework.TestCase;

public class InfinityFileTest extends TestCase {

    public void testAdd() throws Exception {
        InfinityFile file = new InfinityFile("test");
        byte[] testData = "test".getBytes();
        long position = file.add(testData);
        byte[] readiedData = file.read(position, testData.length);
        assertEquals(new String(testData), new String(readiedData));
    }

    public void testAdd2() throws Exception {
        InfinityFile file = new InfinityFile("test");
        byte[] testData = "test".getBytes();
        long position = file.add(testData);
        byte[] readiedData = file.read(position, testData.length);
        assertEquals(new String(testData), new String(readiedData));
    }
}