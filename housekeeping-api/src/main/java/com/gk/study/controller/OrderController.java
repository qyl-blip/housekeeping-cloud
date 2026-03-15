package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Order;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.OrderService;
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
 * 订单模块接口。
 *
 * <p>主要提供：</p>
 * <ul>
 *   <li>管理端：订单列表查询、订单删除/更新</li>
 *   <li>用户端：我的订单查询、下单、取消</li>
 * </ul>
 *
 * <p>权限控制通过 {@link Access} + {@link AccessLevel} 在拦截器中统一校验。</p>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService service;

    /**
     * 管理端订单列表。
     *
     * @param page     页码（可选；为空则不分页）
     * @param pageSize 每页条数（可选；为空则不分页）
     * @return 订单列表（可分页）
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize) {
        List<Order> list = service.getOrderList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 用户端：我的订单列表（可按状态筛选）。
     *
     * @param userId   用户ID
     * @param status   订单状态（可选；为空表示不过滤）
     * @param page     页码（可选；为空则不分页）
     * @param pageSize 每页条数（可选；为空则不分页）
     * @return 用户订单列表（可分页）
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/userOrderList", method = RequestMethod.GET)
    public APIResponse userOrderList(String userId, String status, Integer page, Integer pageSize) {
        List<Order> list = service.getUserOrderList(userId, status);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 创建订单（下单）。
     *
     * <p>订单号、下单时间、初始状态等字段由 Service 层在创建时补齐。</p>
     *
     * @param order 订单信息
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Order order) throws IOException {
        service.createOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 管理端删除订单（支持逗号分隔的批量删除）。
     *
     * @param ids 订单ID列表，英文逗号分隔，例如："1,2,3"
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        if (!StringUtils.hasText(ids)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        String[] arr = ids.split(",");
        for (String id : arr) {
            if (StringUtils.hasText(id)) {
                service.deleteOrder(id.trim());
            }
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 管理端更新订单（包含状态更新）。
     *
     * @param order 订单实体（需包含 id）
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Order order) throws IOException {
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 用户取消订单（带状态校验）。
     *
     * <p>当前约束：只有当订单状态为 "1" 时才允许取消。</p>
     *
     * @param id 订单ID
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public APIResponse cancelOrder(String id) {
        if (!StringUtils.hasText(id)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        Order order = service.getOrderDetail(id);
        if (order == null) {
            return new APIResponse(ResponeCode.FAIL, "订单不存在");
        }
        if (!"1".equals(order.getStatus())) {
            return new APIResponse(ResponeCode.FAIL, "当前状态，请勿重复操作");
        }
        order.setStatus("3");
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

    /**
     * 用户取消订单（不做“只能取消待处理订单”的校验）。
     *
     * <p>注意：该接口会直接把订单状态置为 "3"。</p>
     *
     * @param id 订单ID
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    public APIResponse cancelUserOrder(String id) {
        if (!StringUtils.hasText(id)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        Order order = service.getOrderDetail(id);
        if (order == null) {
            return new APIResponse(ResponeCode.FAIL, "订单不存在");
        }
        order.setStatus("3");
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }
}
