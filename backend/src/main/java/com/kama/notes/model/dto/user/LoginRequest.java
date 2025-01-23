package com.kama.notes.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    /*
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 6, max = 32, message = "账号长度必须在 6 到 32 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "账号只能包含字母、数字和下划线")
    private String account;

    /**
     * 登录密码
     * 必填，长度 6-32
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在 6 到 32 个字符之间")
    private String password;
}
