package com.wuhen.springwork.enums;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
public enum ErrorCodeEnum {
    PARAM_ERROR(10001,"请检查参数是否正确"),
    UPDATE_FAIL(10002,"修改数据失败"),
    USERNAME_AL_EXIST(10003,"该昵称已被使用"),
    USER_UNEXIT(10004,"该用户不存在"),
    EXCEPTION(500,"服务端出现异常");
    private int code;
    private String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
