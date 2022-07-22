package com.wuhen.exam1.adapter;

import java.util.concurrent.Future;

/**
 * @author chaoshunh
 * @create 2022/7/21
 */
public interface BaseAdapter<T> {
    Future<T> convert();
}
