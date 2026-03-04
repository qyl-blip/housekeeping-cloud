package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ThingWish;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingService;
import com.gk.study.service.ThingWishService;
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

@RestController
@RequestMapping("/thingWish")
public class ThingWishController {

    private final static Logger logger = LoggerFactory.getLogger(ThingWishController.class);

    @Autowired
    ThingWishService thingWishService;

    @Autowired
    ThingService thingService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/wish", method = RequestMethod.POST)
    @Transactional
    public APIResponse wish(ThingWish thingWish) throws IOException {
        if (thingWishService.getThingWish(thingWish.getUserId(), thingWish.getThingId()) != null) {
            return new APIResponse(ResponeCode.SUCCESS, "已加入心愿");
        }
        thingWishService.createThingWish(thingWish);
        thingService.addWishCount(thingWish.getThingId());
        return new APIResponse(ResponeCode.SUCCESS, "加入心愿成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unWish", method = RequestMethod.POST)
    @Transactional
    public APIResponse unWish(ThingWish thingWish) throws IOException {
        ThingWish target = null;
        if (thingWish != null && thingWish.getId() != null) {
            target = thingWishService.getThingWishById(thingWish.getId());
        }
        if (target == null && thingWish != null && StringUtils.hasText(thingWish.getUserId()) && StringUtils.hasText(thingWish.getThingId())) {
            target = thingWishService.getThingWish(thingWish.getUserId(), thingWish.getThingId());
        }
        if (target != null) {
            thingWishService.deleteThingWish(target.getId().toString());
            thingService.subWishCount(target.getThingId());
        }
        return new APIResponse(ResponeCode.SUCCESS, "取消心愿成功");
    }

    @RequestMapping(value = "/getUserWishList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserWishList(String userId, Integer page, Integer pageSize) throws IOException {
        List<Map> lists = thingWishService.getThingWishList(userId);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(lists, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}