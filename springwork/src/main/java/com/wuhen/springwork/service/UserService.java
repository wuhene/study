package com.wuhen.springwork.service;

import com.wuhen.springwork.model.User;
import com.wuhen.springwork.base.Response;

import java.util.List;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
public interface UserService {
    /**
     * 新增用户
     * @param user 用户对象
     * @return 此条记录主键
     */
    Response<Long> addUser(User user);

    /**
     * 删除用户
     * @param uid 要删除的用户id
     */
    Response delUser(Long uid);

    /**
     * 修改用户信息
     * @param user 要修改的用户对象
     * @return 修改成功条数
     */
    Response updateUser(User user);

    /**
     * 获取用户列表, 只有uid，name，username 根据页数和每页个数
     * @param pageNum 当前页
     * @param pageSize 每页个数
     * @return
     */
    Response<List<User>> getUserList(int pageNum, int pageSize);

    /**
     * 获取用户详细信息
     * @param uid 用户id
     * @return
     */
    Response<User> getUserDetail(long uid);

}
