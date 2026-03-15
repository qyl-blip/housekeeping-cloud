package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ErrorLog;
import com.gk.study.service.ErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错误日志（ErrorLog）接口。
 *
 * <p>用于后台查看系统运行过程中的异常/错误记录，便于排查问题。
 * 目前主要提供“列表查询”能力，支持可选分页（内存分页）。</p>
 */
@RestController
@RequestMapping("/errorLog")
public class ErrorLogController {

    private final static Logger logger = LoggerFactory.getLogger(ErrorLogController.class);

    @Autowired
    ErrorLogService service;

    /**
     * 查询错误日志列表。
     *
     * <p>可选分页参数：page/pageSize 同时传入时，对查询结果进行内存分页。</p>
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<ErrorLog> list =  service.getErrorLogList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }
}

