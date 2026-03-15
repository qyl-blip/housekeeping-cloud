package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.ErrorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错误日志（ErrorLog）数据访问层。
 *
 * <p>对应表：b_error_log。用于持久化系统异常信息，便于后台排查。</p>
 */
@Mapper
public interface ErrorLogMapper extends BaseMapper<ErrorLog> {

}









