package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.dto.TimeSlotDTO;
import com.gk.study.entity.Appointment;
import com.gk.study.entity.TimeSlot;
import com.gk.study.exception.AppointmentConflictException;
import com.gk.study.exception.InvalidDateException;
import com.gk.study.exception.InvalidSlotException;
import com.gk.study.mapper.AppointmentMapper;
import com.gk.study.mapper.TimeSlotMapper;
import com.gk.study.service.AppointmentService;
import com.gk.study.util.AppointmentNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 预约服务实现类
 * 提供预约时间选择和预约创建功能
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private TimeSlotMapper timeSlotMapper;
    
    /**
     * 获取指定日期的可用时间段
     * @param date 日期 (格式: yyyy-MM-dd)
     * @param thingId 服务ID
     * @return 时间段列表（包含可用状态）
     */
    @Override
    public List<TimeSlotDTO> getAvailableSlots(String date, String thingId) {
        // 1. 查询所有启用的时间段配置
        List<TimeSlot> timeSlots = timeSlotMapper.selectAllEnabled();
        
        // 2. 创建结果列表
        List<TimeSlotDTO> result = new ArrayList<>();
        
        // 3. 遍历每个时间段，查询预约数量并计算可用状态
        for (TimeSlot slot : timeSlots) {
            // 查询该日期该时间段的已预约数量
            int bookedCount = appointmentMapper.countByDateAndSlot(date, slot.getSlotId());
            
            // 创建 TimeSlotDTO 对象
            TimeSlotDTO dto = new TimeSlotDTO();
            dto.setSlotId(slot.getSlotId());
            dto.setStartTime(slot.getStartTime());
            dto.setEndTime(slot.getEndTime());
            dto.setBookedCount(bookedCount);
            dto.setMaxCapacity(slot.getMaxCapacity());
            
            // 计算可用状态：已预约数 < 最大容量
            dto.setAvailable(bookedCount < slot.getMaxCapacity());
            
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * 创建预约记录
     * @param appointment 预约信息
     * @throws com.gk.study.exception.AppointmentConflictException 时间段已被预约
     * @throws com.gk.study.exception.InvalidDateException 日期无效
     */
    @Override
    @Transactional
    public void createAppointment(Appointment appointment) {
        // 1. 验证预约日期不能是过去的日期
        LocalDate appointmentDate;
        try {
            appointmentDate = LocalDate.parse(appointment.getAppointmentDate());
        } catch (Exception e) {
            throw new InvalidDateException("日期格式无效，请使用 yyyy-MM-dd 格式");
        }
        
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new InvalidDateException("不能预约过去的日期");
        }
        
        // 2. 验证时间段ID有效且已启用
        TimeSlot slot = timeSlotMapper.selectBySlotId(appointment.getSlotId());
        if (slot == null) {
            throw new InvalidSlotException("无效的时间段ID");
        }
        if (!slot.getEnabled()) {
            throw new InvalidSlotException("该时间段已被禁用");
        }
        
        // 3. 使用 SELECT FOR UPDATE 锁定记录并查询预约数
        int currentCount = appointmentMapper.countByDateAndSlotWithLock(
            appointment.getAppointmentDate(), 
            appointment.getSlotId()
        );
        
        // 4. 检查预约数是否已达到最大容量
        if (currentCount >= slot.getMaxCapacity()) {
            throw new AppointmentConflictException("该时间段已被预约，请选择其他时间");
        }
        
        // 5. 生成唯一的预约编号（格式：APT + yyyyMMdd + 6位序列号）
        String appointmentNumber = AppointmentNumberGenerator.generate();
        appointment.setAppointmentNumber(appointmentNumber);
        
        // 6. 插入预约记录到数据库
        appointmentMapper.insert(appointment);
    }
    
    /**
     * 检查时间段是否可用
     * @param date 日期 (格式: yyyy-MM-dd)
     * @param slotId 时间段ID
     * @return 是否可用
     */
    @Override
    public boolean isSlotAvailable(String date, String slotId) {
        // 查询时间段配置
        TimeSlot slot = timeSlotMapper.selectBySlotId(slotId);
        if (slot == null || !slot.getEnabled()) {
            return false;
        }
        
        // 查询已预约数量
        int bookedCount = appointmentMapper.countByDateAndSlot(date, slotId);
        
        // 判断是否可用：已预约数 < 最大容量
        return bookedCount < slot.getMaxCapacity();
    }
    
    /**
     * 获取用户的预约列表
     * @param userId 用户ID
     * @return 预约列表,按创建时间倒序排列，包含服务标题、封面图和时间段信息
     */
    @Override
    public List<Appointment> getUserAppointments(String userId) {
        return appointmentMapper.selectUserAppointmentsWithDetails(userId);
    }
    
    /**
     * 获取服务提供者收到的预约列表
     * @param userId 服务提供者的用户ID
     * @return 预约列表
     */
    @Override
    public List<Appointment> getReceivedAppointments(String userId) {
        return appointmentMapper.selectReceivedAppointmentsWithDetails(userId);
    }
    
    /**
     * 根据ID获取预约
     * @param appointmentId 预约ID
     * @return 预约对象
     */
    @Override
    public Appointment getAppointmentById(String appointmentId) {
        return appointmentMapper.selectById(appointmentId);
    }
    
    /**
     * 更新预约信息
     * @param appointment 预约对象
     */
    @Override
    public void updateAppointment(Appointment appointment) {
        appointmentMapper.updateById(appointment);
    }
    
    /**
     * 管理员获取所有预约列表
     * @return 所有预约列表
     */
    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentMapper.getAllAppointments();
    }
}
