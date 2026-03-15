package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.OpLog;
import com.gk.study.service.OpLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 操作日志（OpLog）接口。
 *
 * <p>用于查看与维护系统操作日志，包括：
 * 1) 全部操作日志列表；2) 登录日志列表（按 URL 过滤）；3) 日志的新增/删除/更新。</p>
 */
@RestController
@RequestMapping("/opLog")
public class OpLogController {

    private final static Logger logger = LoggerFactory.getLogger(OpLogController.class);

    @Autowired
    OpLogService service;

    /**
     * 操作日志列表。
     *
     * <p>支持可选分页：page/pageSize 同时传入时进行内存分页。</p>
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<OpLog> list =  service.getOpLogList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 登录日志列表。
     *
     * <p>底层按 URL（/api/user/userLogin）筛选，主要用于后台查看登录记录。</p>
     */
    @RequestMapping(value = "/loginLogList", method = RequestMethod.GET)
    public APIResponse loginLogList(Integer page, Integer pageSize){
        List<OpLog> list =  service.getLoginLogList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增操作日志。
     *
     * <p>一般由拦截器/业务流程在关键操作时写入，前端通常不直接调用。</p>
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(OpLog opLog) throws IOException {
        service.createOpLog(opLog);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 删除操作日志（支持批量）。
     *
     * @param ids 逗号分隔的日志ID
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteOpLog(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 更新操作日志。
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(OpLog opLog) throws IOException {
        service.updateOpLog(opLog);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}





