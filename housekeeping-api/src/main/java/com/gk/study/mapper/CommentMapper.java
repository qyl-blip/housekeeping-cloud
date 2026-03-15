package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper。
 *
 * <p>对应表：b_comment。除通用 CRUD 外，本 Mapper 还提供了带关联查询的列表接口：
 * 通过 join b_user / b_thing 回填用户名、服务标题等字段，便于前端展示。</p>
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 评论列表（管理端/全量）。
     *
     * <p>对应 XML：CommentMapper.xml#getList。
     * 关联查询 user/thing，默认按评论时间倒序。</p>
     */
    List<Comment> getList();

    /**
     * 查询某个服务（Thing）的评论列表。
     *
     * @param thingId 服务ID
     * @param order   排序方式：recent=按时间倒序；hot=按点赞数倒序（约定值见 XML）
     * @return 评论列表（包含用户名、服务标题等回填字段）
     */
    List<Comment> selectThingCommentList(@Param("thingId") String thingId, @Param("order") String order);

    /**
     * 查询某个用户发表的评论列表。
     *
     * <p>对应 XML：CommentMapper.xml#selectUserCommentList。
     * 关联查询服务信息（包含 cover），按评论时间倒序。</p>
     *
     * @param userId 用户ID
     * @return 该用户的评论列表
     */
    List<Comment> selectUserCommentList(@Param("userId") String userId);
}









