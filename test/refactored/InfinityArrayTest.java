package refactored;

import org.junit.Test;

import static org.junit.Assert.*;

public class InfinityArrayTest {

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
}