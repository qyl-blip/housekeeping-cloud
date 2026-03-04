package com.gk.study.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("b_thing") // 对应数据库表名
public class Thing implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;  // 服务标题
    private Double price;  // 价格
    private String cover;  // 封面图
    private String city;   // 【新增】城市字段
    private Double latitude; // 纬度
    private Double longitude; // 经度
    private Integer visitCount; // 浏览量
    private Double score; // 机器学习计算的分数
}