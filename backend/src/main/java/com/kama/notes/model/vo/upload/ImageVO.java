package com.kama.notes.model.vo.upload;

import lombok.Data;

/**
 * ImageVO
 *
 * 图片视图对象（用于上传/返回图片信息）。
 *
 * 说明：
 * - 该类为展示层 VO，仅包含前端需要的图片信息，不包含持久化或业务逻辑；
 * - url 通常为可访问的绝对地址或 CDN 路径，前端可直接使用该字段展示图片。
 */
@Data
public class ImageVO {
    /**
     * 图片的可访问 URL（例如 CDN 或静态资源地址）
     */
    private String url;
}
