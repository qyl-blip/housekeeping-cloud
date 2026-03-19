package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.RecommendConfig;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.RecommendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推荐权重配置接口（管理员专用）。
 */
@RestController
@RequestMapping("/recommendConfig")
public class RecommendConfigController {

    @Autowired
    RecommendConfigService service;

    /** 获取配置列表 */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", service.getConfigList());
    }

    /** 获取当前生效的配置（前台推荐排序使用） */
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public APIResponse active() {
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", service.getActiveConfig());
    }

    /** 新增配置 */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(RecommendConfig config) {
        service.createConfig(config);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /** 更新配置 */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(RecommendConfig config) {
        service.updateConfig(config);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /** 删除配置 */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        for (String id : ids.split(",")) {
            service.deleteConfig(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}
