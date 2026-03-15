package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.User;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户业务实现。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>项目使用 MyBatis-Plus {@link QueryWrapper} 组合查询条件。</li>
 *   <li>管理员/普通用户登录通过 role 字段区分（role>1 / role=1）。</li>
 *   <li>token 查询用于接口鉴权（由拦截器从请求头 TOKEN 读取）。</li>
 * </ul>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 查询用户列表（支持关键词，具体筛选条件以实现为准）。
     *
     * <p>注意：当前代码保留了 keyword 的入口，但尚未追加具体的 like 条件，
     * 因此 keyword 目前不会影响查询结果。</p>
     */
    @Override
    public List<User> getUserList(String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(keyword)) {
            // 预留：可在这里追加 username / phone 等字段的模糊查询
        }
        queryWrapper.orderBy(true, false, "create_time");
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 管理端登录校验：username + password（已加密）+ role > 1。
     */
    @Override
    public User getAdminUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.gt("role", "1");
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 普通用户登录校验：username + password（已加密）+ role = 1。
     */
    @Override
    public User getNormalUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.eq("role", "1");
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 创建用户。
     */
    @Override
    public void createUser(User user) {
        userMapper.insert(user);
    }

    /**
     * 根据ID删除用户。
     */
    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }

    /**
     * 更新用户信息。
     */
    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    /**
     * 通过 token 查询用户（用于接口鉴权）。
     */
    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 通过用户名查询用户（用于注册时的唯一性校验）。
     */
    @Override
    public User getUserByUserName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 获取用户详情。
     */
    @Override
    public User getUserDetail(String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
}
