package com.kama.notes.model.enums.message;

/**
 * MessageType
 *
 * 消息类型常量定义，用于标识不同的消息/通知类别。
 *
 * 说明：
 * - 使用 Integer 常量以便与数据库字段或现有接口保持兼容；
 * - 若需更强的类型安全或便于序列化，建议替换为 enum。
 */
public class MessageType {
    /**
     * 点赞类型
     */
    public static final Integer LIKE = 1;

    /**
     * 评论类型
     */
    public static final Integer COMMENT = 2;

    /**
     * 系统通知类型
     */
    public static final Integer SYSTEM = 3;
}
