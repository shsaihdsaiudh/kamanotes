package com.kama.notes.model.enums.user;

/**
 * UserBanned
 *
 * 用户封禁状态常量定义。
 *
 * 说明：
 * - NOT_BANNED = 0 表示用户未被封禁；
 * - IS_BANNED = 1 表示用户已被封禁；
 * - 使用 Integer 常量便于与数据库字段（整型）或现有接口保持兼容；
 * - 若需更强的类型安全与可读性，建议改为 enum。
 */
public class UserBanned {
    /**
     * 未封禁
     */
    public static final Integer NOT_BANNED = 0;

    /**
     * 已封禁
     */
    public static final Integer IS_BANNED = 1;
}
