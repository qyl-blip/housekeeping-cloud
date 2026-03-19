-- 推荐权重配置表
CREATE TABLE IF NOT EXISTS `b_recommend_config` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`           VARCHAR(64)  NOT NULL COMMENT '配置名称',
  `pv_weight`      DOUBLE       NOT NULL DEFAULT 0.4  COMMENT '浏览量权重',
  `wish_weight`    DOUBLE       NOT NULL DEFAULT 0.2  COMMENT '心愿数权重',
  `collect_weight` DOUBLE       NOT NULL DEFAULT 0.3  COMMENT '收藏数权重',
  `score_weight`   DOUBLE       NOT NULL DEFAULT 0.1  COMMENT '评分权重',
  `enabled`        TINYINT      NOT NULL DEFAULT 0    COMMENT '是否启用(1=启用,0=禁用)',
  `remark`         VARCHAR(255)          DEFAULT NULL COMMENT '备注',
  `update_time`    VARCHAR(32)           DEFAULT NULL COMMENT '更新时间戳',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐权重配置表';

-- 插入默认配置（启用状态）
INSERT INTO `b_recommend_config` (`name`, `pv_weight`, `wish_weight`, `collect_weight`, `score_weight`, `enabled`, `remark`)
VALUES ('默认配置', 0.4, 0.2, 0.3, 0.1, 1, '系统默认推荐权重，浏览量40%+收藏30%+心愿20%+评分10%');
