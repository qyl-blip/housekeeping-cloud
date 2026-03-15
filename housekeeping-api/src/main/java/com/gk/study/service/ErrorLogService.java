package com.gk.study.service;

import com.gk.study.entity.ErrorLog;

import java.util.List;

/**
 * 错误日志（ErrorLog）业务接口。
 *
 * <p>用于记录系统运行过程中的异常信息，便于后台排查问题。</p>
 */
public interface ErrorLogService {

    /**
     * 错误日志列表。
     */
    List<ErrorLog> getErrorLogList();

    /**
     * 新增错误日志。
     */
    void createErrorLog(ErrorLog errorLog);

    /**
     * 删除错误日志。
     */
    void deleteErrorLog(String id);

    /**
     * 更新错误日志。
     */
    void updateErrorLog(ErrorLog errorLog);
}









