package com.wuhen.exam1.adapter.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.wuhen.exam1.adapter.BaseAdapter;

import java.util.concurrent.*;

/**
 * 将CompletableFuture适配转换为ListenableFuture
 * @author chaoshunh
 * @create 2022/7/22
 */
public class ListenableFutureAdapter<T> implements BaseAdapter<T> {
    /**
     * 被适配者
     */
    private CompletableFuture<T> adaptee;

    public ListenableFutureAdapter(CompletableFuture<T> adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public Future<T> convert() {
        return new ListenableFutureHandler();
    }

    /**
     * 处理器
     */
    class ListenableFutureHandler implements ListenableFuture<T> {

        @Override
        public void addListener(Runnable runnable, Executor executor) {
            adaptee.thenRunAsync(runnable,executor);
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
