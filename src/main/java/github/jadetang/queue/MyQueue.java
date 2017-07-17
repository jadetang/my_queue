package github.jadetang.queue;

import github.jadetang.message.Message;

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
     *
     * @param messageOffset the messageOffset of the message to be consumed
     * @return null if the messageId is not valid
     */
    Message consume(int messageOffset);

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
    Message consume();

    /**
     * return the max offset of the queue so far
     *
     * @return the max offset
     */
    int maxOffset();


}
