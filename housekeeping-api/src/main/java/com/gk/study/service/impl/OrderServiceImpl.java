package com.gk.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Order;
import com.gk.study.mapper.OrderMapper;
import com.gk.study.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单业务实现。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>列表/详情查询主要依赖 {@link OrderMapper} 在 XML 中的自定义 SQL。</li>
 *   <li>创建订单时，会自动补齐下单时间、订单号与初始状态。</li>
 * </ul>
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    OrderMapper mapper;

    /**
     * 管理端查询全部订单列表。
     */
    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    /**
     * 创建订单。
     *
     * <p>当前实现约定：</p>
     * <ul>
     *   <li>订单时间（orderTime）与订单号（orderNumber）均使用当前时间戳字符串</li>
     *   <li>初始状态为 status="1"</li>
     * </ul>
     */
    @Override
    public void createOrder(Order order) {
        // 1) 生成订单号/下单时间
        long ct = System.currentTimeMillis();
        order.setOrderTime(String.valueOf(ct));
        order.setOrderNumber(String.valueOf(ct));

        // 2) 设置初始状态
        order.setStatus("1");

        // 3) 入库
        mapper.insert(order);
    }

    /**
     * 删除订单（一般由管理员操作）。
     */
    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新订单信息（包含状态更新）。
     */
    @Override
    public void updateOrder(Order order) {
        mapper.updateById(order);
    }

    /**
     * 查询用户订单列表（可按状态过滤）。
     */
    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return mapper.getUserOrderList(userId, status);
    }

    /**
     * 查询订单详情。
     */
    @Override
    public Order getOrderDetail(String id) {
        return mapper.getDetail(id);
    }
}
