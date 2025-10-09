package com.kama.notes.model.vo.user;

import lombok.Data;

/**
 * AvatarVO
 *
 * 用户头像视图对象（用于接口返回头像信息）。
 *
 * 说明：
 * - 仅包含前端需要的头像字段，不包含持久化或业务逻辑；
 * - url 通常为可访问的绝对地址或 CDN 路径，前端可直接使用该字段展示头像。
 */
@Data
public class AvatarVO {
    /**
     * 头像的可访问 URL（例如 CDN 或静态资源地址）
     */
    private String url;
}
