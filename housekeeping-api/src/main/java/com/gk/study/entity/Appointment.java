package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("b_appointment")
public class Appointment implements Serializable {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    // 预约编号
    @TableField
    private String appointmentNumber;
    
    // 服务ID
    @TableField
    private String thingId;
    
    // 用户ID
    @TableField
    private String userId;
    
    // 预约日期 (yyyy-MM-dd)
    @TableField
    private String appointmentDate;
    
    // 时间段ID
    @TableField
    private String slotId;
    
    // 预约状态: 0-待服务, 1-已完成, 2-已取消
    @TableField
    private String status;
    
    // 接收人姓名
    @TableField
    private String receiverName;
    
    // 接收人电话
    @TableField
    private String receiverPhone;
    
    // 接收人地址
    @TableField
    private String receiverAddress;
    
    // 备注
    @TableField
    private String remark;
    
    // 创建时间
    @TableField
    private String createTime;
    
    // 关联字段（不存储在数据库）
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String thingTitle;
    
    @TableField(exist = false)
    private String cover; // 服务封面图
    
    @TableField(exist = false)
    private String slotTime; // 例如: "09:00-11:00"
}
