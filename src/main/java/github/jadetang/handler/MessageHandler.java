package github.jadetang.handler;

import github.jadetang.message.Message;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author sanguan.tangsicheng on 2017/7/17 下午4:24
 */
public interface MessageHandler {

    CompletableFuture<Integer> messageReceived(Message m);


    void channelCreated(String channelId,int transferSize) throws IOException;


    void channelDestroyed(String channelId);

}
