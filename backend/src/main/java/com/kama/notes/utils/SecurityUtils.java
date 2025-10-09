package com.kama.notes.utils;

import com.kama.notes.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtils
 *
 * 安全相关工具类，封装从 Spring Security 上下文获取当前认证用户信息的常用方法。
 *
 * 说明：
 * - 这些方法依赖于 Spring Security 将已认证的 User 实例放到 Authentication.principal 中；
 * - 若未登录或 principal 不是预期类型，方法会返回 null，调用方应做空值判断。
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的 ID。
     *
     * 实现细节：
     * - 从 SecurityContextHolder 获取当前 Authentication；
     * - 若 authentication 存在且 principal 是 com.kama.notes.model.entity.User，则返回其 userId 字段；
     * - 否则返回 null（表示未登录或 principal 类型不匹配）。
     *
     * @return 当前用户 ID，未登录或无法获取时返回 null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getUserId();
        }
        return null;
    }

    /**
     * 获取当前登录的 User 实体。
     *
     * 说明：
     * - 直接返回 Authentication.principal 中的 User 对象，便于获取更多用户信息（用户名、角色等）；
     * - 若未登录或 principal 类型不匹配，返回 null。
     *
     * @return 当前登录的 User 实体，未登录时返回 null
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}