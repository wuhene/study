package com.wuhen.springwork.controller;

import com.wuhen.springwork.base.Response;
import com.wuhen.springwork.model.User;
import com.wuhen.springwork.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 新增用户
     * @param user 用户对象
     * @return 数据库主键
     */
    @PostMapping("/addUser")
    public Response<Long> addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    /**
     * 删除用户
     * @param user 用户对象，里面只需要uid字段
     * @return 结果
     */
    @PostMapping("/delUser")
    public Response delUser(@RequestBody User user) {
        return userService.delUser(user.getUid());
    }

    /**
     * 修改用户信息
     * @param user 用户对象
     * @return 结果条数
     */
    @PostMapping("/updateUser")
    public Response updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 获取用户列表,只有uid，name，username 根据页数和每页个数
     * @param pageNum 当前页
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/getUserList/{pageNum}/{pageSize}")
    public Response<List<User>> getUserList(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        return userService.getUserList(pageNum, pageSize);
    }

    /**
     * 获取用户详细信息
     * @param uid 用户id
     * @return
     */
    @GetMapping("/getUserDetail/{uid}")
    public Response<User> getUserDetail(@PathVariable("uid") long uid){
        return userService.getUserDetail(uid);
    }
}
