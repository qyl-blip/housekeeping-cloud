package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Thing;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务（家政项目）数据访问层。
 *
 * 对应表：b_thing（具体表名以数据库为准）。
 *
 * 说明：
 * - 目前大多数 CRUD 直接使用 MyBatis-Plus 的 BaseMapper 即可。
 * - 若后续需要复杂查询，可在这里新增自定义 Mapper 方法并在 XML 中实现。
 */
@Mapper
public interface ThingMapper extends BaseMapper<Thing> {
}









