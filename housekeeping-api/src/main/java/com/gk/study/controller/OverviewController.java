package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/overview")
public class OverviewController {

    private final static Logger logger = LoggerFactory.getLogger(OverviewController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public APIResponse list() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 获取最近7天的访问量数据
            List<Map<String, Object>> visitList = getVisitData();
            result.put("visitList", visitList);
            
            // 2. 获取热门家政服务排名（前10）
            List<Map<String, Object>> popularThings = getPopularThings();
            result.put("popularThings", popularThings);
            
            // 3. 获取热门分类比例
            List<Map<String, Object>> popularClassification = getPopularClassification();
            result.put("popularClassification", popularClassification);
            
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", result);
        } catch (Exception e) {
            logger.error("获取统计数据失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近7天的访问量数据
     */
    private List<Map<String, Object>> getVisitData() {
        List<Map<String, Object>> visitList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            String day = sdf.format(calendar.getTime());
            
            // 从操作日志表统计访问量
            String sql = "SELECT COUNT(DISTINCT re_ip) as uv, COUNT(*) as pv " +
                        "FROM b_op_log " +
                        "WHERE DATE(STR_TO_DATE(re_time, '%Y-%m-%d %H:%i:%s.%f')) = ?";
            
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateSdf.format(calendar.getTime());
            
            try {
                Map<String, Object> visitData = jdbcTemplate.queryForMap(sql, dateStr);
                Map<String, Object> item = new HashMap<>();
                item.put("day", day);
                item.put("uv", visitData.get("uv") != null ? visitData.get("uv") : 0);
                item.put("pv", visitData.get("pv") != null ? visitData.get("pv") : 0);
                visitList.add(item);
            } catch (Exception e) {
                // 如果查询失败，返回0
                Map<String, Object> item = new HashMap<>();
                item.put("day", day);
                item.put("uv", 0);
                item.put("pv", 0);
                visitList.add(item);
            }
        }
        
        return visitList;
    }

    /**
     * 获取热门家政服务排名（按订单数量）
     */
    private List<Map<String, Object>> getPopularThings() {
        String sql = "SELECT t.title, COUNT(o.id) as count " +
                    "FROM b_thing t " +
                    "LEFT JOIN b_order o ON t.id = o.thing_id " +
                    "GROUP BY t.id, t.title " +
                    "ORDER BY count DESC " +
                    "LIMIT 10";
        
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.error("获取热门家政服务失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热门分类比例
     */
    private List<Map<String, Object>> getPopularClassification() {
        String sql = "SELECT c.title, COUNT(t.id) as count " +
                    "FROM b_classification c " +
                    "LEFT JOIN b_thing t ON c.id = t.classification_id " +
                    "GROUP BY c.id, c.title " +
                    "HAVING count > 0 " +
                    "ORDER BY count DESC";
        
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.error("获取热门分类失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取系统信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/sysInfo", method = RequestMethod.GET)
    public APIResponse sysInfo() {
        try {
            Map<String, Object> sysInfo = new HashMap<>();
            
            // 系统基本信息
            sysInfo.put("sysName", "家政服务管理系统");
            sysInfo.put("versionName", "v1.0.0");
            
            // 操作系统信息
            sysInfo.put("osName", System.getProperty("os.name"));
            sysInfo.put("pf", System.getProperty("os.arch"));
            
            // CPU信息
            int cpuCount = Runtime.getRuntime().availableProcessors();
            sysInfo.put("cpuCount", cpuCount);
            sysInfo.put("processor", System.getProperty("os.arch"));
            
            // CPU使用率（简化版本）
            double cpuLoad = getCpuLoad();
            sysInfo.put("cpuLoad", String.format("%.2f", cpuLoad));
            
            // 内存信息
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            double totalMemoryGB = totalMemory / (1024.0 * 1024.0 * 1024.0);
            double usedMemoryGB = usedMemory / (1024.0 * 1024.0 * 1024.0);
            double maxMemoryGB = maxMemory / (1024.0 * 1024.0 * 1024.0);
            double percentMemory = (usedMemory * 100.0) / totalMemory;
            
            sysInfo.put("memory", String.format("%.2f", maxMemoryGB));
            sysInfo.put("usedMemory", String.format("%.2f", usedMemoryGB));
            sysInfo.put("percentMemory", String.format("%.2f", percentMemory));
            
            // Java版本
            sysInfo.put("jvmVersion", System.getProperty("java.version"));
            
            // 系统语言和时区
            sysInfo.put("sysLan", System.getProperty("user.language"));
            sysInfo.put("sysZone", TimeZone.getDefault().getID());
            
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", sysInfo);
        } catch (Exception e) {
            logger.error("获取系统信息失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取CPU使用率（简化版本）
     */
    private double getCpuLoad() {
        try {
            com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            return osBean.getSystemCpuLoad() * 100;
        } catch (Exception e) {
            // 如果获取失败，返回0
            return 0.0;
        }
    }
}
