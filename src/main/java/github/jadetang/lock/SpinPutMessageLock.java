package github.jadetang.lock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spin lock used in high race condition, could reduce the overhead of context switching.
 *
 * @author sanguan.tangsicheng on 2017/7/16 下午4:45
 */
public class SpinPutMessageLock implements PutMessageLock {


    private AtomicBoolean putMessageSpinLock = new AtomicBoolean(true);

    @Override
    public void lock() {
        boolean flag;
        do {
            flag = this.putMessageSpinLock.compareAndSet(true, false);
        }
        while (!flag);
    }

    @Override
    public void unlock() {
        this.putMessageSpinLock.compareAndSet(false, true);
    }
}
