package github.jadetang.storage;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Random;

/**
 * @author sanguan.tangsicheng on 2017/7/15 下午4:14
 */
public class MappedFileTest {

    String testFile = "test";
    String smallFile = "small";

    @Test
    public void test() throws IOException {
        MappedFile f = new MappedFile(testFile, 1024);
        Assert.assertNotNull(f);
    }

    @Test
    public void testAppendData() throws IOException {
        MappedFile f = new MappedFile(testFile, 1024);
        Assert.assertTrue(f.appendData("test data".getBytes()));
    }


    @Test
    public void testAppendIntAndReadFromFile() throws IOException {
        MappedFile f = new MappedFile("tiny.data", Integer.BYTES);
        ByteBuffer b = ByteBuffer.allocate(Integer.BYTES);
        int intToAppend = 2;
        b.putInt(intToAppend);
        f.appendData(b.array());
        int intReadFromFile = f.readData(0, Integer.BYTES).get().getInt();
        Assert.assertEquals(intToAppend, intReadFromFile);

    }


    @Test
    public void testAppendWhenSmall() throws IOException {
        MappedFile f = new MappedFile(smallFile, 5);
        Assert.assertFalse(f.appendData("very very very very very very very long data".getBytes()));
    }

    @Test
    public void testReadData() throws IOException {
        Random random = new Random();

        MappedFile f = new MappedFile(random.nextInt(100) + ".data", 1024);

        String data = "this is a test";

        f.appendData(data.getBytes());

        ByteBuffer byteBuffer = f.readData(0, data.getBytes().length).get();


        byte[] newData = new byte[byteBuffer.limit()];

        int i = 0;

        while (byteBuffer.hasRemaining()) {
            newData[i++] = byteBuffer.get();
        }

        String readData = new String(newData);

        Assert.assertEquals(data, readData);

    }


    @Test(expected = ReadOnlyBufferException.class)
    public void testModifyByteBuffer() throws IOException {
        MappedFile f = new MappedFile(testFile, 1024);
        f.setWrotePosition(4);
        ByteBuffer byteBuffer = f.readData(0, 4).get();
        byteBuffer.putChar('t');

    }
    


}
