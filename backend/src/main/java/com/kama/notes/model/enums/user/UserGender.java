package com.kama.notes.model.enums.user;

/**
 * UserGender
 *
 * 用户性别常量定义。
 *
 * 说明：
 * - 使用 Integer 常量便于与数据库字段或现有接口兼容；
 * - MALE / FEMALE / SECRET 分别表示男性、女性和保密（未知）；
 * - 若需更强类型安全或扩展方法，建议改用 enum。
 */
public class UserGender {
    /**
     * 男性
     */
    public static final Integer MALE = 1;

    /**
     * 女性
     */
    public static final Integer FEMALE = 2;

    /**
     * 保密 / 未知
     */
    public static final Integer SECRET = 3;
}
