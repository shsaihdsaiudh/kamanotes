package com.kama.notes.annotation;

import java.lang.annotation.*;

/**
 * 标注需要用户登录才能访问的方法。
 *
 * 用法：
 * - 在控制器或服务方法上添加 @NeedLogin，配合切面（NeedLoginAspect）或拦截器做登录校验。
 * - 若未登录或 userId 异常，切面/拦截器应返回统一的错误响应（例如：ApiResponseUtil.error(...)）。
 *
 * 设计说明：
 * - 仅作用于方法级别（@Target(ElementType.METHOD)），运行时保留以便通过反射或 AOP 获取（@Retention(RetentionPolicy.RUNTIME)）。
 * - 当前注解不携带任何属性；若后续需要更细粒度控制（如角色/权限），可在注解中添加属性扩展。
 *
 * 示例：
 * <pre>
 *     @NeedLogin
 *     public ApiResponse someProtectedEndpoint(...) { ... }
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedLogin {
}