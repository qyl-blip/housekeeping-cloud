package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告 Mapper。
 *
 * <p>对应表：b_notice。公告属于运营内容，主要使用 MyBatis-Plus 的通用 CRUD 能力。</p>
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

}









