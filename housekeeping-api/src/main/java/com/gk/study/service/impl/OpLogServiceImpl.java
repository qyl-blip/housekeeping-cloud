package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.OpLog;
import com.gk.study.mapper.OpLogMapper;
import com.gk.study.service.OpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志（OpLog）业务实现。
 *
 * <p>用于记录并查询系统访问/操作日志，便于后台审计与排查。
 * 当前实现提供两类列表：
 * 1) 全量操作日志（按 re_time 倒序，最多 1000 条）；
 * 2) 登录日志（按 re_url 过滤 /api/user/userLogin，同样最多 1000 条）。</p>
 */
@Service
public class OpLogServiceImpl extends ServiceImpl<OpLogMapper, OpLog> implements OpLogService {
    @Autowired
    OpLogMapper mapper;

    /**
     * 查询操作日志列表。
     *
     * <p>按 re_time 倒序，并做简单条数上限限制（1000 条）。</p>
     */
    @Override
    public List<OpLog> getOpLogList() {
        QueryWrapper<OpLog> queryWrapper = new QueryWrapper();
        queryWrapper.orderBy(true, false, "re_time");
        queryWrapper.last("limit 0, 1000");
        return mapper.selectList(queryWrapper);
    }

    /**
     * 新增操作日志。
     */
    @Override
    public void createOpLog(OpLog opLog) {
        mapper.insert(opLog);
    }

    /**
     * 删除操作日志。
     */
    @Override
    public void deleteOpLog(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新操作日志。
     */
    @Override
    public void updateOpLog(OpLog opLog) {
        mapper.updateById(opLog);
    }

    /**
     * 查询登录日志列表。
     *
     * <p>通过 re_url = /api/user/userLogin 进行过滤，并按 re_time 倒序，最多 1000 条。</p>
     */
    @Override
    public List<OpLog> getLoginLogList() {
        QueryWrapper<OpLog> queryWrapper = new QueryWrapper();
        queryWrapper.eq("re_url", "/api/user/userLogin");
        queryWrapper.orderBy(true, false, "re_time");
        queryWrapper.last("limit 0, 1000");
        return mapper.selectList(queryWrapper);
    }
}
