package refactored;

import org.junit.Test;

import static org.junit.Assert.*;

public class InfinityConstArrayTest {

    class TestCell implements InfinityConstArrayCell {

        long field1;
        long field2;

        public TestCell() {
            this.field1 = 0;
            this.field2 = 0;
        }

        public TestCell(long field1, long field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public void setData(byte[] data) {
            long[] longData = Bytes.toLongArray(data);
            field1 = longData[0];
            field2 = longData[1];
        }

        @Override
        public byte[] getBytes() {
            long[] longs = new long[2];
            longs[0] = field1;
            longs[1] = field2;
            byte[] data = Bytes.fromLongArray(longs);
            return data;
        }

        @Override
        public int getSize() {
            return 2 * Long.BYTES;
        }
    }

    @Test
    public void add() {
        InfinityConstArray testConstArray = new InfinityConstArray("constArrayTest");
        long index1 = testConstArray.add(new TestCell(258, 789));
        long index2 = testConstArray.add(new TestCell(345, 674));
        TestCell destination = new TestCell();
        testConstArray.get(index1, destination);
        assertEquals(258, destination.field1);
        testConstArray.get(index2, destination);
        assertEquals(674, destination.field2);
    }
}