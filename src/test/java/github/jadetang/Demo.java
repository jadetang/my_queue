package github.jadetang;

import github.jadetang.Util.MockMessageHandler;
import github.jadetang.Util.TestHelp;
import github.jadetang.message.Message;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author sanguan.tangsicheng on 2017/7/18 下午6:29
 */
public class Demo {


    List<String> channels = Arrays.asList("c1", "c2", "c3", "c4");

    @Test
    public void demoTest() throws InterruptedException {

        ChannelListener listener = ChannelListener.getInstance();

        //  listener.registerMessageHandler(new DefaultMessageHandler());
        listener.registerMessageHandler(new MockMessageHandler());

        channels.forEach(c -> {
            try {
                listener.createChannel(c, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listener.startConsumeMessage();
        Thread producer = new Thread(new MessageProduce(listener, channels, 1000));
        MessageConsumer consumer = new MessageConsumer(listener);
        Thread consumerThread = new Thread(consumer);

        producer.start();
        consumerThread.start();
        producer.join();
        TimeUnit.SECONDS.sleep(10);
        consumer.finish();
        consumerThread.join();
    }


    public static class MessageConsumer implements Runnable {

        private ChannelListener listener;

        private AtomicBoolean shouldFinish;

        private int count = 0;

        MessageConsumer(ChannelListener listener) {
            this.listener = listener;
            this.shouldFinish = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            while (!shouldFinish.get()) {
                Message m = listener.getQueue().poll();
                if (m != null) {
                    System.out.println(m);
                    count++;
                }
            }
            System.out.println("now should finish");
            while (!listener.getQueue().isEmpty()) {
                System.out.println(listener.getQueue().poll());
                count++;
            }
            System.out.println(String.format("consume %d message", count));
        }

        public void finish() {
            this.shouldFinish.compareAndSet(false, true);
        }

    }


    public static class MessageProduce implements Runnable {

        ChannelListener listener;

        List<String> channels;

        int messageNum;

        MessageProduce(ChannelListener listener, List<String> channels, int messageNum) {
            this.listener = listener;
            this.channels = channels;
            this.messageNum = messageNum;
        }


        @Override
        public void run() {
            for (int i = 0; i < messageNum; i++) {
                int finalI = i;
                channels.forEach(c -> listener.receiveMessage(
                        TestHelp.getMessage(c, String.format("message no:%s_%d", c, finalI))));
            }
        }
    }


}
