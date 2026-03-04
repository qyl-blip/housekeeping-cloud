-- ============================================
-- 清除数据脚本 - 只保留admin用户
-- 数据库: java_jiazheng
-- 执行前请备份数据库！
-- ============================================

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空预约相关表
TRUNCATE TABLE b_appointment;
TRUNCATE TABLE b_time_slot;

-- 清空订单表
TRUNCATE TABLE b_order;

-- 清空服务相关表
TRUNCATE TABLE b_thing;
TRUNCATE TABLE b_thing_collect;
TRUNCATE TABLE b_thing_wish;
TRUNCATE TABLE b_thing_tag;

-- 清空评论表
TRUNCATE TABLE b_comment;

-- 清空分类和标签表
TRUNCATE TABLE b_classification;
TRUNCATE TABLE b_tag;

-- 清空地址表
TRUNCATE TABLE b_address;

-- 清空广告和横幅表
TRUNCATE TABLE b_ad;
TRUNCATE TABLE b_banner;

-- 清空公告表
TRUNCATE TABLE b_notice;

-- 清空日志表
TRUNCATE TABLE b_op_log;
TRUNCATE TABLE b_error_log;

-- 清空用户表，但保留admin用户
DELETE FROM b_user WHERE username != 'admin';

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示剩余的用户
SELECT * FROM b_user;
