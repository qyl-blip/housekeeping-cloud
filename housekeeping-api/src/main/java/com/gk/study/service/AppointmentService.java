package com.gk.study.service;

import com.gk.study.dto.TimeSlotDTO;
import com.gk.study.entity.Appointment;

import java.util.List;

/**
 * 预约（Appointment）业务接口。
 *
 * <p>负责“可预约时间段查询 + 创建预约 + 查询预约列表 + 更新预约信息”等能力。
 * 其中“同一日期 + 时间段的名额控制/并发控制”在实现层通过带锁查询（SELECT ... FOR UPDATE）来保证。</p>
 */
public interface AppointmentService {
    
    /**
     * 查询某天某个服务的可预约时间段列表。
     *
     * <p>返回的 DTO 通常会包含：时间段起止时间、最大容量、已预约数量、是否仍可预约等字段。</p>
     *
     * @param date    日期（yyyy-MM-dd）
     * @param thingId 服务ID
     * @return 可预约时间段列表
     */
    List<TimeSlotDTO> getAvailableSlots(String date, String thingId);
    
    /**
     * 创建预约。
     *
     * <p>实现层通常会做：</p>
     * <ol>
     *   <li>校验日期不能是过去时间</li>
     *   <li>校验时间段存在且启用</li>
     *   <li>带锁统计当前占用数量（FOR UPDATE），防止并发超卖</li>
     *   <li>生成预约编号并入库</li>
     * </ol>
     *
     * @param appointment 预约信息
     * @throws com.gk.study.exception.AppointmentConflictException 时间段名额不足
     * @throws com.gk.study.exception.InvalidDateException 日期无效
     */
    void createAppointment(Appointment appointment);
    
    /**
     * 判断某天某时间段是否仍可预约。
     *
     * @param date   日期（yyyy-MM-dd）
     * @param slotId 时间段ID
     * @return true=可预约；false=不可预约
     */
    boolean isSlotAvailable(String date, String slotId);
    
    /**
     * 查询用户的预约列表（我的预约）。
     *
     * @param userId 用户ID
     * @return 预约列表（通常按创建时间倒序）
     */
    List<Appointment> getUserAppointments(String userId);
    
    /**
     * 查询服务提供者收到的预约列表。
     *
     * @param userId 服务提供者用户ID（通常对应 b_thing.user_id）
     * @return 收到的预约列表
     */
    List<Appointment> getReceivedAppointments(String userId);
    
    /**
     * 按主键查询预约。
     *
     * @param appointmentId 预约ID
     * @return 预约记录（不存在返回 null）
     */
    Appointment getAppointmentById(String appointmentId);
    
    /**
     * 更新预约信息（包含状态更新）。
     *
     * @param appointment 预约实体（需包含 id）
     */
    void updateAppointment(Appointment appointment);
    
    /**
     * 管理端：查询全部预约列表。
     *
     * @return 全部预约列表
     */
    List<Appointment> getAllAppointments();
}
