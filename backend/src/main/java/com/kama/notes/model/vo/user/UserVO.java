package com.kama.notes.model.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserVO
 *
 * 用于在当前登录用户查看他人信息时返回的用户视图对象（VO）。
 *
 * 说明：
 * - 该类为展示层对象，仅包含前端需要的用户公开信息，不包含持久化或业务逻辑；
 * - 返回给客户端的字段应注意隐私与脱敏，例如邮箱在有必要时可只返回部分或不返回；
 * - 字段可能为 null（例如未设置），调用方应做好空值判断。
 */
@Data
public class UserVO {
    /**
     * 用户昵称 / 显示名
     */
    private String username;

    /**
     * 用户性别（由业务约定的数值，例如 0=未知，1=男，2=女）
     */
    private Integer gender;

    /**
     * 头像的可访问 URL（例如 CDN 地址）
     */
    private String avatarUrl;

    /**
     * 用户邮箱（对外返回时注意脱敏或按权限控制）
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
     * 最后登录时间（可用于显示用户活跃度）
     */
    private LocalDateTime lastLoginAt;
}
