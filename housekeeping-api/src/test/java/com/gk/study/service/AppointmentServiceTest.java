package com.gk.study.service;

import com.gk.study.entity.Appointment;
import com.gk.study.entity.TimeSlot;
import com.gk.study.exception.AppointmentConflictException;
import com.gk.study.exception.InvalidDateException;
import com.gk.study.exception.InvalidSlotException;
import com.gk.study.mapper.AppointmentMapper;
import com.gk.study.mapper.TimeSlotMapper;
import com.gk.study.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 预约服务单元测试
 * 测试 createAppointment 方法的各种场景
 */
public class AppointmentServiceTest {
    
    private AppointmentServiceImpl appointmentService;
    private AppointmentMapper appointmentMapper;
    private TimeSlotMapper timeSlotMapper;
    
    @BeforeEach
    void setUp() {
        appointmentMapper = Mockito.mock(AppointmentMapper.class);
        timeSlotMapper = Mockito.mock(TimeSlotMapper.class);
        
        appointmentService = new AppointmentServiceImpl();
        
        // 使用反射注入 mock 对象
        try {
            java.lang.reflect.Field appointmentMapperField = AppointmentServiceImpl.class.getDeclaredField("appointmentMapper");
            appointmentMapperField.setAccessible(true);
            appointmentMapperField.set(appointmentService, appointmentMapper);
            
            java.lang.reflect.Field timeSlotMapperField = AppointmentServiceImpl.class.getDeclaredField("timeSlotMapper");
            timeSlotMapperField.setAccessible(true);
            timeSlotMapperField.set(appointmentService, timeSlotMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }
    }
    
    /**
     * 测试成功创建预约
     * 验证需求: 4.1, 4.2
     */
    @Test
    void shouldCreateAppointmentSuccessfully() {
        // 准备测试数据
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(futureDate);
        appointment.setSlotId("1");
        appointment.setThingId("123");
        appointment.setUserId("456");
        appointment.setReceiverName("张三");
        appointment.setReceiverPhone("13800138000");
        appointment.setReceiverAddress("北京市朝阳区");
        
        // 设置时间段配置
        TimeSlot slot = new TimeSlot();
        slot.setId(1L);
        slot.setSlotId("1");
        slot.setMaxCapacity(5);
        slot.setEnabled(true);
        
        when(timeSlotMapper.selectBySlotId("1")).thenReturn(slot);
        when(appointmentMapper.countByDateAndSlotWithLock(futureDate, "1")).thenReturn(2);
        when(appointmentMapper.insert(any(Appointment.class))).thenReturn(1);
        
        // 执行测试
        appointmentService.createAppointment(appointment);
        
        // 验证预约编号已生成
        assertThat(appointment.getAppointmentNumber()).isNotNull();
        assertThat(appointment.getAppointmentNumber()).startsWith("APT");
        assertThat(appointment.getAppointmentNumber()).hasSize(17); // APT + 8位日期 + 6位序列号
        
        // 验证 insert 方法被调用
        verify(appointmentMapper, times(1)).insert(appointment);
    }
    
    /**
     * 测试预约过去的日期应抛出异常
     * 验证需求: 7.3
     */
    @Test
    void shouldThrowExceptionWhenAppointmentDateIsInPast() {
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(pastDate);
        appointment.setSlotId("1");
        
        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
            .isInstanceOf(InvalidDateException.class)
            .hasMessageContaining("不能预约过去的日期");
    }
    
    /**
     * 测试无效的时间段ID应抛出异常
     * 验证需求: 7.3
     */
    @Test
    void shouldThrowExceptionWhenSlotIdIsInvalid() {
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(futureDate);
        appointment.setSlotId("999");
        
        when(timeSlotMapper.selectBySlotId("999")).thenReturn(null);
        
        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
            .isInstanceOf(InvalidSlotException.class)
            .hasMessageContaining("无效的时间段ID");
    }
    
    /**
     * 测试时间段已禁用应抛出异常
     * 验证需求: 7.3
     */
    @Test
    void shouldThrowExceptionWhenSlotIsDisabled() {
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(futureDate);
        appointment.setSlotId("1");
        
        TimeSlot slot = new TimeSlot();
        slot.setId(1L);
        slot.setSlotId("1");
        slot.setEnabled(false);
        
        when(timeSlotMapper.selectBySlotId("1")).thenReturn(slot);
        
        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
            .isInstanceOf(InvalidSlotException.class)
            .hasMessageContaining("该时间段已被禁用");
    }
    
    /**
     * 测试时间段已满应抛出冲突异常
     * 验证需求: 7.3
     */
    @Test
    void shouldThrowConflictExceptionWhenSlotIsFull() {
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(futureDate);
        appointment.setSlotId("1");
        
        TimeSlot slot = new TimeSlot();
        slot.setId(1L);
        slot.setSlotId("1");
        slot.setMaxCapacity(5);
        slot.setEnabled(true);
        
        when(timeSlotMapper.selectBySlotId("1")).thenReturn(slot);
        when(appointmentMapper.countByDateAndSlotWithLock(futureDate, "1")).thenReturn(5);
        
        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
            .isInstanceOf(AppointmentConflictException.class)
            .hasMessageContaining("该时间段已被预约");
    }
    
    /**
     * 测试预约编号格式正确
     * 验证需求: 4.2
     */
    @Test
    void shouldGenerateCorrectAppointmentNumberFormat() {
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(futureDate);
        appointment.setSlotId("1");
        
        TimeSlot slot = new TimeSlot();
        slot.setId(1L);
        slot.setSlotId("1");
        slot.setMaxCapacity(5);
        slot.setEnabled(true);
        
        when(timeSlotMapper.selectBySlotId("1")).thenReturn(slot);
        when(appointmentMapper.countByDateAndSlotWithLock(futureDate, "1")).thenReturn(0);
        when(appointmentMapper.insert(any(Appointment.class))).thenReturn(1);
        
        appointmentService.createAppointment(appointment);
        
        String appointmentNumber = appointment.getAppointmentNumber();
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        
        // 验证格式：APT + yyyyMMdd + 6位序列号
        assertThat(appointmentNumber).startsWith("APT" + today);
        assertThat(appointmentNumber).matches("APT\\d{8}\\d{6}");
    }
}
