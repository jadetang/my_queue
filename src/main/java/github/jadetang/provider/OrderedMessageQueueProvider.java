package github.jadetang.provider;

import github.jadetang.message.Message;

import java.util.Queue;

/**
 * @author sanguan.tangsicheng on 2017/7/18 上午7:50
 */
public interface OrderedMessageQueueProvider {

    Queue<Message> getQueue();
}
