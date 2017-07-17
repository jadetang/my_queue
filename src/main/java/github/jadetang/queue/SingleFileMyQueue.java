package github.jadetang.queue;

import github.jadetang.Config;
import github.jadetang.index.HashIndex;
import github.jadetang.index.QueueIndex;
import github.jadetang.lock.Locks;
import github.jadetang.lock.PutMessageLock;
import github.jadetang.message.Message;
import github.jadetang.storage.MappedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * a message queue using a single file to store, the max size of a single file is defined by
 * {@link github.jadetang.Config#fileSize()}. It not unbound, but better than a memory based queue
 *
 * @author sanguan.tangsicheng on 2017/7/16 下午3:08
 */
public class SingleFileMyQueue implements MyQueue {


    private static final Logger logger = LoggerFactory.getLogger(SingleFileMyQueue.class);


    private String name;

    private MappedFile mappedFile;

    private QueueIndex index;

    private PutMessageLock lock;

    private AtomicInteger messageOffset = new AtomicInteger(-1);

    public SingleFileMyQueue(String name) throws IOException {
        this.name = name;
        mappedFile = new MappedFile(this.name, Config.fileSize());
        index = new HashIndex();
        lock = Locks.getLock(Config.lockType());
    }

    public void setMessageOffset(int messageOffset) {
        this.messageOffset.set(messageOffset);
    }

    public String getName() {
        return name;
    }

    public Message consume(int messageOffset) {
        int fileOffset = index.find(messageOffset);
        Optional<ByteBuffer> byteBufferOptional = mappedFile.readData(fileOffset, 4 + 4);
        if (byteBufferOptional.isPresent()) {
            ByteBuffer byteBuffer = byteBufferOptional.get();
            int fOffset = byteBuffer.getInt();
            assert fOffset == fileOffset;
            int messageLength = byteBuffer.getInt();
            return consume(fileOffset, messageLength);
        } else {
            return null;
        }
    }

    private Message consume(int fileOffset, int messageLength) {
        Optional<ByteBuffer> byteBufferOptional = mappedFile.readData(fileOffset, messageLength);
        if (byteBufferOptional.isPresent()) {
            ByteBuffer byteBuffer = byteBufferOptional.get();
            byteBuffer.position(4);
            int length = byteBuffer.getInt();
            assert messageLength == length;
            long messageId = byteBuffer.getLong();
            int messageChannelLength = byteBuffer.getInt();
            byte[] data = new byte[messageChannelLength];
            int i = 0;
            while (i < messageChannelLength) {
                data[i++] = byteBuffer.get();
            }
            String messageChannelId = new String(data);
            data = new byte[byteBuffer.remaining()];
            i = 0;
            while (byteBuffer.hasRemaining()) {
                data[i++] = byteBuffer.get();
            }
            return new Message(messageId, messageChannelId, data);
        } else {
            return null;
        }

    }

    public int append(Message message) {
        assert message != null;
        lock.lock();
        try {
            int fileOffset = mappedFile.getWrotePosition();
            byte[] data = extractData(fileOffset, message);
            boolean success = mappedFile.appendData(data);
            if (success) {
                this.messageOffset.incrementAndGet();
                index.addEntry(maxOffset(), fileOffset);
                return maxOffset();
            } else {
                return -1;
            }

        } finally {
            lock.unlock();
        }
    }


    private byte[] extractData(int offset, Message message) {
        int messageLength = calculateLength(message);
        ByteBuffer byteBuffer = ByteBuffer.allocate(messageLength);
        byteBuffer.putInt(offset);
        byteBuffer.putInt(messageLength);
        byteBuffer.putLong(message.getMessageId());
        byteBuffer.putInt(message.getSourceChannelId().getBytes().length);
        byteBuffer.put(message.getSourceChannelId().getBytes());
        byteBuffer.put(message.getPayload());
        logger.debug("offset:{},message length:{},message id:{},channel id:{}",
                offset, messageLength, message.getMessageId(), message.getSourceChannelId());
        assert byteBuffer.position() == messageLength;
        return byteBuffer.array();
    }

    private int calculateLength(Message message) {
        return Integer.BYTES // offset
                + Integer.BYTES // message length
                + Long.BYTES // message id
                + Integer.BYTES // message channel id length
                + message.getSourceChannelId().getBytes().length +
                +(message.getPayload() == null ? 0 : message.getPayload().length); // message load length
    }

    public Message consume() {
        return consume(maxOffset());
    }

    public int maxOffset() {
        return messageOffset.get();
    }

}
