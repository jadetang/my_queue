package github.jadetang;

import github.jadetang.Util.MockMessageHandler;
import github.jadetang.Util.TestHelp;
import github.jadetang.handler.DefaultMessageHandler;
import github.jadetang.handler.MessageHandler;
import github.jadetang.message.Message;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * @author sanguan.tangsicheng on 2017/7/18 下午2:09
 */
public class ChannelListenerTest {

    static ChannelListener listener;

    static String channelId = "testChannelId";

    static MessageHandler messageHandler;

    @BeforeClass
    public static void setUp() throws IOException {
        listener = ChannelListener.getInstance();
        messageHandler = new MockMessageHandler();
        listener.registerMessageHandler(messageHandler);
        listener.createChannel(channelId, 10);
    }


    @Test
    public void getInstanceShouldReturnTheSameInstance() throws Exception {

        ChannelListener instance1 = ChannelListener.getInstance();
        ChannelListener instance2 = ChannelListener.getInstance();

        Assert.assertTrue(instance1 == instance2);

    }

    @Test
    public void queueSizeShouldEqualToTheMessageNumber() throws Exception {
        Message message = TestHelp.getMessage(channelId,"test is a test");
        int count = 100;
        for (int i = 0; i < count; i++) {
            listener.receiveMessage(message);
        }
        listener.startConsumeMessage();
        TimeUnit.SECONDS.sleep(1);
        Queue<Message> queue = listener.getQueue();

        Assert.assertEquals(count,queue.size());


    }


    @Test
    public void queueSizeShouldExceedTheMaxOutputSize() throws Exception{

        Message message = TestHelp.getMessage(channelId,"test is a test");
        int count = 1000;
        for (int i = 0; i < count; i++) {
            listener.receiveMessage(message);
        }
        listener.startConsumeMessage();
        TimeUnit.SECONDS.sleep(1);
        Queue<Message> queue = listener.getQueue();

        Assert.assertEquals(Config.outputQueueSize(),queue.size());

    }


    @Test
    public void afterRemoveChannelTheMessageShouldNotBeStored() throws Exception {
        String localChannelId = "localChannel";

        listener.createChannel(localChannelId,10);

        Message message = TestHelp.getMessage(localChannelId, "this is a test");

        listener.receiveMessage(message);


        Message message2 = TestHelp.getMessage(localChannelId, "this is another message");

        listener.removeChannel(localChannelId);

        listener.receiveMessage(message2);

        Assert.assertTrue(!messageHandler.consumeMessage(localChannelId, 1).isPresent());

    }


    @Test(expected = IllegalArgumentException.class)
    public void registerMessageHandlerShouldCalledOnlyOnce() throws Exception {

        ChannelListener.getInstance().registerMessageHandler(new DefaultMessageHandler());
        ChannelListener.getInstance().registerMessageHandler(new DefaultMessageHandler());

    }

}
