package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ThingCollect;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingCollectService;
import com.gk.study.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 收藏（ThingCollect）模块接口。
 *
 * <p>用于处理用户对服务（Thing）的收藏/取消收藏，以及查看个人收藏列表。
 * 收藏/取消收藏成功后，会同步更新服务的收藏数（collectCount）。</p>
 */
@RestController
@RequestMapping("/thingCollect")
public class ThingCollectController {

    private final static Logger logger = LoggerFactory.getLogger(ThingCollectController.class);

    @Autowired
    ThingCollectService thingCollectService;

    @Autowired
    ThingService thingService;

    /**
     * 收藏某个服务（登录用户）。
     *
     * <p>如果已经收藏过，会直接返回提示，不会重复插入记录。</p>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @Transactional
    public APIResponse collect(ThingCollect thingCollect) throws IOException {
        if (thingCollectService.getThingCollect(thingCollect.getUserId(), thingCollect.getThingId()) != null) {
            return new APIResponse(ResponeCode.SUCCESS, "您已收藏过了");
        }
        thingCollectService.createThingCollect(thingCollect);
        thingService.addCollectCount(thingCollect.getThingId());
        return new APIResponse(ResponeCode.SUCCESS, "收藏成功");
    }

    /**
     * 取消收藏（登录用户）。
     *
     * <p>支持两种定位方式：
     * 1) 直接传收藏记录ID；2) 传 userId + thingId 组合。</p>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unCollect", method = RequestMethod.POST)
    @Transactional
    public APIResponse unCollect(ThingCollect thingCollect) throws IOException {
        ThingCollect target = null;
        if (thingCollect != null && thingCollect.getId() != null) {
            target = thingCollectService.getThingCollectById(thingCollect.getId());
        }
        if (target == null && thingCollect != null && StringUtils.hasText(thingCollect.getUserId()) && StringUtils.hasText(thingCollect.getThingId())) {
            target = thingCollectService.getThingCollect(thingCollect.getUserId(), thingCollect.getThingId());
        }
        if (target != null) {
            thingCollectService.deleteThingCollect(target.getId().toString());
            thingService.subCollectCount(target.getThingId());
        }
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    /**
     * 查询用户收藏列表。
     *
     * <p>返回结果为 join 后的 Map 列表（便于前端展示服务标题/封面等字段）。
     * 支持可选分页：page/pageSize 同时传入时进行内存分页。</p>
     */
    @RequestMapping(value = "/getUserCollectList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserCollectList(String userId, Integer page, Integer pageSize) throws IOException {
        List<Map> lists = thingCollectService.getThingCollectList(userId);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(lists, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}