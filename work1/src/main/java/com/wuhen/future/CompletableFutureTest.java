package com.wuhen.future;

import com.wuhen.future.wrapper.CompletableFutureWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author chaoshunh
 * @create 2022/7/19
 */
public class CompletableFutureTest {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureTest.class);

    public static void main(String[] args) throws InterruptedException {
        CompletableFutureWrapper<String> futureWrapper = new CompletableFutureWrapper<>();
        long timeout = 1000;
        futureWrapper
                .tryAccept(s -> {
                    logger.info(s);
                }, timeout, TimeUnit.MILLISECONDS)
                .thenTimeout(() -> {
                    logger.warn("执行超时");
                });
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(timeout + 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            futureWrapper.complete("响应成功！");
        });
        thread.start();
    }

}
