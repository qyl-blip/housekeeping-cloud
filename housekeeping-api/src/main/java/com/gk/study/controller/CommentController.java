package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Comment;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.CommentService;
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
 * 评论模块接口。
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>评论列表（管理端或全量展示）</li>
 *   <li>某个服务（thingId）的评论列表（支持按时间/热度排序，由 Service 解释 order 参数）</li>
 *   <li>某个用户发表的评论列表</li>
 *   <li>新增评论、删除评论</li>
 *   <li>评论点赞：likeCount + 1</li>
 * </ul>
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService service;

    /**
     * 查询评论列表（全量）。
     *
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<Comment> list =  service.getCommentList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 查询某个服务（Thing）的评论列表。
     *
     * @param thingId   服务ID
     * @param order     排序方式（由 Service 层解释具体含义，例如 recent/hot 等）
     * @param page      页码（可选）
     * @param pageSize  每页条数（可选）
     */
    @RequestMapping(value = "/listThingComments", method = RequestMethod.GET)
    public APIResponse listThingComments(String thingId, String order, Integer page, Integer pageSize){
        List<Comment> list =  service.getThingCommentList(thingId, order);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 查询某个用户发表的评论列表。
     *
     * @param userId   用户ID
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     */
    @RequestMapping(value = "/listUserComments", method = RequestMethod.GET)
    public APIResponse listUserComments(String userId, Integer page, Integer pageSize){
        List<Comment> list =  service.getUserCommentList(userId);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增评论（登录用户）。
     *
     * @param comment 评论内容
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Comment comment) throws IOException {
        service.createComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 删除评论（管理员，支持批量）。
     *
     * @param ids 评论ID，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteComment(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 评论点赞（登录用户）。
     *
     * <p>实现方式：读取评论记录，把 likeCount + 1 后写回。
     * 当前为简单累加逻辑，不做重复点赞校验与并发控制。</p>
     *
     * @param id 评论ID
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public APIResponse like(String id){
        Comment commentBean = service.getCommentDetail(id);
        int likeCount = Integer.parseInt(commentBean.getLikeCount()) + 1;
        commentBean.setLikeCount(String.valueOf(likeCount));
        service.updateComment(commentBean);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}



