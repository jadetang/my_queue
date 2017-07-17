package github.jadetang.message;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午2:36
 */
public class Message {

    long messageId;
    String sourceChannelId;
    byte[] payload;

    public Message(long messageId, String sourceChannelId, byte[] payload) {
        this.messageId = messageId;
        this.sourceChannelId = sourceChannelId;
        this.payload = payload;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSourceChannelId() {
        return sourceChannelId;
    }

    public void setSourceChannelId(String sourceChannelId) {
        this.sourceChannelId = sourceChannelId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    //simulate propare process
    public void prepare() {
        Random random = new Random();
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        } catch (InterruptedException ignore) {

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        if (getMessageId() != message.getMessageId()) return false;
        if (!getSourceChannelId().equals(message.getSourceChannelId())) return false;
        return Arrays.equals(getPayload(), message.getPayload());
    }

    @Override
    public int hashCode() {
        int result = (int) (getMessageId() ^ (getMessageId() >>> 32));
        result = 31 * result + getSourceChannelId().hashCode();
        result = 31 * result + Arrays.hashCode(getPayload());
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", sourceChannelId='" + sourceChannelId + '\'' +
                '}';
    }
}
