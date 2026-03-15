package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.service.BannerService;
import com.gk.study.entity.Banner;
import com.gk.study.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图（Banner）业务实现。
 *
 * <p>轮播图属于运营内容：图片上传与落盘在 Controller 中处理，本层负责记录的增删改查。</p>
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
    @Autowired
    BannerMapper mapper;

    /**
     * 查询轮播图列表。
     */
    @Override
    public List<Banner> getBannerList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 新增轮播图（自动补齐 createTime）。
     */
    @Override
    public void createBanner(Banner banner) {
        banner.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(banner);
    }

    /**
     * 删除轮播图。
     */
    @Override
    public void deleteBanner(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新轮播图。
     */
    @Override
    public void updateBanner(Banner banner) {
        mapper.updateById(banner);
    }
}









