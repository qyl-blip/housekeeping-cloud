package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Notice;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.NoticeService;
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

/**
 * 公告模块接口。
 *
 * <p>公告用于在前台/后台展示系统通知、运营信息等内容。</p>
 *
 * <p>权限说明：</p>
 * <ul>
 *   <li>查询列表：公开接口</li>
 *   <li>新增/删除/更新：管理员 {@link AccessLevel#ADMIN}</li>
 * </ul>
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    private final static Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    NoticeService service;

    /**
     * 查询公告列表。
     *
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<Notice> list = service.getNoticeList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增公告（管理员）。
     *
     * @param notice 公告内容
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Notice notice) throws IOException {
        service.createNotice(notice);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 删除公告（管理员，支持批量）。
     *
     * @param ids 公告ID，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        if (!StringUtils.hasText(ids)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        String[] arr = ids.split(",");
        for (String id : arr) {
            if (StringUtils.hasText(id)) {
                service.deleteNotice(id.trim());
            }
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新公告（管理员）。
     *
     * @param notice 公告内容
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Notice notice) throws IOException {
        service.updateNotice(notice);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}