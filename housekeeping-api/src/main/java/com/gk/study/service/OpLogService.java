package com.gk.study.service;

import com.gk.study.entity.OpLog;

import java.util.List;

/**
 * 操作日志（OpLog）业务接口。
 *
 * <p>用于查询/维护系统操作日志，并提供“登录日志”快捷查询能力。</p>
 */
public interface OpLogService {

    /**
     * 操作日志列表（通常用于后台管理查看）。
     */
    List<OpLog> getOpLogList();

    /**
     * 新增操作日志。
     */
    void createOpLog(OpLog opLog);

    /**
     * 删除操作日志。
     */
    void deleteOpLog(String id);

    /**
     * 更新操作日志。
     */
    void updateOpLog(OpLog opLog);

    /**
     * 登录日志列表。
     *
     * <p>底层按登录接口 URL 过滤得到的子集。</p>
     */
    List<OpLog> getLoginLogList();
}









