package com.wuhen.exam1.adapter.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.wuhen.exam1.adapter.BaseAdapter;

import java.util.concurrent.*;

/**
 * 将ListenableFuture适配转换为CompletableFuture
 * @author chaoshunh
 * @create 2022/7/22
 */
public class CompletableFutureAdapter<T> implements BaseAdapter<T> {
    /**
     * 被适配者
     */
    private ListenableFuture<T> adaptee;

    public CompletableFutureAdapter(ListenableFuture<T> adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public Future<T> convert() {
        return new CompletableFutureHandler();
    }

    /**
     * 处理器
     */
    class CompletableFutureHandler extends CompletableFuture<T> {
        @Override
        public CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
            adaptee.addListener(action, executor);
            return (CompletableFuture<Void>) this;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return adaptee.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return adaptee.isCancelled();
        }

        @Override
        public boolean isDone() {
            return adaptee.isDone();
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return adaptee.get();
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return adaptee.get(timeout, unit);
        }
    }
}
