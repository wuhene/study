package com.wuhen.exam1.utils;

import com.google.common.util.concurrent.ListenableFuture;
import com.wuhen.exam1.adapter.BaseAdapter;
import com.wuhen.exam1.adapter.impl.CompletableFutureAdapter;
import com.wuhen.exam1.adapter.impl.ListenableFutureAdapter;

import java.util.concurrent.CompletableFuture;

/**
 * @author chaoshunh
 * @create 2022/7/22
 */
public class FutureConvertUtils {
    /**
     * 将listenableFuture转为completableFuture
     * @param listenableFuture
     * @param <T>
     * @return
     */
    public static  <T> CompletableFuture<T> listenableFuture2CompletableFuture(ListenableFuture<T> listenableFuture){
        BaseAdapter<T> adapter = new CompletableFutureAdapter<>(listenableFuture);
        return (CompletableFuture<T>) adapter.convert();
    }

    /**
     * 将completableFuture转为listenableFuture
     * @param completableFuture
     * @param <T>
     * @return
     */
    public static <T> ListenableFuture<T> completableFuture2ListenableFuture(CompletableFuture<T> completableFuture){
        BaseAdapter<T> adapter = new ListenableFutureAdapter<>(completableFuture);
        return (ListenableFuture<T>) adapter.convert();
    }
}
