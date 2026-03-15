package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单数据访问层。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>基础 CRUD 由 MyBatis-Plus {@link BaseMapper} 提供。</li>
 *   <li>这里定义的查询方法通常会在 mapper XML 中编写更复杂的 SQL（如联表/条件筛选）。</li>
 * </ul>
 *
 * <p>对应表：b_order（具体表名以数据库为准）。</p>
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询订单列表（通常用于管理端）。
     *
     * @return 订单列表
     */
    List<Order> getList();

    /**
     * 查询某个用户的订单列表，可按状态筛选。
     *
     * @param userId 用户ID
     * @param status 订单状态（为空表示不过滤，具体以 XML SQL 为准）
     * @return 订单列表
     */
    List<Order> getUserOrderList(String userId, String status);

    /**
     * 查询订单详情。
     *
     * @param id 订单ID
     * @return 订单详情（可能包含关联信息，具体取决于 XML SQL）
     */
    Order getDetail(String id);
}
