package com.kama.notes.model.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TokenApiResponse
 *
 * 带有 token 的统一 API 响应封装，继承自 ApiResponse。
 *
 * 用途：
 * - 在登录、刷新令牌等需要返回 token 的接口中使用；
 * - 保持与 ApiResponse 相同的结构（code/message/data），并额外携带 token 字段。
 *
 * 设计说明：
 * - 使用 Lombok 自动生成 getter/setter/toString/equals/hashCode 等方法；
 * - token 字段为 final，表示响应创建后 token 不可变（通过构造函数注入）。
 *
 * @param <T> 响应数据类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenApiResponse<T> extends ApiResponse<T> {
    /**
     * 响应中携带的 token（例如 JWT），用于客户端后续请求的身份认证/授权。
     */
    private final String token;

    /**
     * 构造函数
     *
     * @param code  响应码（例如 200 表示成功）
     * @param msg   响应消息文本
     * @param data  响应数据（泛型）
     * @param token 返回给客户端的 token 字符串
     */
    public TokenApiResponse(Integer code, String msg, T data, String token) {
        super(code, msg, data);
        this.token = token;
    }
}
