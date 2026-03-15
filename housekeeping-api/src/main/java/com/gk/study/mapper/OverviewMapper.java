package com.gk.study.mapper;

import com.gk.study.entity.VisitData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 概览统计（Overview）数据访问层。
 *
 * <p>提供后台概览页的统计查询（热门服务/分类、访问数据等）。
 * 注意：当前 {@code OverviewController} 走 JdbcTemplate 直查，这个 Mapper 可能属于早期/备用实现，保留以便后续切回 MyBatis 方案。</p>
 */
@Mapper
public interface OverviewMapper {

    /**
     * 查询热门服务列表（一般用于 TOP 榜）。
     */
    List<Object> getPopularThing();
    /**
     * 查询热门分类统计（用于分类占比/排行）。
     */
    List<Object> getPopularClassification();
    /**
     * 查询指定日期的访问数据（按 IP 聚合）。
     *
     * @param day 日期字符串（通常是 yyyy-MM-dd），用于匹配 re_time
     */
    List<VisitData> getWebVisitData(@Param("day") String day);

}









