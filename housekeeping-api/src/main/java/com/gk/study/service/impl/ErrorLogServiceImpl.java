package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.ErrorLog;
import com.gk.study.mapper.ErrorLogMapper;
import com.gk.study.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 错误日志（ErrorLog）业务实现。
 *
 * <p>用于记录系统运行过程中的异常信息，便于后台排查问题。</p>
 */
@Service
public class ErrorLogServiceImpl extends ServiceImpl<ErrorLogMapper, ErrorLog> implements ErrorLogService {
    @Autowired
    ErrorLogMapper mapper;

    /**
     * 查询错误日志列表。
     */
    @Override
    public List<ErrorLog> getErrorLogList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 新增错误日志。
     */
    @Override
    public void createErrorLog(ErrorLog errorLog) {
        mapper.insert(errorLog);
    }

    /**
     * 删除错误日志。
     */
    @Override
    public void deleteErrorLog(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新错误日志。
     */
    @Override
    public void updateErrorLog(ErrorLog errorLog) {
        mapper.updateById(errorLog);
    }
}









