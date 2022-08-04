package com.wuhen.springwork.base;

import com.wuhen.springwork.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
@Data
public class Response<T> {
    private int code;
    private String msg;
    private T data;


    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <R> Response<R> ok(R data){
        return new Response<>(0,"success",data);
    }
    public static Response ok(){
        return new Response<>(0,"success",null);
    }

    public static Response error(ErrorCodeEnum errorCode){
        return new Response(errorCode.getCode(),errorCode.getMsg(),null);
    }
}
