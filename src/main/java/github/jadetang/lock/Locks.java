package github.jadetang.lock;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午4:51
 */
public class Locks {

    public static PutMessageLock getLock(LockType lockType) {
        switch (lockType) {
            case SPIN:
                return new SpinPutMessageLock();
            case REENTRANT:
                return new ReentrantPutMessageLock();
            default:
                throw new IllegalArgumentException("do not support " + lockType.name());
        }
    }


    public enum LockType {

        SPIN, REENTRANT


    }

}
