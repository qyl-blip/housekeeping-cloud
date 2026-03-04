package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("b_time_slot")
public class TimeSlot implements Serializable {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    // 时间段标识
    @TableField
    private String slotId;
    
    // 开始时间
    @TableField
    private String startTime;
    
    // 结束时间
    @TableField
    private String endTime;
    
    // 最大容量（同一时间段可预约数量）
    @TableField
    private Integer maxCapacity;
    
    // 是否启用
    @TableField
    private Boolean enabled;
    
    // 排序
    @TableField
    private Integer sortOrder;
}
