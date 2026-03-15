package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.ThingTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务-标签关联（ThingTag）数据访问层。
 *
 * <p>对应表：b_thing_tag，用于维护服务（Thing）与标签（Tag）的多对多关系。</p>
 */
@Mapper
public interface ThingTagMapper extends BaseMapper<ThingTag> {

}









