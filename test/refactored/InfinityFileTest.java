package refactored;

import junit.framework.TestCase;

public class InfinityFileTest extends TestCase {

    public void testReadFromWriteBuffer1() {
        InfinityFile file = new InfinityFile("test1");
        byte[] testData = "test".getBytes();
        long position = file.add(testData);
        byte[] readiedData = file.read(position, testData.length);
        assertEquals(new String(testData), new String(readiedData));
    }
}