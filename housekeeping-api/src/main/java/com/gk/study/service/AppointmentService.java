package com.gk.study.service;

import com.gk.study.dto.TimeSlotDTO;
import com.gk.study.entity.Appointment;

import java.util.List;

/**
 * 预约服务接口
 * 提供预约时间选择和预约创建功能
 */
public interface AppointmentService {
    
    /**
     * 获取指定日期的可用时间段
     * @param date 日期 (格式: yyyy-MM-dd)
     * @param thingId 服务ID
     * @return 时间段列表（包含可用状态）
     */
    List<TimeSlotDTO> getAvailableSlots(String date, String thingId);
    
    /**
     * 创建预约记录
     * @param appointment 预约信息
     * @throws com.gk.study.exception.AppointmentConflictException 时间段已被预约
     * @throws com.gk.study.exception.InvalidDateException 日期无效
     */
    void createAppointment(Appointment appointment);
    
    /**
     * 检查时间段是否可用
     * @param date 日期 (格式: yyyy-MM-dd)
     * @param slotId 时间段ID
     * @return 是否可用
     */
    boolean isSlotAvailable(String date, String slotId);
    
    /**
     * 获取用户的预约列表
     * @param userId 用户ID
     * @return 预约列表
     */
    List<Appointment> getUserAppointments(String userId);
    
    /**
     * 获取服务提供者收到的预约列表
     * @param userId 服务提供者的用户ID
     * @return 预约列表
     */
    List<Appointment> getReceivedAppointments(String userId);
    
    /**
     * 根据ID获取预约
     * @param appointmentId 预约ID
     * @return 预约对象
     */
    Appointment getAppointmentById(String appointmentId);
    
    /**
     * 更新预约信息
     * @param appointment 预约对象
     */
    void updateAppointment(Appointment appointment);
    
    /**
     * 管理员获取所有预约列表
     * @return 所有预约列表
     */
    List<Appointment> getAllAppointments();
}
