package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.OpLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志（OpLog）数据访问层。
 *
 * <p>对应表：b_op_log。当前使用 MyBatis-Plus 提供的通用 CRUD，无自定义 SQL。</p>
 */
@Mapper
public interface OpLogMapper extends BaseMapper<OpLog> {

}









