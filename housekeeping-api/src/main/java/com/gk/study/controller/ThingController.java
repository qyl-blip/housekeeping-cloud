package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Thing;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingService;
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
 * 服务（家政项目）模块接口。
 *
 * <p>主要提供：</p>
 * <ul>
 *   <li>前台服务列表/详情查询</li>
 *   <li>后台管理员创建/更新/删除服务</li>
 *   <li>普通用户发布服务（提交审核）、更新自己的服务（更新后重新审核）</li>
 * </ul>
 *
 * <p>说明：上传图片会保存到 uploadPath/image 目录，并把文件名写入 thing.cover。</p>
 */
@RestController
@RequestMapping("/thing")
public class ThingController {

    private final static Logger logger = LoggerFactory.getLogger(ThingController.class);

    @Autowired
    ThingService service;

    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 服务列表（支持关键词/排序/分类/标签筛选，可选分页）。
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String tag, Integer page, Integer pageSize) {
        List<Thing> list = service.getThingList(keyword, sort, c, tag);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 服务详情。
     *
     * <p>注意：在 Service 实现中，查询详情时会对 pv（浏览量）进行自增。</p>
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id) {
        Thing thing = service.getThingById(id);
        if (thing == null) {
            return new APIResponse(ResponeCode.FAIL, "未找到数据");
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", thing);
    }

    /**
     * 管理端创建服务。
     *
     * <p>如携带图片文件，会保存并写入 thing.cover。</p>
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        String image = saveThing(thing);
        if (!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        // 自动从 location 提取 city
        extractCityFromLocation(thing);
        service.createThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 管理端删除服务（支持逗号分隔的批量删除）。
     *
     * @param ids 服务ID列表，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThing(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 管理端更新服务。
     *
     * <p>如携带图片文件，会保存并更新 thing.cover。</p>
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        String image = saveThing(thing);
        if (!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        // 自动从 location 提取 city
        extractCityFromLocation(thing);
        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 查询某个用户发布的服务列表。
     *
     * @param userId 用户ID
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/listUserThing", method = RequestMethod.GET)
    public APIResponse listUserThing(String userId, Integer page, Integer pageSize) {
        List<Thing> list = service.getUserThing(userId);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 普通用户发布服务（提交审核）。
     *
     * <p>当前约定：status="1" 表示“下架/待审核”。用户提交时会把状态置为待审核。</p>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/userCreate", method = RequestMethod.POST)
    @Transactional
    public APIResponse userCreate(Thing thing) throws IOException {
        String image = saveThing(thing);
        if (!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        if (StringUtils.isEmpty(thing.status)) {
            thing.status = "1";
        }
        // 自动从 location 提取 city
        extractCityFromLocation(thing);
        service.createThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "提交成功，等待审核");
    }

    /**
     * 普通用户更新自己的服务。
     *
     * <p>规则：</p>
     * <ul>
     *   <li>必须是该服务的发布者（thing.userId）才允许修改</li>
     *   <li>修改后状态会改为待审核（status="1"）</li>
     * </ul>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/userUpdate", method = RequestMethod.POST)
    @Transactional
    public APIResponse userUpdate(Thing thing) throws IOException {
        Thing existingThing = service.getThingById(String.valueOf(thing.id));
        if (existingThing == null) {
            return new APIResponse(ResponeCode.FAIL, "服务不存在");
        }
        if (!existingThing.userId.equals(thing.userId)) {
            return new APIResponse(ResponeCode.FAIL, "无权限修改此服务");
        }

        String image = saveThing(thing);
        if (!StringUtils.isEmpty(image)) {
            thing.cover = image;
        }
        thing.status = "1";
        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功，等待审核");
    }

    /**
     * 保存服务封面图片（如果前端携带了 imageFile）。
     *
     * <p>文件落盘路径：uploadPath/image/{uuid}.ext</p>
     *
     * @return 新文件名（不含目录），未上传则返回 null
     */
    public String saveThing(Thing thing) throws IOException {
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if (!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        return newFileName;
    }

    /**
     * 从 location 字段自动提取城市名称到 city 字段。
     * 
     * <p>支持的格式：</p>
     * <ul>
     *   <li>"北京市海淀区" -> "北京"</li>
     *   <li>"上海市浦东新区" -> "上海"</li>
     *   <li>"海淀区" -> 保持原样（如果没有"市"字）</li>
     * </ul>
     */
    private void extractCityFromLocation(Thing thing) {
        if (StringUtils.isEmpty(thing.location)) {
            logger.info("location 为空，跳过城市提取");
            return;
        }
        
        String location = thing.location.trim();
        logger.info("开始从 location='{}' 提取城市", location);
        
        // 如果已经有 city 字段且不为空，不覆盖
        if (!StringUtils.isEmpty(thing.city)) {
            logger.info("city 字段已存在：'{}'，跳过提取", thing.city);
            return;
        }
        
        // 提取城市：查找"市"字，取之前的内容
        int cityIndex = location.indexOf("市");
        if (cityIndex > 0) {
            // 找到"市"字，提取城市名
            thing.city = location.substring(0, cityIndex);
            logger.info("通过'市'字提取到城市：'{}'", thing.city);
        } else {
            // 没有"市"字，尝试匹配常见直辖市/省会
            if (location.startsWith("北京")) {
                thing.city = "北京";
                logger.info("匹配到北京");
            } else if (location.startsWith("上海")) {
                thing.city = "上海";
                logger.info("匹配到上海");
            } else if (location.startsWith("天津")) {
                thing.city = "天津";
                logger.info("匹配到天津");
            } else if (location.startsWith("重庆")) {
                thing.city = "重庆";
                logger.info("匹配到重庆");
            } else {
                // 其他情况，保持 location 原样
                thing.city = location;
                logger.info("无法识别城市，使用原 location：'{}'", thing.city);
            }
        }
        
        logger.info("最终结果：location='{}' -> city='{}'", thing.location, thing.city);
    }
}

