package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层。
 *
 * 对应表：b_user（具体表名以数据库为准）。
 *
 * 说明：
 * - 当前项目主要使用 BaseMapper 提供的通用 CRUD。
 * - token 查询、用户名查询等逻辑在 Service 层使用 QueryWrapper 组合条件完成。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}









