package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Thing;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/thing")
public class ThingController {

    private final static Logger logger = LoggerFactory.getLogger(ThingController.class);

    @Autowired
    ThingService service;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String tag, Integer page, Integer pageSize){
        List<Thing> list =  service.getThingList(keyword, sort, c, tag);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        Thing thing =  service.getThingById(id);
        if (thing == null) {
            return new APIResponse(ResponeCode.FAIL, "未找到数据");
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", thing);
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        String image = saveThing(thing);
        if(!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        service.createThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThing(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        String image = saveThing(thing);
        if(!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/listUserThing", method = RequestMethod.GET)
    public APIResponse listUserThing(String userId, Integer page, Integer pageSize){
        List<Thing> list =  service.getUserThing(userId);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/userCreate", method = RequestMethod.POST)
    @Transactional
    public APIResponse userCreate(Thing thing) throws IOException {
        // 普通用户创建自己的服务，默认状态为待审核
        String image = saveThing(thing);
        if(!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        // 确保状态为待审核（1表示下架/待审核）
        if (StringUtils.isEmpty(thing.status)) {
            thing.status = "1";
        }
        service.createThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "提交成功，等待审核");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/userUpdate", method = RequestMethod.POST)
    @Transactional
    public APIResponse userUpdate(Thing thing) throws IOException {
        // 普通用户更新自己的服务
        // 首先验证这个Thing是否属于当前用户
        Thing existingThing = service.getThingById(String.valueOf(thing.id));
        if (existingThing == null) {
            return new APIResponse(ResponeCode.FAIL, "服务不存在");
        }
        if (!existingThing.userId.equals(thing.userId)) {
            return new APIResponse(ResponeCode.FAIL, "无权限修改此服务");
        }
        
        String image = saveThing(thing);
        if(!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        // 更新后状态改为待审核
        thing.status = "1";
        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功，等待审核");
    }

    public String saveThing(Thing thing) throws IOException {
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        return newFileName;
    }
}
