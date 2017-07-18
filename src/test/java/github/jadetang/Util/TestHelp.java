package github.jadetang.Util;

import github.jadetang.message.Message;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午5:37
 */
public class TestHelp {



    public static Message getMessage(String channelId, String text) {

        assert channelId != null;

        return getMessage(null, channelId, text);

    }


    public static Message getMessage(Long messageId, String sourceChannelId, String text) {

        assert text != null;

        Message message = new Message(2L, sourceChannelId, text.getBytes());

        return message;

    }
}
