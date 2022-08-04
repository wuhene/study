package com.wuhen.springwork.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
@Data
public class User {
    private Long uid;
    private String name;
    private String username;
    private int sex;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date updateTime;
}
