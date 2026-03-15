package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.ThingWish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 心愿单（ThingWish）数据访问层。
 *
 * <p>对应表：b_thing_wish。
 * 除通用 CRUD 外，提供“查询用户心愿单列表”的自定义查询，用于联表回填服务标题/封面字段。</p>
 */
@Mapper
public interface ThingWishMapper extends BaseMapper<ThingWish> {

    /**
     * 查询用户心愿单列表（回填服务信息）。
     *
     * <p>对应 XML：ThingWishMapper.xml#getThingWishList
     * 查询 b_thing_wish 并 join b_thing，回填字段：title、cover。</p>
     */
    List<Map> getThingWishList(String userId);
}









