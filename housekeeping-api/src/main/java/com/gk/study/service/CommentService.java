package com.gk.study.service;


import com.gk.study.entity.Comment;

import java.util.List;

/**
 * 评论业务接口。
 *
 * <p>负责评论列表查询、发表评论、删除评论、点赞（由 Controller 实现累加，Service 负责更新）。</p>
 */
public interface CommentService {

    /**
     * 查询评论列表（全量/管理端）。
     */
    List<Comment> getCommentList();

    /**
     * 新增评论。
     */
    void createComment(Comment comment);

    /**
     * 删除评论。
     */
    void deleteComment(String id);

    /**
     * 更新评论（用于点赞数等更新）。
     */
    void updateComment(Comment comment);

    /**
     * 查询评论详情。
     */
    Comment getCommentDetail(String id);

    /**
     * 查询某个服务（Thing）的评论列表。
     *
     * @param thingId 服务ID
     * @param order  排序方式（具体含义以 Mapper/XML 实现为准）
     */
    List<Comment> getThingCommentList(String thingId, String order);

    /**
     * 查询某个用户发表的评论列表。
     */
    List<Comment> getUserCommentList(String userId);
}









