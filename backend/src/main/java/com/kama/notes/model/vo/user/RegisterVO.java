package com.kama.notes.model.vo.user;

import lombok.Data;

/**
 * RegisterVO
 *
 * 注册接口返回的视图对象（用于在用户注册成功后向客户端返回必要的信息）。
 *
 * 说明：
 * - 仅包含客户端在注册成功后需要的最小字段（如 userId）；
 * - 作为响应 VO，不包含持久化或业务逻辑；对外返回时注意不要泄露敏感信息。
 */
@Data
public class RegisterVO {
    /**
     * 新注册用户的 ID（用于客户端确认注册结果或后续跳转）
     */
    private Long userId;
}
