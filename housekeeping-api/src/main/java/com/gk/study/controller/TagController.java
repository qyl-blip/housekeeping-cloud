package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Tag;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.TagService;
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
 * 标签模块接口。
 *
 * <p>标签用于对服务（Thing）做更细粒度的属性描述（例如：深度保洁、除螨、开荒等）。</p>
 *
 * <p>权限说明：</p>
 * <ul>
 *   <li>查询列表：公开接口</li>
 *   <li>新增/删除/更新：管理员 {@link AccessLevel#ADMIN}</li>
 * </ul>
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    private final static Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    TagService service;

    /**
     * 查询标签列表。
     *
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<Tag> list =  service.getTagList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增标签（管理员）。
     *
     * @param tag 标签信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Tag tag) throws IOException {
        service.createTag(tag);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 删除标签（管理员，支持批量）。
     *
     * @param ids 标签ID，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteTag(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 更新标签（管理员）。
     *
     * @param tag 标签信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Tag tag) throws IOException {
        service.updateTag(tag);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}





