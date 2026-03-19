package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 推荐权重配置实体。
 *
 * <p>管理员可通过后台调节各维度权重，影响"智能推荐"排序结果。
 * 综合得分 = pv * pvWeight + wishCount * wishWeight + collectCount * collectWeight + score * scoreWeight</p>
 */
@Data
@TableName("b_recommend_config")
public class RecommendConfig implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    /** 配置名称（如"默认配置"） */
    @TableField
    public String name;

    /** 浏览量权重（默认 0.4） */
    @TableField
    public Double pvWeight;

    /** 心愿数权重（默认 0.2） */
    @TableField
    public Double wishWeight;

    /** 收藏数权重（默认 0.3） */
    @TableField
    public Double collectWeight;

    /** 评分权重（默认 0.1） */
    @TableField
    public Double scoreWeight;

    /** 是否启用（1=启用，0=禁用） */
    @TableField
    public Integer enabled;

    /** 备注 */
    @TableField
    public String remark;

    /** 更新时间 */
    @TableField
    public String updateTime;
}
