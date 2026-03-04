-- ============================================
-- 预约时间选择功能 - 数据库表结构
-- ============================================
-- 创建日期: 2024
-- 说明: 包含预约表和时间段配置表的定义
-- ============================================

SET NAMES utf8mb4;

-- ============================================
-- 表: b_appointment (预约表)
-- 说明: 存储用户的预约记录，包含预约日期、时间段、接收人信息等
-- ============================================
CREATE TABLE IF NOT EXISTS b_appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    appointment_number VARCHAR(50) NOT NULL UNIQUE COMMENT '预约编号，格式：APT+日期+序列号',
    thing_id VARCHAR(20) NOT NULL COMMENT '服务ID，关联服务表',
    user_id VARCHAR(20) NOT NULL COMMENT '用户ID，关联用户表',
    appointment_date VARCHAR(10) NOT NULL COMMENT '预约日期，格式：yyyy-MM-dd',
    slot_id VARCHAR(10) NOT NULL COMMENT '时间段ID，关联时间段配置表',
    status VARCHAR(1) DEFAULT '0' COMMENT '预约状态: 0-待服务, 1-已完成, 2-已取消',
    receiver_name VARCHAR(50) NOT NULL COMMENT '接收人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '接收人电话',
    receiver_address VARCHAR(200) NOT NULL COMMENT '接收人地址',
    remark VARCHAR(500) COMMENT '备注信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引定义
    INDEX idx_date_slot (appointment_date, slot_id) COMMENT '日期和时间段组合索引，用于查询时间段可用性',
    INDEX idx_user (user_id) COMMENT '用户索引，用于查询用户的预约记录',
    INDEX idx_thing (thing_id) COMMENT '服务索引，用于查询服务的预约记录',
    INDEX idx_status (status) COMMENT '状态索引，用于查询不同状态的预约',
    INDEX idx_create_time (create_time) COMMENT '创建时间索引，用于按时间排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- ============================================
-- 表: b_time_slot (时间段配置表)
-- 说明: 配置可用的预约时间段，包含时间范围和容量限制
-- ============================================
CREATE TABLE IF NOT EXISTS b_time_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    slot_id VARCHAR(10) NOT NULL UNIQUE COMMENT '时间段标识，唯一标识一个时间段',
    start_time VARCHAR(5) NOT NULL COMMENT '开始时间，格式：HH:mm',
    end_time VARCHAR(5) NOT NULL COMMENT '结束时间，格式：HH:mm',
    max_capacity INT DEFAULT 5 COMMENT '最大容量，同一时间段可预约的最大数量',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用: 1-启用, 0-禁用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序，数字越小越靠前',
    
    -- 索引定义
    INDEX idx_enabled (enabled) COMMENT '启用状态索引，用于查询启用的时间段',
    INDEX idx_sort_order (sort_order) COMMENT '排序索引，用于按顺序获取时间段'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段配置表';

-- ============================================
-- 初始化时间段配置数据
-- 说明: 插入默认的时间段配置
-- ============================================
INSERT INTO b_time_slot (slot_id, start_time, end_time, max_capacity, enabled, sort_order) VALUES
('1', '09:00', '11:00', 5, 1, 1),
('2', '11:00', '13:00', 5, 1, 2),
('3', '14:00', '16:00', 5, 1, 3),
('4', '16:00', '18:00', 5, 1, 4),
('5', '18:00', '20:00', 5, 1, 5)
ON DUPLICATE KEY UPDATE 
    start_time = VALUES(start_time),
    end_time = VALUES(end_time),
    max_capacity = VALUES(max_capacity),
    enabled = VALUES(enabled),
    sort_order = VALUES(sort_order);

-- ============================================
-- 验证表创建
-- ============================================
-- 查看表结构
-- DESCRIBE b_appointment;
-- DESCRIBE b_time_slot;

-- 查看索引
-- SHOW INDEX FROM b_appointment;
-- SHOW INDEX FROM b_time_slot;

-- 查看时间段配置
-- SELECT * FROM b_time_slot ORDER BY sort_order;
