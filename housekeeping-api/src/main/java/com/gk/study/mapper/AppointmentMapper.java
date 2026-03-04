package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AppointmentMapper 接口
 * 提供预约数据访问功能，包括查询预约数量和并发控制
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
    
    /**
     * 查询指定日期和时间段的预约数量
     * @param date 预约日期 (格式: yyyy-MM-dd)
     * @param slotId 时间段ID
     * @return 预约数量
     */
    int countByDateAndSlot(@Param("date") String date, @Param("slotId") String slotId);
    
    /**
     * 查询指定日期和时间段的预约数量（带锁）
     * 使用 SELECT FOR UPDATE 锁定记录，确保并发安全
     * 用于预约创建时检查时间段可用性
     * @param date 预约日期 (格式: yyyy-MM-dd)
     * @param slotId 时间段ID
     * @return 预约数量
     */
    int countByDateAndSlotWithLock(@Param("date") String date, @Param("slotId") String slotId);
    
    /**
     * 查询用户的预约列表（包含关联的服务和时间段信息）
     * @param userId 用户ID
     * @return 预约列表，按创建时间倒序排列
     */
    @Select("SELECT a.*, t.title as thing_title, t.cover, " +
            "CONCAT(ts.start_time, '-', ts.end_time) as slot_time " +
            "FROM b_appointment a " +
            "LEFT JOIN b_thing t ON a.thing_id = t.id " +
            "LEFT JOIN b_time_slot ts ON a.slot_id = ts.slot_id " +
            "WHERE a.user_id = #{userId} " +
            "ORDER BY a.create_time DESC")
    List<Appointment> selectUserAppointmentsWithDetails(@Param("userId") String userId);
    
    /**
     * 查询服务提供者收到的预约列表（包含关联的服务和时间段信息）
     * @param userId 服务提供者的用户ID
     * @return 预约列表，按创建时间倒序排列
     */
    @Select("SELECT a.*, t.title as thing_title, t.cover, " +
            "CONCAT(ts.start_time, '-', ts.end_time) as slot_time " +
            "FROM b_appointment a " +
            "LEFT JOIN b_thing t ON a.thing_id = t.id " +
            "LEFT JOIN b_time_slot ts ON a.slot_id = ts.slot_id " +
            "WHERE t.user_id = #{userId} " +
            "ORDER BY a.create_time DESC")
    List<Appointment> selectReceivedAppointmentsWithDetails(@Param("userId") String userId);
    
    /**
     * 管理员查询所有预约列表（包含关联的用户、服务和时间段信息）
     * @return 所有预约列表，按创建时间倒序排列
     */
    List<Appointment> getAllAppointments();
}
