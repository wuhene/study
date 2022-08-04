package com.wuhen.springwork.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author chaoshunh
 * @create 2022/7/12
 */
public class JSONUtils {
    public static String toJson(Object object){
        return JSON.toJSONString(object);
    }

    /**
     * 反序列化json
     * @param json json串
     * @param tClass 对象class类型
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T fromJson(String json,Class<T> tClass){
        return JSON.parseObject(json,tClass);
    }

    /**
     * 反序列化json为list
     * @param json json传
     * @param tClass 对象class类型
     * @param <T> 泛型
     * @return list集合
     */
    public static <T> List<T> fromJson2List(String json,Class<T> tClass){
        return JSON.parseArray(json,tClass);
    }
}
