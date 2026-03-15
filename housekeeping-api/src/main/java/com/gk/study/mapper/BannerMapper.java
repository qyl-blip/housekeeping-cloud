package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Banner;
import org.apache.ibatis.annotations.Mapper;

/**
 * 轮播图（Banner）Mapper。
 *
 * <p>对应表：b_banner。轮播图属于运营内容：
 * 图片文件的上传与落盘通常在 Controller 层处理，数据库中一般只保存文件名/相对路径等元信息。</p>
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

}









