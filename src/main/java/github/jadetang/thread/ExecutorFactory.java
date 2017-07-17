package github.jadetang.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sanguan.tangsicheng on 2017/7/17 下午6:44
 */
public class ExecutorFactory {

    public static ExecutorService executor(String namePattern, int coresize, int maxPoolSize) {
        String.format(namePattern, 1);  //just check the pattern is valid,throw exception early
        ThreadFactory threadFactory = new CustomThreadFactory(namePattern);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maxPoolSize);
        return new ThreadPoolExecutor(coresize, maxPoolSize, 10, TimeUnit.SECONDS, queue, threadFactory);
    }


    private static class CustomThreadFactory implements ThreadFactory {


        private String namePattern;

        private AtomicInteger count = new AtomicInteger(0);

        CustomThreadFactory(String threadNamePattern) {
            this.namePattern = threadNamePattern;
            count = new AtomicInteger(0);
        }


        @Override
        public Thread newThread(Runnable r) {
            if (namePattern != null) {
                return new Thread(r, String.format(namePattern, count.incrementAndGet()));
            } else {
                return new Thread(r);
            }
        }
    }
}
