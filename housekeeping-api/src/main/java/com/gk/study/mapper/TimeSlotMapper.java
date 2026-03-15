package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.TimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 时间段配置（TimeSlot）数据访问层。
 *
 * <p>对应表：b_time_slot。
 * 用于预约模块展示“可选择的服务时间段”，以及在创建预约时校验时间段是否启用。</p>
 */
@Mapper
public interface TimeSlotMapper extends BaseMapper<TimeSlot> {
    
    /**
     * 查询所有启用的时间段配置（enabled=1），按 sort_order 升序。
     */
    @Select("SELECT * FROM b_time_slot WHERE enabled = 1 ORDER BY sort_order ASC")
    List<TimeSlot> selectAllEnabled();
    
    /**
     * 按 slot_id 查询时间段配置。
     */
    @Select("SELECT * FROM b_time_slot WHERE slot_id = #{slotId}")
    TimeSlot selectBySlotId(String slotId);
}
