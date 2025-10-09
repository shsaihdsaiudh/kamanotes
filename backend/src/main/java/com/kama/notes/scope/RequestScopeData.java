package com.kama.notes.scope;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * RequestScopeData
 *
 * 请求作用域数据容器：用于在同一次 HTTP 请求处理周期内保存共享的上下文信息。
 *
 * 特性与使用说明：
 * - 标注 @RequestScope：每次 HTTP 请求都会创建该 Bean 的独立实例，线程安全（不在多个请求间复用）；
 * - 适合保存与当前请求相关的临时数据（如解析后的 token、当前用户 id、登录状态等）；
 * - 不应在该类中放置长期状态或跨请求共享的数据；跨请求数据应放在单例 Bean 或外部存储（Redis/DB）；
 * - 若在拦截器/过滤器中设置该 Bean 的字段，确保在请求结束后自动销毁（Spring 会在请求结束时回收请求作用域 Bean）。
 *
 * 常见用途：
 * - TokenInterceptor 在请求进入时解析 token 并将 token/userId/isLogin 写入此处，业务层/Service 可直接读取而无需重复解析；
 * - 用于在日志或审计中关联当前请求的用户信息。
 */
@Component
@RequestScope
@Data
public class RequestScopeData {
    /**
     * 原始或已解析的认证令牌（例如 JWT），可供后续业务或日志使用。
     */
    private String token;

    /**
     * 解析得到的当前请求用户 ID（若未登录则为 null）。
     */
    private Long userId;

    /**
     * 当前请求是否已通过认证。
     */
    private boolean isLogin;

    // private boolean isAdmin;
}
