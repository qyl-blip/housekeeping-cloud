package com.gk.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Comment;
import com.gk.study.mapper.CommentMapper;
import com.gk.study.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论业务实现。
 *
 * <p>评论查询存在自定义 SQL（例如：按服务ID查询、按用户查询、按热度/时间排序），
 * 对应的 SQL 主要在 {@link CommentMapper} 的 XML 中实现。</p>
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    CommentMapper mapper;

    /**
     * 查询评论列表（全量/管理端）。
     */
    @Override
    public List<Comment> getCommentList() {
        return mapper.getList();
    }

    /**
     * 新增评论（自动补齐 commentTime）。
     */
    @Override
    public void createComment(Comment comment) {
        comment.setCommentTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(comment);
    }

    /**
     * 删除评论。
     */
    @Override
    public void deleteComment(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新评论。
     */
    @Override
    public void updateComment(Comment comment) {
        mapper.updateById(comment);
    }

    /**
     * 查询评论详情。
     */
    @Override
    public Comment getCommentDetail(String id) {
        return mapper.selectById(id);
    }

    /**
     * 查询某个服务（Thing）的评论列表。
     */
    @Override
    public List<Comment> getThingCommentList(String thingId, String order) {
        return mapper.selectThingCommentList(thingId, order);
    }

    /**
     * 查询某个用户发表的评论列表。
     */
    @Override
    public List<Comment> getUserCommentList(String userId) {
        return mapper.selectUserCommentList(userId);
    }
}









