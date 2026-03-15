package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Ad;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.AdService;
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
 * 广告（Ad）模块接口。
 *
 * <p>广告属于运营内容：
 * 1) 图片上传后落盘到 {@code File.uploadPath/image/}；
 * 2) 数据库记录保存图片文件名（ad.image）等元信息。</p>
 */
@RestController
@RequestMapping("/ad")
public class AdController {

    private final static Logger logger = LoggerFactory.getLogger(AdController.class);

    @Autowired
    AdService service;

    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 广告列表。
     *
     * <p>支持可选分页：page/pageSize 同时传入时进行内存分页。</p>
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Integer page, Integer pageSize){
        List<Ad> list = service.getAdList();
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增广告（管理员）。
     *
     * <p>如果携带图片文件：先上传落盘，再把生成的文件名写入 ad.image。</p>
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Ad ad) throws IOException {
        String image = saveAd(ad);
        if(StringUtils.hasText(image)) {
            ad.image = image;
        }
        service.createAd(ad);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 删除广告（管理员，支持批量）。
     *
     * @param ids 逗号分隔的广告ID
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
                service.deleteAd(id.trim());
            }
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新广告（管理员）。
     *
     * <p>如果携带新图片文件：先上传落盘，并覆盖 ad.image 字段为新文件名。</p>
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Ad ad) throws IOException {
        String image = saveAd(ad);
        if(StringUtils.hasText(image)) {
            ad.image = image;
        }
        service.updateAd(ad);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 保存广告图片到本地，并返回生成的文件名。
     *
     * <p>落盘路径：{@code ${File.uploadPath}/image/{uuid}.{ext}}。</p>
     */
    private String saveAd(Ad ad) throws IOException {
        MultipartFile file = ad.getImageFile();
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
            ad.image = newFileName;
        }
        return newFileName;
    }
}