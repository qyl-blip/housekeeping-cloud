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
    private String description; // 描述
    private String status; // 状态
    private String createTime; // 创建时间
    private String mobile; // 手机号
    private String age; // 年龄
    private String sex; // 性别
    private String city;   // 城市字段
    private String location; // 详细地址
    private Long classificationId; // 分类ID
    private String userId; // 用户ID
    private Double latitude; // 纬度
    private Double longitude; // 经度
    private Integer visitCount; // 浏览量
    private Integer pv; // 浏览量（另一个字段）
    private Integer recommendCount; // 推荐数
    private Integer wishCount; // 心愿数
    private Integer collectCount; // 收藏数
    private Double score; // 机器学习计算的分数
}