package com.wuhen.springwork.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Strings;
import com.wuhen.springwork.anno.LogRecord;
import com.wuhen.springwork.base.Response;
import com.wuhen.springwork.enums.ErrorCodeEnum;
import com.wuhen.springwork.dao.UserMapper;
import com.wuhen.springwork.model.User;
import com.wuhen.springwork.service.UserService;
import com.wuhen.springwork.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logeer = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final long INVALID_UID = 0;
    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    @LogRecord(tag = "UserServiceImpl->addUser(User user)")//记录日志并捕获异常
    public Response<Long> addUser(User user) {
        if (!checkUser(user)) {
            logeer.error("addUser 请求参数错误：{}", JSONUtils.toJson(user));
            return Response.error(ErrorCodeEnum.PARAM_ERROR);
        }
        //昵称不能重复
        User dbUser = userMapper.queryUserDetailByUsername(user.getUsername());
        if (!Objects.isNull(dbUser)){
            return Response.error(ErrorCodeEnum.USERNAME_AL_EXIST);
        }
        user.setDescription(Strings.nullToEmpty(user.getDescription()));
        userMapper.saveUser(user);
        logeer.info("新增用户成功，uid：{}",user.getUid());
        return Response.ok(user.getUid());
    }

    /**
     * 检查参数是否正确
     * @param user
     * @return
     */
    private boolean checkUser(User user){
        if (Objects.isNull(user)) return false;
        if (Strings.isNullOrEmpty(user.getName())) return false;
        if (Strings.isNullOrEmpty(user.getUsername())) return false;
        return true;
    }

    @Override
    @Transactional
    @LogRecord(tag = "UserServiceImpl->delUser(List<BigInteger> uid)")//记录日志并捕获异常
    public Response delUser(Long uid) {
        if (Objects.isNull(uid) || uid <= INVALID_UID) {
            logeer.error("请求参数错误：{}", uid);
            return Response.error(ErrorCodeEnum.PARAM_ERROR);
        }
        userMapper.deleteUser(uid);
        return Response.ok();
    }

    @Override
    @Transactional
    @LogRecord(tag = "UserServiceImpl->updateUser(List<User> user)")//记录日志并捕获异常
    public Response updateUser(User user) {
        if (Objects.isNull(user) || Objects.isNull(user.getUid()) || user.getUid() <= INVALID_UID) {
            logeer.error("updateUser 请求参数错误：{}", JSONUtils.toJson(user));
            return Response.error(ErrorCodeEnum.PARAM_ERROR);
        }
        User dbUser = userMapper.queryUserDetailByUsername(user.getUsername());
        //该用户不存在
        if (Objects.isNull(dbUser)) {
            return Response.error(ErrorCodeEnum.USER_UNEXIT);
        }
        //数据库已有的昵称跟当前请求的不是同一个说明当前请求user要改的昵称重名了
        if (!Objects.equals(user.getUsername(),dbUser.getUsername())){
            return Response.error(ErrorCodeEnum.USERNAME_AL_EXIST);
        }
        user.setDescription(Strings.isNullOrEmpty(user.getDescription()) ? dbUser.getDescription() : user.getDescription());
        int count = userMapper.updateUser(user);
        if (count > 0){
            return Response.ok();
        }
        logeer.error("修改失败，uid：{}",user.getUid());
        return Response.error(ErrorCodeEnum.UPDATE_FAIL);//修改成功条数为0
    }

    @Override
    @LogRecord(tag = "UserServiceImpl->updateUser(List<User> user)")//记录日志并捕获异常
    public Response<List<User>> getUserList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize,false);
        List<User> userList = userMapper.queryUserList();
        return Response.ok(userList);
    }

    @Override
    @LogRecord(tag = "UserServiceImpl->getUserDetail(long uid)")
    public Response<User> getUserDetail(long uid) {
        if (uid <= INVALID_UID) {
            logeer.error("getUserDetail 请求参数错误：{}", uid);
            return Response.error(ErrorCodeEnum.PARAM_ERROR);
        }
        return Response.ok(userMapper.queryUserDetailByUid(uid));
    }
}
