package com.wuhen.future.wrapper;





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author chaoshunh
 * @create 2022/7/19
 */
public class CompletableFutureWrapper<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureWrapper.class);
    /**
     * 定时调度池
     */
    private static final ScheduledExecutorService TIMEOUT_POOL = Executors.newScheduledThreadPool(5);

    private ReentrantLock lock = new ReentrantLock();

    /**
     * 两个标志，是否正常执行设置结果、异步是否超时
     */
    private volatile boolean isComplete;
    private volatile boolean isTimeout;

    private CompletableFuture<T> future;

    /**
     * 超时执行策略
     */
    private Runnable timeoutStargy;

    public CompletableFutureWrapper() {
        this.future = new CompletableFuture<>();
    }

    /**
     * 尝试接收异步结果
     * @param action 结果回调
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return
     */
    public CompletableFutureWrapper<T> tryAccept(Consumer<? super T> action, long timeout, TimeUnit unit){
        future.thenAccept(action);
        TIMEOUT_POOL.schedule(() -> {
            if (!lock.tryLock()) return;//与complete互斥
            try {
                if (!isComplete && this.timeoutStargy != null){
                    this.isTimeout = true;
                    this.timeoutStargy.run();//执行超时策略
                }
            } catch (Exception e){
                logger.error("超时策略执行出现异常：",e);
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        },timeout, unit);
        return this;
    }

    /**
     * 设置异步超时策略
     * @param timeoutStrategy 超时策略
     */
    public void thenTimeout(Runnable timeoutStrategy){
        this.timeoutStargy = timeoutStrategy;
    }

    /**
     * 设置结果
     * @param result
     */
    public void complete(T result){
        if (!lock.tryLock()) return;//与超时策略执行互斥
        try {
            if (isTimeout) return;
            future.complete(result);
            isComplete = true;
        } catch (Exception e) {
            logger.error("设置结果出现异常：",e);
        } finally {
            lock.unlock();
        }
    }
}
