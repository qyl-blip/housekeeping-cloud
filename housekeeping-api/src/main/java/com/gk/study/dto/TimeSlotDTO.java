package com.gk.study.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 时间段数据传输对象
 * 用于返回时间段可用状态信息
 */
@Data
public class TimeSlotDTO implements Serializable {
    
    // 时间段ID
    private String slotId;
    
    // 开始时间
    private String startTime;
    
    // 结束时间
    private String endTime;
    
    // 是否可用
    private Boolean available;
    
    // 已预约数量
    private Integer bookedCount;
    
    // 最大容量
    private Integer maxCapacity;
}
