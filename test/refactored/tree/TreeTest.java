package refactored.tree;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class TreeTest {

    @Test
    public void put() {
        int randomInt = new Random(System.currentTimeMillis()).nextInt(1000);
        Tree tree = new Tree("tree" + randomInt);
        assertEquals(1 * TreeNode.SIZE, tree.fileData.sumFilesSize);
        tree.put("String1", "3012".getBytes(), 123);
        assertEquals(2 * TreeNode.SIZE, tree.fileData.sumFilesSize);
        tree.put("String2", "3021".getBytes(), 123);
        assertEquals(3 * TreeNode.SIZE, tree.fileData.sumFilesSize);
    }
}