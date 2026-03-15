package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Classification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务分类 Mapper。
 *
 * <p>对应表：b_classification。主要用于分类字典数据的维护（增删改查）。</p>
 */
@Mapper
public interface ClassificationMapper extends BaseMapper<Classification> {

}









