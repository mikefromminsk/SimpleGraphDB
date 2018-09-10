package refactored;

import org.junit.Test;

import static org.junit.Assert.*;

public class InfinityArrayTest {
    /*

        @Test
        public void add() {
            InfinityArray testArray = new InfinityArray("testArray");
            byte[] testData = "tests".getBytes();
            long index = testArray.add(testData);
            testData[0] = 'b';
            testArray.set(index, testData);
            byte[] results = testArray.get(index);
            assertEquals("bests", new String(results));
        }
    */
    @Test
    public void testAddToGarbage() throws Exception {
        InfinityArray testArray = new InfinityArray("testArrayGarbage");
        byte[] testData = "test".getBytes();
        long index = testArray.add(testData);
        testData = Bytes.concat(testData, "ss".getBytes());
        testArray.set(index, testData);
        long contentLength = testArray.garbageCollector.get(4L).getLong(0);
        long lastGarbageIndex = testArray.garbageCollector.get(4L).getLong(contentLength - Long.BYTES);
        byte[] results = testArray.get(index);
        assertEquals("testss", new String(results));
        assertEquals(index, lastGarbageIndex);
        testArray.flush();
    }
}