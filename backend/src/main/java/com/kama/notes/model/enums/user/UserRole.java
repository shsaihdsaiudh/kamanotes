package com.kama.notes.model.enums.user;

/**
 * UserRole
 *
 * 用户角色常量定义。
 *
 * 说明：
 * - NOT_ADMIN = 0 表示普通用户；IS_ADMIN = 1 表示管理员用户；
 * - 使用 Integer 常量便于与数据库整型字段或历史接口兼容；
 * - 若需要更强的类型安全、可读性或扩展方法（例如权限判断），建议替换为 enum 类型。
 */
public class UserRole {
    /**
     * 普通用户 / 非管理员
     */
    public static final Integer NOT_ADMIN = 0;

    /**
     * 管理员
     */
    public static final Integer IS_ADMIN = 1;
}
