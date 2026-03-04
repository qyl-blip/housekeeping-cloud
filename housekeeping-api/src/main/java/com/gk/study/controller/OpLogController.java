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

@RestController
@RequestMapping("/opLog")
public class OpLogController {

    private final static Logger logger = LoggerFactory.getLogger(OpLogController.class);

    @Autowired
    OpLogService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<OpLog> list =  service.getOpLogList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    @RequestMapping(value = "/loginLogList", method = RequestMethod.GET)
    public APIResponse loginLogList(Integer page, Integer pageSize){
        List<OpLog> list =  service.getLoginLogList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(OpLog opLog) throws IOException {
        service.createOpLog(opLog);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteOpLog(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(OpLog opLog) throws IOException {
        service.updateOpLog(opLog);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}





