package github.jadetang.handler;

import github.jadetang.TestHelp;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @author sanguan.tangsicheng on 2017/7/17 下午7:32
 */
public class DefaultMessaageHandlerTest {
    @Test
    public void messageReceivedShouldReturnOffset() throws Exception {

        String channelId = "test";

        DefaultMessageHandler handler = new DefaultMessageHandler();

        handler.channelCreated(channelId, 3);

        CompletableFuture<Integer> futureInt = handler.messageReceived(TestHelp.getMessage(channelId,
                "Bruce Lee"));

        Assert.assertTrue(0 == futureInt.get());

    }

}
