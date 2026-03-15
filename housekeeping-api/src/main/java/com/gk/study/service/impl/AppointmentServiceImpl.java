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
 * 预约（Appointment）业务实现。
 *
 * <p>实现要点：</p>
 * <ul>
 *   <li>时间段配置来自 b_time_slot（{@link TimeSlotMapper}）。</li>
 *   <li>创建预约时通过带锁统计（FOR UPDATE）做并发名额控制，防止同一时间段被超卖。</li>
 *   <li>名额占用的统计口径：以 {@code AppointmentMapper.xml} 中的 status 条件为准（当前为 status='0'）。</li>
 * </ul>
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private TimeSlotMapper timeSlotMapper;
    
    /**
     * 查询某天某个服务的可预约时间段列表。
     *
     * <p>实现方式：读取所有启用的时间段配置，然后逐个统计“该日期该时间段的已预约数量”，
     * 最终计算是否仍可预约（bookedCount &lt; maxCapacity）。</p>
     *
     * @param date    日期（yyyy-MM-dd）
     * @param thingId 服务ID（当前实现未直接参与计算，保留参数便于后续扩展）
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
     * 创建预约（带名额与并发控制）。
     *
     * <p>流程：</p>
     * <ol>
     *   <li>校验日期格式与“不能预约过去日期”</li>
     *   <li>校验时间段存在且启用</li>
     *   <li>使用 FOR UPDATE 带锁统计当前占用数量，并与 maxCapacity 比较</li>
     *   <li>生成预约编号并入库</li>
     * </ol>
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
     * 判断某天某时间段是否仍可预约。
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
     * 查询用户的预约列表（我的预约）。
     */
    @Override
    public List<Appointment> getUserAppointments(String userId) {
        return appointmentMapper.selectUserAppointmentsWithDetails(userId);
    }
    
    /**
     * 查询服务提供者收到的预约列表。
     */
    @Override
    public List<Appointment> getReceivedAppointments(String userId) {
        return appointmentMapper.selectReceivedAppointmentsWithDetails(userId);
    }
    
    /**
     * 按主键查询预约。
     */
    @Override
    public Appointment getAppointmentById(String appointmentId) {
        return appointmentMapper.selectById(appointmentId);
    }
    
    /**
     * 更新预约信息（包含状态更新）。
     */
    @Override
    public void updateAppointment(Appointment appointment) {
        appointmentMapper.updateById(appointment);
    }
    
    /**
     * 管理端：查询全部预约列表。
     */
    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentMapper.getAllAppointments();
    }
}
