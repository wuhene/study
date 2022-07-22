package com.wuhen.exam1;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.wuhen.exam1.utils.FutureConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;


/**
 * @author chaoshunh
 * @create 2022/7/21
 */
public class Exam1Test {
    private static final Logger logger = LoggerFactory.getLogger(Exam1Test.class);
    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * 将CompletableFuture转换为ListenableFuture
     */
    public static void test1(){
        ExecutorService pool = Executors.newFixedThreadPool(5);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        //睡眠一秒后，completableFuture设置返回值，如果一秒后下面的listenableFuture监听到了值说明转换没问题
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                completableFuture.complete("CompletableFuture转换为ListenableFuture无误");
            } catch (InterruptedException e) {
                logger.error("异常：",e);
            }
        }).start();


        ListenableFuture<String> listenableFuture = FutureConvertUtils.completableFuture2ListenableFuture(completableFuture);
        listenableFuture.addListener(() -> {
            try {
                logger.info(listenableFuture.get());
            } catch (Exception e) {
                logger.error("异常：",e);
            }
        },pool);
    }

    /**
     * 将ListenableFuture转换为CompletableFuture
     */
    public static void test2(){
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
        //睡眠一秒后，listenableFuture设置返回值，如果一秒后下面的completableFuture获取到了值说明转换没问题
        ListenableFuture<String> listenableFuture = pool.submit(() ->{
            try {
                Thread.sleep(1000);
                return  "ListenableFuture转换为CompletableFuture无误";
            } catch (InterruptedException e) {
                logger.error("异常：",e);
                return null;
            }
        });


        CompletableFuture<String> completableFuture = FutureConvertUtils.listenableFuture2CompletableFuture(listenableFuture);
        completableFuture.thenRunAsync(() -> {
            try {
                logger.info(completableFuture.get());
            } catch (Exception e) {
                logger.error("异常：",e);
            }
        },pool);
    }



}
