package github.jadetang.handler;

import github.jadetang.Config;
import github.jadetang.message.Message;
import github.jadetang.queue.MyQueue;
import github.jadetang.queue.SingleFileMyQueue;
import github.jadetang.thread.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author sanguan.tangsicheng on 2017/7/17 下午6:30
 */
public class DefaultMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageHandler.class);

    private Map<String, MyQueue> channels;
    private Map<String, Integer> transferSizeMap;
    private ExecutorService receiveMessageService;


    public DefaultMessageHandler() {
        channels = new HashMap<>();
        transferSizeMap = new HashMap<>();
        receiveMessageService = ExecutorFactory.executor("receive-message-thread-%d"
                , Config.threadPoolCoreSize(), Config.threadMaxPoolSize());
    }


    @Override
    public CompletableFuture<Integer> messageReceived(Message m) {
        MyQueue queue = channels.get(m.getSourceChannelId());
        if (queue == null) {
            return CompletableFuture.completedFuture(-1);
        } else {
            //using same thread to prepare message and append prepared message to the queue in order to
            //to reduce the context switching overhead
            return CompletableFuture.supplyAsync(() -> {
                logMessage(m);
                m.prepare();
                return m;
            }, receiveMessageService).thenApply(message -> queue.append(m))
                    .exceptionally(throwable -> {
                        logger.error(String.format(
                                "append message error, message:%s, channelId:%s"
                                , m, channels), throwable);
                        return -1;
                    });
        }


    }

    private void logMessage(Message m) {
        logger.info("receive message:{}", m);
    }

    @Override
    public synchronized void channelCreated(String channelId, int transferSize) throws IOException {
        if (channels.containsKey(channelId)) {
            logger.warn("the channelId:{} already exist", channelId);
        } else {
            channels.put(channelId, new SingleFileMyQueue(channelId));
            transferSizeMap.put(channelId, transferSize);
        }
    }

    @Override
    public synchronized void channelDestroyed(String channelId) {
        MyQueue q = channels.remove(channelId);
        transferSizeMap.remove(channelId);
        if (q == null) {
            logger.warn("the channel:{} to remove is not existed", channelId);
        } else {
            // the queue should dispose
        }

    }
}
