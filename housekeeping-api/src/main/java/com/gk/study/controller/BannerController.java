package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Banner;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 轮播图（Banner）模块接口。
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>查询轮播图列表（前台展示）</li>
 *   <li>管理员创建/更新/删除轮播图</li>
 * </ul>
 *
 * <p>图片上传说明：创建/更新时如携带 {@code imageFile}，会落盘到 {@code File.uploadPath/image/}
 * 目录，并把文件名写入 {@code banner.image} 字段。</p>
 */
@RestController
@RequestMapping("/banner")
public class BannerController {

    private final static Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    BannerService service;

    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 查询轮播图列表。
     *
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<Banner> list = service.getBannerList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增轮播图（管理员）。
     *
     * <p>如上传了图片文件，会自动保存图片并把文件名写入 banner.image。</p>
     *
     * @param banner 轮播图信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Banner banner) throws IOException {
        String image = saveBanner(banner);
        if(StringUtils.hasText(image)) {
            banner.image = image;
        }
        service.createBanner(banner);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 删除轮播图（管理员，支持批量）。
     *
     * @param ids 轮播图ID，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        if (!StringUtils.hasText(ids)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        String[] arr = ids.split(",");
        for (String id : arr) {
            if (StringUtils.hasText(id)) {
                service.deleteBanner(id.trim());
            }
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新轮播图（管理员）。
     *
     * <p>如上传了图片文件，会自动保存图片并更新 banner.image。</p>
     *
     * @param banner 轮播图信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Banner banner) throws IOException {
        String image = saveBanner(banner);
        if(StringUtils.hasText(image)) {
            banner.image = image;
        }
        service.updateBanner(banner);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 保存轮播图图片（如果前端携带了 imageFile）。
     *
     * <p>文件落盘路径：uploadPath/image/{uuid}.ext</p>
     *
     * @return 新文件名（不含目录），未上传则返回 null
     */
    private String saveBanner(Banner banner) throws IOException {
        MultipartFile file = banner.getImageFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            if (!StringUtils.hasText(oldFileName)) {
                return null;
            }
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(StringUtils.hasText(newFileName)) {
            banner.image = newFileName;
        }
        return newFileName;
    }
}