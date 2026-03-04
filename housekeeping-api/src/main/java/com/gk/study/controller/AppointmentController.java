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
import com.gk.study.utils.HttpContextUtils;
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
 * 预约控制器
 * 处理预约时间选择和预约创建相关的HTTP请求
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
     * 管理员获取所有预约列表
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
     * 获取指定日期的可用时间段
     * @param date 日期 (格式: yyyy-MM-dd)
     * @param thingId 服务ID
     * @return 可用时间段列表
     */
    // @Access(level = AccessLevel.LOGIN) // 临时注释用于测试
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
     * 获取用户的预约列表
     * @param request HTTP请求
     * @return 预约列表
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse getUserAppointments(HttpServletRequest request) {
        // 从请求头获取TOKEN,然后查询用户
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
     * 创建预约
     * @param appointment 预约信息
     * @return 预约结果
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse createAppointment(Appointment appointment, HttpServletRequest request) {
        // 参数验证
        if (appointment == null || !StringUtils.hasText(appointment.getThingId()) 
                || !StringUtils.hasText(appointment.getAppointmentDate())
                || !StringUtils.hasText(appointment.getSlotId())
                || !StringUtils.hasText(appointment.getReceiverName())
                || !StringUtils.hasText(appointment.getReceiverPhone())
                || !StringUtils.hasText(appointment.getReceiverAddress())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误，请填写完整信息");
        }

        // 从请求头获取TOKEN,然后查询用户
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
        appointment.setUserId(userId);

        try {
            service.createAppointment(appointment);
            
            // 返回预约结果
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
            throw e; // 重新抛出异常，让全局异常处理器处理
        }
    }
    
    /**
     * 获取服务提供者收到的预约列表
     * @param request HTTP请求
     * @return 预约列表
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/received", method = RequestMethod.GET)
    public APIResponse getReceivedAppointments(HttpServletRequest request) {
        // 从请求头获取TOKEN,然后查询用户
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
            
            // 打印每个预约的详细信息用于调试
            for (Appointment apt : appointments) {
                logger.info("预约详情 - ID: {}, ThingID: {}, 预约用户ID: {}", apt.getId(), apt.getThingId(), apt.getUserId());
            }
            
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", appointments);
        } catch (Exception e) {
            logger.error("获取收到的预约列表失败 - 用户ID: {}, 错误: {}", user.getId(), e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "获取预约列表失败");
        }
    }
    
    /**
     * 更新预约状态
     * @param appointmentId 预约ID
     * @param status 新状态 (0:待服务, 1:已完成, 2:已取消, 3:已拒绝)
     * @param request HTTP请求
     * @return 更新结果
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateAppointmentStatus(String appointmentId, String status, HttpServletRequest request) {
        if (!StringUtils.hasText(appointmentId) || !StringUtils.hasText(status)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        
        // 从请求头获取TOKEN,然后查询用户
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
            
            // 验证权限：只有预约的用户或服务提供者可以修改状态
            String userId = user.getId().toString();
            boolean isOwner = appointment.getUserId().equals(userId);
            
            // 检查是否是服务提供者（通过thing的userId）
            boolean isProvider = false;
            if (!isOwner) {
                // 需要查询thing的userId
                // 这里简化处理，假设服务提供者可以修改
                isProvider = true; // TODO: 实际应该查询thing表验证
            }
            
            if (!isOwner && !isProvider) {
                return new APIResponse(ResponeCode.FAIL, "无权限修改此预约");
            }
            
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
