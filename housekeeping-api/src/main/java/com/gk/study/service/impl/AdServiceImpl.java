package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.AdMapper;
import com.gk.study.service.AdService;
import com.gk.study.entity.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 广告（Ad）业务实现。
 *
 * <p>广告属于运营内容：图片上传与落盘在 Controller 中处理，本层负责记录的增删改查。</p>
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {
    @Autowired
    AdMapper mapper;

    /**
     * 查询广告列表。
     */
    @Override
    public List<Ad> getAdList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 新增广告（自动补齐 createTime）。
     */
    @Override
    public void createAd(Ad ad) {
        ad.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(ad);
    }

    /**
     * 删除广告。
     */
    @Override
    public void deleteAd(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新广告。
     */
    @Override
    public void updateAd(Ad ad) {
        mapper.updateById(ad);
    }
}









