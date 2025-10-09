package com.kama.notes.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.dto.user.LoginRequest;
import com.kama.notes.model.dto.user.RegisterRequest;
import com.kama.notes.model.dto.user.UpdateUserRequest;
import com.kama.notes.model.dto.user.UserQueryParam;
import com.kama.notes.model.entity.User;
import com.kama.notes.model.vo.user.AvatarVO;
import com.kama.notes.model.vo.user.LoginUserVO;
import com.kama.notes.model.vo.user.RegisterVO;
import com.kama.notes.model.vo.user.UserVO;
import com.kama.notes.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    // 自动注入UserService以使用用户相关服务
    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     *
     * 说明：
     * - 校验入参后调用 service 完成注册逻辑（含密码加密、唯一性校验等）。
     * - 返回泛型 ApiResponse<RegisterVO>，包含注册成功后的关键信息（如用户 id 等）。
     *
     * @param request 包含注册所需字段的 DTO，带有 javax.validation 校验
     * @return ApiResponse 包含 RegisterVO（注册结果）
     */
    @PostMapping("/users")
    public ApiResponse<RegisterVO> register(
            @Valid
            @RequestBody
            RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * 用户登录接口
     *
     * 说明：
     * - 验证登录请求并调用 service 进行认证，通常返回用户信息和认证令牌（如 JWT）。
     *
     * @param request 登录请求体，包含账号/密码或邮箱/验证码等
     * @return ApiResponse 包含 LoginUserVO（登录用户信息与凭证）
     */
    @PostMapping("/users/login")
    public ApiResponse<LoginUserVO> login(
            @Valid
            @RequestBody
            LoginRequest request) {
        return userService.login(request);
    }

    /**
     * 自动登录 / 会话续期接口
     *
     * 说明：
     * - 在前端持有有效凭证时可调用以获取当前登录用户信息（通常用于页面刷新后的身份恢复）。
     *
     * @return ApiResponse 包含当前登录的 LoginUserVO
     */
    @PostMapping("/users/whoami")
    public ApiResponse<LoginUserVO> whoami() {
        return userService.whoami();
    }

    /**
     * 获取指定用户信息
     *
     * 说明：
     * - 根据路径中的 userId 查询用户详情，参数通过 @Pattern 做基本格式校验（数字字符串）。
     *
     * @param userId 要查询的用户 ID（数字字符串）
     * @return ApiResponse 包含 UserVO（用户详细信息）
     */
    @GetMapping("/users/{userId}")
    public ApiResponse<UserVO> getUserInfo(
            @PathVariable
            @Pattern(regexp = "\\d+", message = "ID 格式错误")
            Long userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 更新当前用户信息（部分更新）
     *
     * 说明：
     * - 接收 UpdateUserRequest，进行字段校验后委托 service 更新用户资料（如昵称、签名等）。
     * - 返回更新后的登录用户信息（LoginUserVO）。
     *
     * @param request 更新用户的 DTO，带有 @Valid 校验
     * @return ApiResponse 包含更新后的 LoginUserVO
     */
    @PatchMapping("/users/me")
    public ApiResponse<LoginUserVO> updateUserInfo(
            @Valid
            @RequestBody
            UpdateUserRequest request) {
        return userService.updateUserInfo(request);
    }

    /**
     * 上传用户头像
     *
     * 说明：
     * - 接收 MultipartFile，委托 service 处理文件存储并返回头像信息（如访问 URL）。
     * - 建议在 service 层做文件类型与大小校验、存储路径管理与 CDN/对象存储接入。
     *
     * @param file 头像文件（Multipart/form-data）
     * @return ApiResponse 包含 AvatarVO（头像元信息）
     */
    @PostMapping("/users/avatar")
    public ApiResponse<AvatarVO> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    /**
     * 管理端：获取用户列表
     *
     * 说明：
     * - 该接口用于后台管理页面，接收 UserQueryParam（分页/筛选）并返回用户实体列表。
     * - 请在安全层确保仅管理员可访问此接口（由拦截器或安全配置控制）。
     *
     * @param queryParam 查询参数，包含分页与筛选条件，使用 @Valid 校验
     * @return ApiResponse 包含用户实体列表
     */
    @GetMapping("/admin/users")
    public ApiResponse<List<User>> adminGetUser(
            @Valid UserQueryParam queryParam) {
        return userService.getUserList(queryParam);
    }
}
