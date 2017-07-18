package github.jadetang;

import github.jadetang.lock.Locks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午3:10
 */
public class Config {

    public static final Properties properties;
    public static final String FILE_SIZE = "file.size";
    public static final String DATA_DIR = "data.dir";
    public static final String LOCK = "queue.lock";
    public static final String CORE_SIZE ="thread.core.size";
    public static final String THREAD_MAX_SIZE = "thread.max.poolsize";




    static {
        properties = new Properties();
        try {
            InputStream inputStream = Config.class.getResourceAsStream("/my_queue.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("can not find my_queue.properties under class path");
        }
    }

    public static int fileSize() {
        String value = properties.getProperty(FILE_SIZE);
        return value != null ? Integer.parseInt(value.trim()) : 1024 * 1024 * 10;
    }

    public static String dataDir() {
        return properties.getProperty(DATA_DIR, System.getProperty("user.home", "")
                + File.separator + "my_queue_data") + File.separator;
    }

    public static Locks.LockType lockType() {
        String lockType = properties.getProperty(LOCK, "REENTRANT");
        return Locks.LockType.valueOf(lockType);
    }


    public static int threadPoolCoreSize() {
        return getInt(CORE_SIZE,5);
    }

    public static int threadMaxPoolSize(){
        return getInt(THREAD_MAX_SIZE,100);
    }


    private static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static int outputQueueSize(){
        return 500;
    }


}
