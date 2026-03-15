package com.gk.study.service;


import com.gk.study.entity.Order;

import java.util.List;

public interface OrderService {

    /**
     * 管理端查询全部订单列表。
     *
     * @return 订单列表
     */
    List<Order> getOrderList();

    /**
     * 创建订单。
     *
     * 当前实现会：
     * 1) 生成订单时间/订单号（时间戳字符串）
     * 2) 设置初始状态 status=1
     *
     * @param order 订单信息
     */
    void createOrder(Order order);

    /**
     * 删除订单（一般由管理员操作）。
     *
     * @param id 订单ID
     */
    void deleteOrder(String id);

    /**
     * 更新订单信息（包含状态更新）。
     *
     * @param order 订单实体（需包含 id）
     */
    void updateOrder(Order order);

    /**
     * 查询用户订单列表（可按状态过滤）。
     *
     * @param userId 用户ID
     * @param status 订单状态（为空表示不过滤）
     * @return 订单列表
     */
    List<Order> getUserOrderList(String userId, String status);

    /**
     * 查询订单详情。
     *
     * @param id 订单ID
     * @return 订单详情
     */
    Order getOrderDetail(String id);
}
