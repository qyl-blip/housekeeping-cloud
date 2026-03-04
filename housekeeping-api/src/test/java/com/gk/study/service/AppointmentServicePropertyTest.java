package com.gk.study.service;

import com.gk.study.dto.TimeSlotDTO;
import com.gk.study.entity.Appointment;
import com.gk.study.entity.TimeSlot;
import com.gk.study.exception.AppointmentConflictException;
import com.gk.study.mapper.AppointmentMapper;
import com.gk.study.mapper.TimeSlotMapper;
import com.gk.study.service.impl.AppointmentServiceImpl;
import net.jqwik.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 预约服务属性测试
 * Feature: appointment-time-selection
 */
public class AppointmentServicePropertyTest {
    
    /**
     * **Validates: Requirements 3.2**
     * 
     * Feature: appointment-time-selection, Property 4: 时间段可用性正确性
     * 
     * 属性：对于任意时间段，如果该时间段的已预约数量达到最大容量，
     * 则该时间段应该被标记为不可用并在UI上禁用。
     */
    @Property(tries = 100)
    void timeSlotAvailabilityShouldBeCorrect(
            @ForAll("validDates") String date,
            @ForAll("timeSlots") TimeSlot slot,
            @ForAll("bookingCounts") int bookingCount
    ) {
        // 创建 mock 对象
        AppointmentMapper appointmentMapper = Mockito.mock(AppointmentMapper.class);
        TimeSlotMapper timeSlotMapper = Mockito.mock(TimeSlotMapper.class);
        
        // 创建服务实例并注入 mock
        AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();
        // 使用反射设置私有字段
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
        
        // 设置时间段配置
        when(timeSlotMapper.selectAllEnabled()).thenReturn(Collections.singletonList(slot));
        
        // 设置预约数量
        when(appointmentMapper.countByDateAndSlot(date, slot.getSlotId())).thenReturn(bookingCount);
        
        // 调用服务方法
        List<TimeSlotDTO> slots = appointmentService.getAvailableSlots(date, "thing123");
        
        // 验证返回结果
        assertThat(slots).hasSize(1);
        TimeSlotDTO targetSlot = slots.get(0);
        
        // 验证时间段信息
        assertThat(targetSlot.getSlotId()).isEqualTo(slot.getSlotId());
        assertThat(targetSlot.getStartTime()).isEqualTo(slot.getStartTime());
        assertThat(targetSlot.getEndTime()).isEqualTo(slot.getEndTime());
        assertThat(targetSlot.getBookedCount()).isEqualTo(bookingCount);
        assertThat(targetSlot.getMaxCapacity()).isEqualTo(slot.getMaxCapacity());
        
        // 核心验证：如果预约数达到或超过最大容量，则不可用；否则可用
        if (bookingCount >= slot.getMaxCapacity()) {
            assertThat(targetSlot.getAvailable())
                .as("当预约数(%d)达到最大容量(%d)时，时间段应该标记为不可用", 
                    bookingCount, slot.getMaxCapacity())
                .isFalse();
        } else {
            assertThat(targetSlot.getAvailable())
                .as("当预约数(%d)小于最大容量(%d)时，时间段应该标记为可用", 
                    bookingCount, slot.getMaxCapacity())
                .isTrue();
        }
    }
    
