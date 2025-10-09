package com.kama.notes.controller;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.service.EmailService;
import com.kama.notes.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * EmailController
 *
 * 提供与邮箱相关的简单接口，例如发送邮箱验证码。
 *
 * 说明：
 * - GET /api/email/verify-code: 向指定邮箱发送验证码，接口对外仅返回统一的 ApiResponse，内部异常以错误信息返回。
 * - 入参使用 javax.validation 注解进行基本校验（@NotBlank, @Email）。
 *
 * 注意事项与建议：
 * - 该控制器当前没有做频率限制或防刷处理。生产环境建议在 Service 层或网关/过滤器层加入限流（如基于 IP/邮箱的短时间内发送次数限制）。
 * - sendVerificationCode 可能抛出异常，当前做了通用捕获并返回错误消息；如需区分错误类型（如发送失败/参数错误），可在 ApiResponse 中返回更具体的错误码。
 * - 若使用异步发送邮件，可将邮件发送改为异步调用并立即返回成功，邮件发送结果通过回调/通知或后台任务处理。
 */
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * 发送邮箱验证码。
     *
     * 行为：
     * - 校验 email 为非空且格式合法（@NotBlank、@Email）。
     * - 调用 EmailService 发送验证码，成功返回 ApiResponse.success(null)，失败返回 ApiResponse.error(...)。
     *
     * 参数：
     * @param email 接收验证码的邮箱地址，必须为有效的邮箱格式。
     *
     * 返回值：
     * - 成功：ApiResponse<Void>（data 为 null）
     * - 失败：ApiResponse 包含错误信息（message 字段）
     *
     * 建议：
     * - 可在 Service 层加入发送频率限制、验证码存储与过期策略、以及将发送行为异步化以提升吞吐与用户体验。
     */
    @GetMapping("/verify-code")
    public ApiResponse<Void> sendVerifyCode(@RequestParam @NotBlank @Email String email) {
        try {
            emailService.sendVerificationCode(email);
            return ApiResponseUtil.success(null);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }
}