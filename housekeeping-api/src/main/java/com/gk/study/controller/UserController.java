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

@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final String salt = "abcd1234";

    @Autowired
    UserService userService;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, Integer page, Integer pageSize) {
        List<User> list = userService.getUserList(keyword);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId) {
        User user = userService.getUserDetail(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", user);
    }

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
            user.setStatus("0"); // 设置状态为正常
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            // 暂时注释掉头像处理，避免文件上传问题
            // String avatar = saveAvatar(user);
            // if (StringUtils.hasText(avatar)) {
            //     user.avatar = avatar;
            // }
            
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "注册成功");
        } catch (Exception e) {
            logger.error("用户注册失败", e);
            return new APIResponse(ResponeCode.FAIL, "注册失败: " + e.getMessage());
        }
    }

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