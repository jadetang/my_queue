package github.jadetang;

import github.jadetang.handler.MessageHandler;
import github.jadetang.message.Message;
import github.jadetang.provider.OrderedMessageQueueProvider;
import github.jadetang.thread.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author sanguan.tangsicheng on 2017/7/18 上午7:51
 */
public class ChannelListener implements OrderedMessageQueueProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelListener.class);

    private static final ChannelListener instance = new ChannelListener();

    private MessageHandler messageHandler;

    private Map<String, ConcurrentLinkedQueue<CompletableFuture<Integer>>> messageStore;

    private ConcurrentHashMap<String, Integer> transferSize;

    private ArrayBlockingQueue<Message> internalQueue;

    private ExecutorService consumeMessageService;

    private boolean start;
    private Set<String> channelIdSet;


    private ChannelListener() {
        messageStore = new HashMap<>();
        internalQueue = new ArrayBlockingQueue<>(Config.outputQueueSize());
        transferSize = new ConcurrentHashMap<>();
        consumeMessageService = ExecutorFactory.executor("consume-thread-%d", 10, 100);
        start = false;
        channelIdSet = new HashSet<>();
    }

    public synchronized void startConsumeMessage() {

        if (messageHandler == null) {
            throw new IllegalStateException("the message handler is null,can not start.");
        }
        if (transferSize.isEmpty()) {
            throw new IllegalStateException("at least one channel should be created by start.");
        }
        if (!start) {
            consumeMessageService.execute(() -> {
                while (true) {
                    List<Message> messages =
                            transferSize.entrySet().parallelStream().map(
                                    e -> {
                                        String channelId = e.getKey();
                                        Integer transferSize = e.getValue();
                                        ConcurrentLinkedQueue<CompletableFuture<Integer>> offsetQueue = messageStore.get(channelId);
                                        return consumeMessage(offsetQueue, channelId, transferSize);
                                    }
                            ).flatMap(Collection::stream).collect(Collectors.toList());

                    for (Message m : messages) {
                        try {
                            internalQueue.put(m);
                        } catch (InterruptedException e) {
                            logger.error("error when putting message into queue,{}", m);
                            continue;
                        }
                    }
                }
            });
        }
        start = true;
    }

    public Collection<String> getChannelId() {
        return Collections.unmodifiableCollection(transferSize.keySet());

    }


    public static ChannelListener getInstance() {
        return instance;
    }


    public Queue<Message> getQueue() {
        return internalQueue;
    }

    public void receiveMessage(Message message) {
        if (channelIdSet.contains(message.getSourceChannelId())) {
            CompletableFuture<Integer> offsetFuture = messageHandler.messageReceived(message);
            messageStore.get(message.getSourceChannelId()).offer(offsetFuture);
        } else {
            logger.error("receive a message, but the channel do not exist, so ignore, message {}", message);
        }
    }

    private List<Message> consumeMessage(Queue<CompletableFuture<Integer>> offsetQueue, String channelId, Integer transferSize) {
        if (offsetQueue == null || offsetQueue.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Message> messageList = new ArrayList<>(transferSize);
            while (transferSize > 0) {
                CompletableFuture<Integer> offsetFuture = offsetQueue.poll();
                transferSize--;
                if (offsetFuture != null) {
                    try {
                        int offset = offsetFuture.get();
                        if (offset != -1) {
                            Optional<Message> messageOption = messageHandler.consumeMessage(channelId, offset);
                            messageOption.ifPresent(messageList::add);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("error when consuming message, channel id {}", channelId);
                    }
                }

            }
            return messageList;
        }
    }

    public synchronized void createChannel(String channelId, int transferSize) throws IOException {
        if (transferSize < 1 || transferSize > 10) {
            throw new IllegalArgumentException("transfer size should between 1 and 10");
        }
        this.messageHandler.channelCreated(channelId);
        this.transferSize.put(channelId, transferSize);
        this.messageStore.put(channelId, new ConcurrentLinkedQueue<>());
        this.channelIdSet.add(channelId);
    }

    public synchronized void registerMessageHandler(MessageHandler messageHandler) {
        if (this.messageHandler == null) {
            this.messageHandler = messageHandler;
        } else {
            throw new IllegalArgumentException("The message handler already exists, " + messageHandler.getClass());
        }
    }

    public synchronized void removeChannel(String channelId) {
        if (channelIdSet.remove(channelId)) {
            messageHandler.channelDestroyed(channelId);
        }

    }


}
