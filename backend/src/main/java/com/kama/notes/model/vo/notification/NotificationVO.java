package com.kama.notes.model.vo.notification;

import lombok.Data;

/**
 * NotificationVO
 *
 * 通知视图对象（用于对外接口或消息推送时承载通知内容）。
 *
 * 说明：
 * - 该类为纯展示层 VO，仅包含前端或推送客户端需要的字段，不包含持久化或业务逻辑；
 * - content 字段为通知的文本内容，建议在构建通知时确保内容已被转义/脱敏以防 XSS 或暴露敏感信息；
 * - 若需要扩展（例如通知类型、创建时间、目标用户等），可在该类中增加相应字段并保持向后兼容。
 */
@Data
public class NotificationVO {
    /**
     * 通知内容（纯文本或已渲染的简单 HTML，按接口约定）
     */
    private String content;
}
