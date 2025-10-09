package com.kama.notes.model.enums.message;

/**
 * MessageTargetType
 *
 * 消息目标类型常量定义，用于标识消息所关联的目标对象类型（例如通知关联的是笔记或评论）。
 *
 * 说明：
 * - 使用 Integer 常量以便与数据库字段或其他接口约定保持兼容；
 * - 若需要更强的类型安全或便于序列化/反序列化，可考虑替换为 enum。
 */
public class MessageTargetType {
    /**
     * 目标类型：笔记
     */
    public static final Integer NOTE = 1;

    /**
     * 目标类型：评论
     */
    public static final Integer COMMENT = 2;
}
