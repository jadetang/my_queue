package github.jadetang.queue;

import github.jadetang.Util.TestHelp;
import github.jadetang.message.Message;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午5:37
 */
public class SingleFileMyQueueTest {
    @Test
    public void testAppendReturnRightOffset() throws Exception {

        SingleFileMyQueue queue = new SingleFileMyQueue("testQueue");

        Message message = TestHelp.getMessage("channelId","this is a test message");

        int messageNum = new Random().nextInt(100);
        int i = 0;
        while (i < messageNum) {
            int offset = queue.append(message);
            Assert.assertEquals(i, offset);
            i++;
        }
    }


    @Test
    public void testMaxConsume() throws IOException {

        SingleFileMyQueue queue = new SingleFileMyQueue("testQueue2");

        Message message = TestHelp.getMessage("channelId","test");

        queue.append(message);

        Optional<Message> messageFromQueue = queue.consume();

        Assert.assertEquals(message, messageFromQueue.get());

    }

    @Test
    public void testConsumeMessageShouldEqualMessageAppend() throws Exception {

        SingleFileMyQueue queue = new SingleFileMyQueue("testQueue2");

        Message message = TestHelp.getMessage("channelId","this is a new message");

        int offset = queue.append(message);

        Optional<Message> messageFromQueue = queue.consume(offset);

        Assert.assertEquals(message, messageFromQueue.get());



    }

}
