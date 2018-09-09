package refactored;

import junit.framework.TestCase;

public class InfinityFileTest extends TestCase {

    public void testReadFromWriteBuffer1() throws Exception {
        //cache   [    ]
        //read    [    ]
        InfinityFile file = new InfinityFile("test1");
        byte[] testData = "test".getBytes();
        long position = file.add(testData);
        byte[] readiedData = file.read(position, testData.length);
        assertEquals(new String(readiedData), new String(testData));
    }

    public void testReadFromWriteBuffer2() throws Exception {
        //cache   [         ]
        //read      [    ]
        InfinityFile file = new InfinityFile("test2");
        byte[] testData = "test".getBytes();
        long position1 = file.add(testData);
        long position2 = file.add(testData);
        byte[] readiedData = file.read(position1 + 2, testData.length);
        assertEquals(new String(readiedData), new String("stte"));
    }

    public void testReadFromWriteBuffer3() throws Exception {
        //cache       [    ]
        //read      [    ]
        InfinityFile file = new InfinityFile("test3");
        byte[] testData = "test".getBytes();
        long position1 = file.add(testData);
        file.flush();
        long position2 = file.add(testData);
        byte[] readiedData = file.read(position1 + 2, testData.length);
        assertEquals(new String(readiedData), new String("stte"));
    }

    public void testReadFromWriteBuffer4() throws Exception {
        //cache       [    ]
        //read      [        ]
        InfinityFile file = new InfinityFile("test4");
        byte[] testData = "test".getBytes();
        long position1 = file.add(testData);
        file.add(testData);
        file.flush();
        file.write(position1 + 2, testData);
        byte[] readiedData = file.read(position1, 8);
        assertEquals(new String(readiedData), new String("tetestst"));
    }

    public void testReadFromWriteBuffer5() throws Exception {
        //cache       [    ]
        //read          [    ]
        InfinityFile file = new InfinityFile("test5");
        byte[] testData = "test".getBytes();
        long position1 = file.add(testData);
        file.add(testData);
        file.flush();
        file.write(position1, testData);
        byte[] readiedData = file.read(position1 + 2, 4);
        assertEquals(new String(readiedData), new String("stte"));
    }
}