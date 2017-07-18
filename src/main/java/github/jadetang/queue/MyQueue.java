package github.jadetang.queue;

import github.jadetang.message.Message;

import java.util.Optional;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午2:14
 */
public interface MyQueue {

    /**
     * return the name of the queue
     *
     * @return the queue's name
     */
    String getName();

    /**
     * consume a message
     */
    Optional<Message> consume(int messageOffset);

    /**
     * append a new message to the queue, return -1 if the not succeed
     *
     * @param message the message to append
     * @return the offset of this message in this queue
     */
    int append(Message message);

    /**
     * consume the latest message
     *
     * @return the latest message
     */
    Optional<Message> consume();

    /**
     * return the max offset of the queue so far
     *
     * @return the max offset
     */
    int maxOffset();


}
