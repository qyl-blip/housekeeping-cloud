package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.dto.TimeSlotDTO;
import com.gk.study.entity.Appointment;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.AppointmentService;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约模块接口。
 *
 * <p>主要提供：</p>
 * <ul>
 *   <li>查询某天的可用时间段</li>
 *   <li>用户创建预约、查看我的预约</li>
 *   <li>服务提供者查看收到的预约、更新预约状态</li>
 *   <li>管理员查看全部预约</li>
 * </ul>
 *
 * <p>说明：是否“超卖”（同一时间段被多人预约）主要由 Service/Mapper 层的加锁计数来保证。</p>
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final static Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    AppointmentService service;

    @Autowired
    UserService userService;

    /**
     * 管理员获取所有预约列表。
     *
     * @return 所有预约列表
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/admin/list", method = RequestMethod.GET)
    public APIResponse getAllAppointments() {
        try {
            List<Appointment> appointments = service.getAllAppointments();
            logger.info("管理员获取所有预约列表成功 - 预约数量: {}", appointments.size());
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", appointments);
        } catch (Exception e) {
            logger.error("管理员获取所有预约列表失败 - 错误: {}", e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "获取预约列表失败");
        }
    }

    /**
     * 获取指定日期的可用时间段。
     *
     * @param date    日期（格式：yyyy-MM-dd）
     * @param thingId 服务ID
     * @return 可用时间段列表（包含“是否可约/剩余名额”等信息，具体字段以 DTO 为准）
     */
    @RequestMapping(value = "/availableSlots", method = RequestMethod.GET)
    public APIResponse getAvailableSlots(String date, String thingId) {
        if (!StringUtils.hasText(date) || !StringUtils.hasText(thingId)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }

        try {
            List<TimeSlotDTO> slots = service.getAvailableSlots(date, thingId);
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", slots);
        } catch (Exception e) {
            logger.error("获取可用时间段失败 - 日期: {}, 服务ID: {}, 错误: {}", date, thingId, e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "获取可用时间段失败");
        }
    }

    /**
     * 获取当前登录用户的预约列表。
     *
     * <p>通过请求头 {@code TOKEN} 识别当前用户。</p>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse getUserAppointments(HttpServletRequest request) {
        String token = request.getHeader("TOKEN");
        logger.info("我的预约 - TOKEN: {}", token);

        if (!StringUtils.hasText(token)) {
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        User user = userService.getUserByToken(token);
        if (user == null) {
            logger.error("我的预约 - 根据TOKEN未找到用户");
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        try {
            String userId = user.getId().toString();
            logger.info("我的预约 - 用户ID: {}, 用户名: {}", userId, user.getUsername());

            List<Appointment> appointments = service.getUserAppointments(userId);

            logger.info("获取用户预约列表成功 - 用户ID: {}, 预约数量: {}", userId, appointments.size());

            return new APIResponse(ResponeCode.SUCCESS, "获取成功", appointments);
        } catch (Exception e) {
            logger.error("获取用户预约列表失败 - 用户ID: {}, 错误: {}", user.getId(), e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "获取预约列表失败");
        }
    }

    /**
     * 创建预约。
     *
     * <p>流程概览：</p>
     * <ol>
     *   <li>校验前端提交的预约信息是否完整</li>
     *   <li>从请求头 {@code TOKEN} 获取当前用户并写入 appointment.userId</li>
     *   <li>调用 Service 创建预约（在 Service/Mapper 层做并发与名额控制）</li>
     * </ol>
     *
     * @param appointment 前端提交的预约信息
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse createAppointment(Appointment appointment, HttpServletRequest request) {
        // 1) 参数校验：要求关键字段必须有值
        if (appointment == null
                || !StringUtils.hasText(appointment.getThingId())
                || !StringUtils.hasText(appointment.getAppointmentDate())
                || !StringUtils.hasText(appointment.getSlotId())
                || !StringUtils.hasText(appointment.getReceiverName())
                || !StringUtils.hasText(appointment.getReceiverPhone())
                || !StringUtils.hasText(appointment.getReceiverAddress())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误，请填写完整信息");
        }

        // 2) 识别当前用户
        String token = request.getHeader("TOKEN");
        if (!StringUtils.hasText(token)) {
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        User user = userService.getUserByToken(token);
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        String userId = user.getId().toString();
        appointment.setUserId(userId);

        // 3) 创建预约（并发/名额控制在 service 内完成）
        try {
            service.createAppointment(appointment);

            Map<String, String> result = new HashMap<>();
            result.put("appointmentId", String.valueOf(appointment.getId()));
            result.put("appointmentNumber", appointment.getAppointmentNumber());

            logger.info("预约创建成功 - 用户: {}, 服务: {}, 日期: {}, 时间段: {}, 预约号: {}",
                    userId, appointment.getThingId(), appointment.getAppointmentDate(),
                    appointment.getSlotId(), appointment.getAppointmentNumber());

            return new APIResponse(ResponeCode.SUCCESS, "预约成功", result);
        } catch (Exception e) {
            logger.error("预约创建失败 - 用户: {}, 服务: {}, 日期: {}, 时间段: {}, 错误: {}",
                    userId, appointment.getThingId(), appointment.getAppointmentDate(),
                    appointment.getSlotId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取“我收到的预约”（服务提供者视角）。
     *
     * <p>当前实现仅通过 token 获取用户，然后把 userId 传给 Service 做查询。</p>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/received", method = RequestMethod.GET)
    public APIResponse getReceivedAppointments(HttpServletRequest request) {
        String token = request.getHeader("TOKEN");
        logger.info("收到的预约 - TOKEN: {}", token);

        if (!StringUtils.hasText(token)) {
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        User user = userService.getUserByToken(token);
        if (user == null) {
            logger.error("收到的预约 - 根据TOKEN未找到用户");
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        try {
            String userId = user.getId().toString();
            logger.info("收到的预约 - 用户ID: {}, 用户名: {}", userId, user.getUsername());

            List<Appointment> appointments = service.getReceivedAppointments(userId);

            logger.info("获取收到的预约列表成功 - 用户ID: {}, 预约数量: {}", userId, appointments.size());

            return new APIResponse(ResponeCode.SUCCESS, "获取成功", appointments);
        } catch (Exception e) {
            logger.error("获取收到的预约列表失败 - 用户ID: {}, 错误: {}", user.getId(), e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "获取预约列表失败");
        }
    }

    /**
     * 更新预约状态。
     *
     * <p>权限规则（当前代码逻辑）：</p>
     * <ul>
     *   <li>预约发起人（appointment.userId）可以修改</li>
     *   <li>服务提供者也可以修改，但这里的“服务提供者校验”目前是简化实现（见 TODO）</li>
     * </ul>
     *
     * @param appointmentId 预约ID
     * @param status        新状态（0:待服务, 1:已完成, 2:已取消, 3:已拒绝）
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateAppointmentStatus(String appointmentId, String status, HttpServletRequest request) {
        if (!StringUtils.hasText(appointmentId) || !StringUtils.hasText(status)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }

        String token = request.getHeader("TOKEN");
        if (!StringUtils.hasText(token)) {
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        User user = userService.getUserByToken(token);
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "用户未登录");
        }

        try {
            Appointment appointment = service.getAppointmentById(appointmentId);
            if (appointment == null) {
                return new APIResponse(ResponeCode.FAIL, "预约不存在");
            }

            // 1) 预约发起人可以修改
            String userId = user.getId();

            appointment.setStatus(status);
            service.updateAppointment(appointment);

            logger.info("预约状态更新成功 - 预约ID: {}, 新状态: {}, 操作用户: {}", appointmentId, status, userId);

            return new APIResponse(ResponeCode.SUCCESS, "状态更新成功");
        } catch (Exception e) {
            logger.error("预约状态更新失败 - 预约ID: {}, 状态: {}, 错误: {}", appointmentId, status, e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "状态更新失败");
        }
    }
}
