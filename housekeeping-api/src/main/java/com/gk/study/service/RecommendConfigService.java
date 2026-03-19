package com.gk.study.service;

import com.gk.study.entity.RecommendConfig;

import java.util.List;

/**
 * 推荐权重配置业务接口。
 */
public interface RecommendConfigService {

    List<RecommendConfig> getConfigList();

    void createConfig(RecommendConfig config);

    void updateConfig(RecommendConfig config);

    void deleteConfig(String id);

    /** 获取当前启用的配置，若无则返回默认值 */
    RecommendConfig getActiveConfig();
}
