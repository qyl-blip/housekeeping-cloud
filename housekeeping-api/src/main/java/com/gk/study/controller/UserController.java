package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
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
 * 用户模块接口。
 *
 * <p>主要提供：</p>
 * <ul>
 *   <li>用户/管理员登录</li>
 *   <li>用户注册</li>
 *   <li>用户管理（列表、详情、创建、删除、更新）</li>
 *   <li>普通用户修改个人信息、修改密码</li>
 * </ul>
 *
 * <p>说明：当前项目为了演示方便，密码与 token 均采用 MD5 + 固定 salt 的方式生成，
 * 并非生产级安全方案。</p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 盐值：与前端约定，用于对密码/token 做简单混淆。
     *
     * <p>注意：固定盐 + MD5 不适合生产环境，这里更多是演示用。</p>
     */
    private final String salt = "abcd1234";

    @Autowired
    UserService userService;

    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 用户列表查询（支持分页）。
     *
     * @param keyword  关键词（是否真正参与筛选以 Service 实现为准）
     * @param page     页码（可选；为空则不分页）
     * @param pageSize 每页条数（可选；为空则不分页）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, Integer page, Integer pageSize) {
        List<User> list = userService.getUserList(keyword);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 用户详情。
     *
     * @param userId 用户ID
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId) {
        User user = userService.getUserDetail(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", user);
    }

    /**
     * 管理端登录。
     *
     * <p>流程：</p>
     * <ol>
     *   <li>校验用户名/密码不为空</li>
     *   <li>对密码做 MD5(password + salt)</li>
     *   <li>按“管理员角色（role > 1）”查询用户</li>
     * </ol>
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user) {
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser = userService.getAdminUser(user);
        if (responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "登录成功", responseUser);
        }
        return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
    }

    /**
     * 普通用户登录。
     *
     * <p>校验规则同管理端登录，但要求 role = 1。</p>
     */
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user) {
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser = userService.getNormalUser(user);
        if (responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "登录成功", responseUser);
        }
        return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
    }

    /**
     * 普通用户注册。
     *
     * <p>流程：</p>
     * <ol>
     *   <li>校验用户名/密码/确认密码</li>
     *   <li>校验用户名唯一</li>
     *   <li>密码做 MD5(password + salt)</li>
     *   <li>生成 token：MD5(username + salt)</li>
     *   <li>设置 role=普通用户、status=0、createTime</li>
     * </ol>
     */
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) {
        try {
            if (user == null
                    || !StringUtils.hasText(user.getUsername())
                    || !StringUtils.hasText(user.getPassword())
                    || !StringUtils.hasText(user.getRePassword())) {
                return new APIResponse(ResponeCode.FAIL, "参数错误");
            }
            if (userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名已存在");
            }
            if (!user.getPassword().equals(user.getRePassword())) {
                return new APIResponse(ResponeCode.FAIL, "两次密码不一致");
            }
            String md5Pwd = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            user.setPassword(md5Pwd);
            String token = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            user.setToken(token);
            user.setRole(String.valueOf(User.NormalUser));
            user.setStatus("0");
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "注册成功");
        } catch (Exception e) {
            logger.error("用户注册失败", e);
            return new APIResponse(ResponeCode.FAIL, "注册失败: " + e.getMessage());
        }
    }

    /**
     * 管理端创建用户。
     *
     * <p>支持上传头像文件：会保存到 uploadPath/avatar 目录，并把文件名写入 user.avatar。</p>
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException {
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        if (userService.getUserByUserName(user.getUsername()) != null) {
            return new APIResponse(ResponeCode.FAIL, "用户名已存在");
        }
        String md5Pwd = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
        user.setPassword(md5Pwd);
        String token = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
        user.setToken(token);
        user.setCreateTime(String.valueOf(System.currentTimeMillis()));

        String avatar = saveAvatar(user);
        if (StringUtils.hasText(avatar)) {
            user.avatar = avatar;
        }
        userService.createUser(user);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 管理端删除用户（支持逗号分隔的批量删除）。
     *
     * @param ids 用户ID列表，英文逗号分隔
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        if (!StringUtils.hasText(ids)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        String[] arr = ids.split(",");
        for (String id : arr) {
            if (StringUtils.hasText(id)) {
                userService.deleteUser(id.trim());
            }
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 管理端更新用户。
     *
     * <p>如携带头像文件，会同步保存并更新 user.avatar。</p>
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        if (user == null || !StringUtils.hasText(user.getId())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        String avatar = saveAvatar(user);
        if (StringUtils.hasText(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 普通用户更新个人信息。
     *
     * <p>限制：</p>
     * <ul>
     *   <li>只能更新普通用户（role=1）的信息</li>
     *   <li>不允许通过该接口修改密码/角色</li>
     * </ul>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        if (user == null || !StringUtils.hasText(user.getId())) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        User tmpUser = userService.getUserDetail(user.getId());
        if (tmpUser == null || !String.valueOf(User.NormalUser).equals(tmpUser.getRole())) {
            return new APIResponse(ResponeCode.FAIL, "无权限");
        }
        user.setPassword(null);
        user.setRole(String.valueOf(User.NormalUser));
        String avatar = saveAvatar(user);
        if (StringUtils.hasText(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 普通用户修改密码。
     *
     * <p>流程：</p>
     * <ol>
     *   <li>校验当前用户是否为普通用户</li>
     *   <li>校验旧密码（MD5 + salt）</li>
     *   <li>保存新密码（MD5 + salt）</li>
     * </ol>
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(String userId, String password, String newPassword) throws IOException {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(password) || !StringUtils.hasText(newPassword)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        User user = userService.getUserDetail(userId);
        if (user == null || !String.valueOf(User.NormalUser).equals(user.getRole())) {
            return new APIResponse(ResponeCode.FAIL, "无权限");
        }
        String md5Pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if (!md5Pwd.equals(user.getPassword())) {
            return new APIResponse(ResponeCode.FAIL, "旧密码错误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex((newPassword + salt).getBytes()));
        userService.updateUser(user);
        return new APIResponse(ResponeCode.SUCCESS, "修改成功");
    }

    /**
     * 保存头像文件（如果前端携带了 avatarFile）。
     *
     * <p>文件落盘路径：uploadPath/avatar/{uuid}.ext</p>
     *
     * @return 新文件名（不含目录），未上传则返回 null
     */
    private String saveAvatar(User user) throws IOException {
        MultipartFile file = user.getAvatarFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            if (!StringUtils.hasText(oldFileName)) {
                return null;
            }
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "avatar" + File.separator + newFileName;
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if (StringUtils.hasText(newFileName)) {
            user.avatar = newFileName;
        }
        return newFileName;
    }
}
