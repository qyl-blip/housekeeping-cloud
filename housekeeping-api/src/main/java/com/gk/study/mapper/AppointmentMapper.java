package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预约（Appointment）数据访问层。
 *
 * <p>对应表：b_appointment。除基础 CRUD 外，提供一些统计/联表查询：
 * - 名额控制：通过带锁统计（FOR UPDATE）防止同一时间段被并发预约“超卖”；
 * - 列表查询：联表回填服务标题/封面、时间段文字等展示字段。</p>
 *
 * <p>自定义 SQL 见：resources/mapper/AppointmentMapper.xml。</p>
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
    
    /**
     * 统计指定日期 + 时间段的“有效预约”数量。
     *
     * <p>统计口径以 XML 为准：当前仅统计 status='0' 的记录（表示仍占用名额）。</p>
     */
    int countByDateAndSlot(@Param("date") String date, @Param("slotId") String slotId);
    
    /**
     * 统计指定日期 + 时间段的“有效预约”数量（带锁）。
     *
     * <p>SQL 使用 FOR UPDATE，对统计范围加行锁（或间隙锁），
     * 需要在事务中调用，用于创建预约时的并发名额控制。</p>
     */
    int countByDateAndSlotWithLock(@Param("date") String date, @Param("slotId") String slotId);
    
    /**
     * 查询用户的预约列表（我的预约）。
     *
     * <p>对应 XML：AppointmentMapper.xml#selectUserAppointmentsWithDetails
     * 会联表回填：服务标题/封面、时间段文字（slotTime）等字段。</p>
     */
    List<Appointment> selectUserAppointmentsWithDetails(@Param("userId") String userId);
    
    /**
     * 查询服务提供者收到的预约列表。
     *
     * <p>对应 XML：AppointmentMapper.xml#selectReceivedAppointmentsWithDetails
     * 过滤条件为：b_thing.user_id = userId（即服务发布者/提供者）。
     * 同时联表回填预约用户用户名、服务信息与时间段文字。</p>
     */
    List<Appointment> selectReceivedAppointmentsWithDetails(@Param("userId") String userId);
    
    /**
     * 管理端：查询全部预约列表。
     *
     * <p>对应 XML：AppointmentMapper.xml#getAllAppointments
     * 会联表回填预约用户、服务信息与时间段文字。</p>
     */
    List<Appointment> getAllAppointments();
}
