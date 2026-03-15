package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签 Mapper。
 *
 * <p>对应表：b_tag。标签属于基础字典数据，用于给服务（Thing）做标记与筛选。</p>
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}









