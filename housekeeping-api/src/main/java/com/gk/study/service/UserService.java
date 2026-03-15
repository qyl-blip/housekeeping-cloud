package com.gk.study.service;


import com.gk.study.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 查询用户列表（支持关键词）。
     *
     * @param keyword 关键词（通常用于匹配用户名等字段；具体实现以 serviceImpl 为准）
     * @return 用户列表
     */
    List<User> getUserList(String keyword);

    /**
     * 管理端登录校验：用户名 + 密码（已加密）+ role > 1。
     *
     * @param user 包含用户名/密码的对象
     * @return 匹配到的用户（找不到返回 null）
     */
    User getAdminUser(User user);

    /**
     * 普通用户登录校验：用户名 + 密码（已加密）+ role = 1。
     *
     * @param user 包含用户名/密码的对象
     * @return 匹配到的用户（找不到返回 null）
     */
    User getNormalUser(User user);

    /**
     * 创建用户。
     *
     * @param user 用户实体（调用前一般会完成密码加密、token 生成等）
     */
    void createUser(User user);

    /**
     * 根据ID删除用户。
     *
     * @param id 用户ID
     */
    void deleteUser(String id);

    /**
     * 更新用户信息。
     *
     * @param user 用户实体（需包含 id；部分字段可为 null 表示不更新）
     */
    void updateUser(User user);

    /**
     * 通过 token 查询用户（用于接口鉴权）。
     *
     * @param token 登录 token（请求头 TOKEN）
     * @return 用户对象（不存在返回 null）
     */
    User getUserByToken(String token);

    /**
     * 通过用户名查询用户（用于注册时的唯一性校验）。
     *
     * @param username 用户名
     * @return 用户对象（不存在返回 null）
     */
    User getUserByUserName(String username);

    /**
     * 获取用户详情。
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    User getUserDetail(String userId);
}









