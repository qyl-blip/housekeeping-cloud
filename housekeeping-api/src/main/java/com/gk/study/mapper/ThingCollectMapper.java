package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.ThingCollect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 收藏（ThingCollect）数据访问层。
 *
 * <p>对应表：b_thing_collect。
 * 除通用 CRUD 外，提供“查询用户收藏列表”的自定义查询，用于联表回填服务标题/封面字段。</p>
 */
@Mapper
public interface ThingCollectMapper extends BaseMapper<ThingCollect> {


    /**
     * 查询用户收藏列表（回填服务信息）。
     *
     * <p>对应 XML：ThingCollectMapper.xml#getThingCollectList
     * 查询 b_thing_collect 并 join b_thing，回填字段：title、cover。</p>
     */
    List<Map> getThingCollectList(String userId);
}









