package github.jadetang.storage;

import github.jadetang.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sanguan.tangsicheng on 2017/7/15 下午3:12
 */
public class MappedFile {

    private static final Logger log = LoggerFactory.getLogger(MappedFile.class);
    private final AtomicInteger wrotePosition = new AtomicInteger(0);
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;
    private int fileSize;
    private File file;
    private String filename;

    public MappedFile(String filename, int fileSize) throws IOException {
        init(filename, fileSize);
    }

    private void init(String filename, int fileSize) throws IOException {
        this.fileSize = fileSize;
        this.filename = filename+".data";
        this.file = new File(Config.dataDir() + this.filename);
        log.debug("create new file {}", this.file.getAbsolutePath());
        ensureDir(this.file.getParent());
        boolean ready = false;
        try {
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
            ready = true;
        } catch (FileNotFoundException e) {
            log.error("create file channel " + this.filename + " failed.", e);
            throw e;
        } catch (IOException e) {
            log.error("map file " + this.filename + " failed.", e);
            throw e;
        } finally {
            if (!ready && this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }

    public int getWrotePosition() {
        return this.wrotePosition.get();
    }

    public void setWrotePosition(int pos) {
        this.wrotePosition.set(pos);
    }

    public boolean appendData(final byte[] data) {
        int currentPosition = getWrotePosition();
        if (currentPosition + data.length <= this.fileSize) {
            try {
                this.fileChannel.position(currentPosition);
                this.fileChannel.write(ByteBuffer.wrap(data));
                this.fileChannel.force(false);
            } catch (IOException e) {
                log.error("Failed when append data to mappedFile, " + this.filename, e);
            }
            this.wrotePosition.addAndGet(data.length);
            return true;
        } else {
            return false;
        }
    }


    public Optional<ByteBuffer> readData(int position, int size) {
        int wrotePosition = getWrotePosition();
        if (position + size <= wrotePosition) {
            ByteBuffer byteBuffer = this.mappedByteBuffer.slice();
            byteBuffer.position(position);
            ByteBuffer byteBufferNew = byteBuffer.slice();
            byteBufferNew.limit(size);
            return Optional.of(byteBufferNew.asReadOnlyBuffer());
        } else {
            return Optional.empty();
        }
    }


    private void ensureDir(String dirName) {
        if (dirName != null) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                assert result;
            }
        }
    }

}
