package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.RecommendConfig;
import com.gk.study.mapper.RecommendConfigMapper;
import com.gk.study.service.RecommendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendConfigServiceImpl extends ServiceImpl<RecommendConfigMapper, RecommendConfig>
        implements RecommendConfigService {

    @Autowired
    RecommendConfigMapper mapper;

    @Override
    public List<RecommendConfig> getConfigList() {
        return mapper.selectList(new QueryWrapper<RecommendConfig>().orderByDesc("id"));
    }

    @Override
    public void createConfig(RecommendConfig config) {
        config.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(config);
    }

    @Override
    public void updateConfig(RecommendConfig config) {
        config.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(config);
    }

    @Override
    public void deleteConfig(String id) {
        mapper.deleteById(id);
    }

    @Override
    public RecommendConfig getActiveConfig() {
        QueryWrapper<RecommendConfig> qw = new QueryWrapper<>();
        qw.eq("enabled", 1).orderByDesc("id").last("LIMIT 1");
        RecommendConfig config = mapper.selectOne(qw);
        if (config == null) {
            // 返回默认权重
            config = new RecommendConfig();
            config.setPvWeight(0.4);
            config.setWishWeight(0.2);
            config.setCollectWeight(0.3);
            config.setScoreWeight(0.1);
        }
        return config;
    }
}
