package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Classification;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ClassificationService;
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
 * 服务分类模块接口。
 *
 * <p>分类用于给服务（Thing）做归类展示（例如：保洁、保姆、家电清洗等）。</p>
 *
 * <p>权限说明：</p>
 * <ul>
 *   <li>查询列表：公开接口</li>
 *   <li>新增/删除/更新：管理员 {@link AccessLevel#ADMIN}</li>
 * </ul>
 */
@RestController
@RequestMapping("/classification")
public class ClassificationController {

    private final static Logger logger = LoggerFactory.getLogger(ClassificationController.class);

    @Autowired
    ClassificationService service;

    /**
     * 查询分类列表。
     *
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<Classification> list =  service.getClassificationList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增分类（管理员）。
     *
     * @param classification 分类信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Classification classification) throws IOException {
        service.createClassification(classification);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 删除分类（管理员，支持批量）。
     *
     * @param ids 分类ID，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteClassification(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 更新分类（管理员）。
     *
     * @param classification 分类信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Classification classification) throws IOException {
        service.updateClassification(classification);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}



