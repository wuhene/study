package com.wuhen.springwork.dao;

import com.wuhen.springwork.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
@Mapper
@Repository
public interface UserMapper {
    /**
     * 保存用户信息到数据库
     * @param user 用户对象
     * @return 主键id
     */
    void saveUser(User user);

    /**
     * 删除某个用户
     * @param uid 用户id
     */
    void deleteUser(long uid);


    /**
     * 修改某个用户信息
     * @param user 用户对象
     * @return 修改成功条数
     */
    int updateUser(User user);

    /**
     * 查询用户列表,只查uid和name和username
     * @return 用户对象列表
     */
    List<User> queryUserList();

    /**
     *查询用户详细信息
     * @param uid 用户id
     * @return 用户详细信息
     */
    User queryUserDetailByUid(long uid);

    /**
     *查询用户详细信息
     * @param username 用户昵称
     * @return 用户详细信息
     */
    User queryUserDetailByUsername(String username);


}
