package io.github.zyszero.utils;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * util for thread and schedule.
 * @Author: zyszero
 * @Date: 2024/5/23 22:31
 */
public interface ThreadUtils {
    Scheduler Default = new SchedulerImpl();

    static Scheduler getDefault() {
        if(Default.isInitialized()) {
            return Default;
        }
        int coreSize = Integer.parseInt(System.getProperty("utils.task.coreSize", "1"));
        Default.init(coreSize);
        return Default;
    }

    interface Scheduler {
        boolean isInitialized();
        void init(int coreSize);
        void shutdown();
        ScheduledFuture<?> schedule(Runnable runnable, long delay, long interval);
    }

    class SchedulerImpl implements Scheduler {
        @Getter
        boolean initialized = false;
        ScheduledExecutorService executor;

        public void init(int coreSize) {
            executor = Executors.newScheduledThreadPool(coreSize);
            initialized = true;
        }

        @Override
        public void shutdown() {
            executor.shutdown();
            try {
                executor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);
                if(!executor.isTerminated()) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // ignore it
                //throw new RuntimeException(e);
            }
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable runnable, long delay, long interval) {
            return executor.scheduleAtFixedRate(runnable, delay,
                    interval, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
}
