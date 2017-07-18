package github.jadetang.Util;

import github.jadetang.handler.MessageHandler;
import github.jadetang.message.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * a mock class used in unit test
 *
 * @author sanguan.tangsicheng on 2017/7/18 下午2:31
 */
public class MockMessageHandler implements MessageHandler {

    private Map<String, Integer> offset;

    private Map<String, Map<Integer, Message>> map;


    public MockMessageHandler() {
        offset = new HashMap<>();
        map = new HashMap<>();
    }

    @Override
    public synchronized CompletableFuture<Integer> messageReceived(Message m) {
        assert m != null;
        String channelId = m.getSourceChannelId();

        offset.put(channelId, offset.get(channelId) + 1);

        int i = offset.get(channelId);


        map.get(channelId).put(i, m);

        return CompletableFuture.completedFuture(i);

    }

    @Override
    public synchronized void channelCreated(String channelId) throws IOException {
        offset.put(channelId, -1);
        map.put(channelId, new HashMap<>());
    }

    @Override
    public synchronized void channelDestroyed(String channelId) {
        //do nothing
    }

    @Override
    public Optional<Message> consumeMessage(String channelId, int messageOffset) {
        return Optional.ofNullable(map.get(channelId).get(messageOffset));
    }
}