    /**
     * 生成有效的日期字符串（yyyy-MM-dd格式）
     */
    @Provide
    Arbitrary<String> validDates() {
        return Arbitraries.integers()
            .between(0, 365)
            .map(days -> LocalDate.now().plusDays(days))
            .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
    
    /**
     * 生成时间段配置
     */
    @Provide
    Arbitrary<TimeSlot> timeSlots() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofLength(2),  // slotId
            Arbitraries.integers().between(0, 23),       // 开始小时
            Arbitraries.integers().between(1, 10)        // 最大容量
        ).as((slotId, startHour, maxCapacity) -> {
            TimeSlot slot = new TimeSlot();
            slot.setId((long) Math.abs(slotId.hashCode()));
            slot.setSlotId(slotId);
            slot.setStartTime(String.format("%02d:00", startHour));
            slot.setEndTime(String.format("%02d:00", (startHour + 2) % 24));
            slot.setMaxCapacity(maxCapacity);
            slot.setEnabled(true);
            slot.setSortOrder(0);
            return slot;
        });
    }
    
    /**
     * 生成预约数量（0到15之间）
     */
    @Provide
    Arbitrary<Integer> bookingCounts() {
        return Arbitraries.integers().between(0, 15);
    }
    
    /**
     * **Validates: Requirements 7.3**
     * 
     * Feature: appointment-time-selection, Property 10: 并发冲突处理
     * 
     * 属性：对于任意预约请求，如果在提交时所选时间段已被其他用户预约（并发冲突），
     * 系统应该拒绝该预约、显示冲突提示信息、并刷新该日期的可用时间段列表。
     */
    @Property(tries = 100)
    void concurrentBookingShouldBeRejected(
            @ForAll("appointments") Appointment appointment,
            @ForAll("timeSlots") TimeSlot slot
    ) {
        // 创建 mock 对象
        AppointmentMapper appointmentMapper = Mockito.mock(AppointmentMapper.class);
        TimeSlotMapper timeSlotMapper = Mockito.mock(TimeSlotMapper.class);
        
        // 创建服务实例并注入 mock
        AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();
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
        
        // 设置预约使用的时间段ID
        appointment.setSlotId(slot.getSlotId());
        
        // 模拟时间段配置查询
        when(timeSlotMapper.selectBySlotId(slot.getSlotId())).thenReturn(slot);
        
        // 模拟时间段已满的情况：当前预约数等于最大容量
        when(appointmentMapper.countByDateAndSlotWithLock(
            appointment.getAppointmentDate(), 
            slot.getSlotId()
        )).thenReturn(slot.getMaxCapacity());
        
        // 验证：尝试预约已满的时间段应该抛出 AppointmentConflictException
        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
            .isInstanceOf(AppointmentConflictException.class)
            .hasMessageContaining("该时间段已被预约");
        
        // 验证：刷新时间段列表后，该时间段应该标记为不可用
        when(timeSlotMapper.selectAllEnabled()).thenReturn(Collections.singletonList(slot));
        when(appointmentMapper.countByDateAndSlot(
            appointment.getAppointmentDate(), 
            slot.getSlotId()
        )).thenReturn(slot.getMaxCapacity());
        
        List<TimeSlotDTO> refreshedSlots = appointmentService.getAvailableSlots(
            appointment.getAppointmentDate(), 
            appointment.getThingId()
        );
        
        assertThat(refreshedSlots).hasSize(1);
        TimeSlotDTO targetSlot = refreshedSlots.get(0);
        assertThat(targetSlot.getSlotId()).isEqualTo(slot.getSlotId());
        assertThat(targetSlot.getAvailable())
            .as("时间段已满后应该标记为不可用")
            .isFalse();
    }
    
    /**
     * 生成预约对象
     */
    @Provide
    Arbitrary<Appointment> appointments() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),  // thingId
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),  // userId
            validDates(),                                                    // appointmentDate
            Arbitraries.strings().alpha().ofLength(2),                      // slotId
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),  // receiverName
            Arbitraries.strings().numeric().ofLength(11),                   // receiverPhone
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(200)  // receiverAddress
        ).as((thingId, userId, date, slotId, name, phone, address) -> {
            Appointment appointment = new Appointment();
            appointment.setThingId(thingId);
            appointment.setUserId(userId);
            appointment.setAppointmentDate(date);
            appointment.setSlotId(slotId);
            appointment.setReceiverName(name);
            appointment.setReceiverPhone(phone);
            appointment.setReceiverAddress(address);
            appointment.setStatus("0");
            return appointment;
        });
    }


    /**
     * **Validates: Requirements 4.2**
     *
     * Feature: appointment-time-selection, Property 14: 预约编号唯一性
     *
     * 属性：对于任意两个不同的预约记录，它们的预约编号应该是唯一的，不存在重复。
     */
    @Property(tries = 100)
    void appointmentNumbersShouldBeUnique(
            @ForAll("appointmentLists") List<Appointment> appointments
    ) {
        // 假设：至少需要2个预约才能测试唯一性
        Assume.that(appointments.size() >= 2);

        // 创建 mock 对象
        AppointmentMapper appointmentMapper = Mockito.mock(AppointmentMapper.class);
        TimeSlotMapper timeSlotMapper = Mockito.mock(TimeSlotMapper.class);

        // 创建服务实例并注入 mock
        AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();
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

        // 为每个预约创建一个时间段配置
        List<String> appointmentNumbers = new java.util.ArrayList<>();

        for (Appointment appointment : appointments) {
            // 创建时间段配置
            TimeSlot slot = new TimeSlot();
            slot.setSlotId(appointment.getSlotId());
            slot.setMaxCapacity(10); // 足够大的容量
            slot.setEnabled(true);

            // 模拟时间段配置查询
            when(timeSlotMapper.selectBySlotId(appointment.getSlotId())).thenReturn(slot);

            // 模拟时间段可用（预约数为0）
            when(appointmentMapper.countByDateAndSlotWithLock(
                appointment.getAppointmentDate(),
                appointment.getSlotId()
            )).thenReturn(0);

            // 模拟插入操作，捕获预约编号
            when(appointmentMapper.insert(Mockito.any(Appointment.class))).thenAnswer(invocation -> {
                Appointment apt = invocation.getArgument(0);
                // 模拟数据库自动生成ID
                apt.setId((long) (Math.random() * 1000000));
                return 1;
            });

            // 创建预约
            appointmentService.createAppointment(appointment);

            // 收集预约编号
            assertThat(appointment.getAppointmentNumber())
                .as("预约编号应该被生成")
                .isNotNull();
            appointmentNumbers.add(appointment.getAppointmentNumber());
        }

        // 核心验证：所有预约编号应该唯一，无重复
        assertThat(appointmentNumbers)
            .as("所有预约编号应该唯一，不存在重复")
            .doesNotHaveDuplicates();

        // 额外验证：所有预约编号应该符合格式要求
        for (String number : appointmentNumbers) {
            assertThat(number)
                .as("预约编号应该符合格式：APT + yyyyMMdd + 6位序列号")
                .matches("APT\\d{8}\\d{6}");
        }
    }

    /**
     * 生成预约列表（2-10个预约）
     */
    @Provide
    Arbitrary<List<Appointment>> appointmentLists() {
        return appointments().list().ofMinSize(2).ofMaxSize(10);
    }

}
