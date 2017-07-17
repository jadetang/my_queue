package github.jadetang.lock;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午4:37
 */
public interface PutMessageLock {

    void lock();

    void unlock();
}
