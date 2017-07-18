package github.jadetang.handler;

import github.jadetang.message.Message;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author sanguan.tangsicheng on 2017/7/17 下午4:24
 */
public interface MessageHandler {

    CompletableFuture<Integer> messageReceived(Message m);

    /**
     * create a channel, should be thread saft
     * @param channelId
     * @throws IOException
     */
    void channelCreated(String channelId) throws IOException;


    void channelDestroyed(String channelId);

    /**
     * consume a meesage in a channel identified by messageOffset
     * @param channelId
     * @param messageOffset
     * @return
     */
    Optional<Message> consumeMessage(String channelId, int messageOffset);



}
