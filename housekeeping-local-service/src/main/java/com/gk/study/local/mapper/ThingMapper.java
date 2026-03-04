package com.gk.study.local.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.local.entity.Thing;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ThingMapper extends BaseMapper<Thing> {
    // MyBatis-Plus 已经内置了 CRUD，不需要写 SQL
}