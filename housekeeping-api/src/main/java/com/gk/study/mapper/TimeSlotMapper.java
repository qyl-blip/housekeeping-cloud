package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.TimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TimeSlotMapper 接口
 * 提供时间段配置数据访问功能
 */
@Mapper
public interface TimeSlotMapper extends BaseMapper<TimeSlot> {
    
    /**
     * 查询所有启用的时间段，按排序字段升序排列
     * @return 启用的时间段列表
     */
    @Select("SELECT * FROM b_time_slot WHERE enabled = 1 ORDER BY sort_order ASC")
    List<TimeSlot> selectAllEnabled();
    
    /**
     * 根据时间段ID查询时间段配置
     * @param slotId 时间段ID
     * @return 时间段配置
     */
    @Select("SELECT * FROM b_time_slot WHERE slot_id = #{slotId}")
    TimeSlot selectBySlotId(String slotId);
}
