package com.kama.notes.model.vo.user;

import lombok.Data;

import java.time.LocalDate;

/**
 * LoginUserVO
 *
 * 当前登录用户的视图对象（VO），用于在登录/会话上下文中承载当前用户的基础信息。
 *
 * 说明：
 * - 用于后端返回当前用户信息或在会话中存储用户简要信息；
 * - 该类为展示层/传输层对象，不包含业务逻辑或持久化注解；
 * - 字段可能为 null，调用方应进行空值校验；涉及隐私的信息（如 email）在对外返回时需注意脱敏/授权。
 */
@Data
public class LoginUserVO {

    /**
     * 用户 ID（主键）
     */
    private Long userId;

    /**
     * 用户账号（登录账号）
     */
    private String account;

    /**
     * 用户昵称 / 显示名
     */
    private String username;

    /**
     * 用户性别（由业务约定的数值，如 0=未知，1=男，2=女 等）
     */
    private Integer gender;

    /**
     * 用户生日（仅年月日）
     */
    private LocalDate birthday;

    /**
     * 用户头像 URL（可用于前端展示）
     */
    private String avatarUrl;

    /**
     * 用户邮箱（对外返回时请注意隐私与脱敏）
     */
    private String email;

    /**
     * 所属学校 / 教育机构
     */
    private String school;

    /**
     * 用户签名 / 简短个人介绍
     */
    private String signature;

    /**
     * 是否管理员（由业务约定的标识，通常 0/1 或 null 表示非管理员）
     */
    private Integer isAdmin;
}
