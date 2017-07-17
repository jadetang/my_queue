package github.jadetang.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午4:41
 */
public class ReentrantPutMessageLock implements PutMessageLock {


    //no fair lock improve the overall throughput, but the waiting time will be vary
    private ReentrantLock putMessageNormalLock = new ReentrantLock();

    @Override
    public void lock() {
        putMessageNormalLock.lock();
    }

    @Override
    public void unlock() {
        putMessageNormalLock.unlock();
    }
}
