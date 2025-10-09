package com.kama.notes.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kama.notes.annotation.NeedLogin;
import com.kama.notes.scope.RequestScopeData;
import com.kama.notes.utils.ApiResponseUtil;

/**
 * NeedLoginAspect
 *
 * 切面：拦截标注了 @NeedLogin 的方法，校验当前请求是否处于已登录状态。
 *
 * 主要职责：
 * - 在目标方法执行前检查 RequestScopeData 中的登录状态与 userId。
 * - 若未登录或 userId 异常，直接返回通用错误响应（通过 ApiResponseUtil）。
 * - 若校验通过，继续执行被拦截的方法并返回其结果。
 *
 * 说明与注意事项：
 * - 该切面使用环绕通知（@Around），能够在方法前进行拦截并决定是否继续执行。
 * - 返回值：当未通过校验时返回 ApiResponseUtil.error(...) 的结果，调用方应能正确处理此响应对象。
 * - 异常传播：若目标方法抛出异常，异常会按原样向上抛出（本切面未捕获并处理目标方法抛出的异常）。
 * - 并发与线程安全：本切面自身无可变状态，线程安全。但依赖的 RequestScopeData 必须以请求作用域或线程安全的方式提供。
 * - 日志与追踪：该切面不负责日志或 MDC 清理，若项目中使用 traceId 等上下文，请在请求边界（过滤器/拦截器）统一管理。
 */
@Aspect
@Component
public class NeedLoginAspect {

    @Autowired
    private RequestScopeData requestScopeData;

    /**
     * 环绕通知：拦截带有 @NeedLogin 注解的方法。
     *
     * 流程：
     * 1. 检查 requestScopeData.isLogin()，若为 false，则返回 ApiResponseUtil.error("用户未登录")。
     * 2. 检查 requestScopeData.getUserId()，若为 null，则返回 ApiResponseUtil.error("用户 ID 异常")。
     * 3. 两项校验通过后，调用 joinPoint.proceed() 执行原方法并返回其结果。
     *
     * 参数说明：
     * @param joinPoint 切点，表示被拦截的方法调用上下文。
     * @param needLogin 注解实例，可在需要时读取注解属性（当前未使用）。
     * @return 被拦截方法的返回值，或 ApiResponseUtil.error(...)（校验失败时）。
     * @throws Throwable 如果 joinPoint.proceed() 抛出异常，则向上抛出。
     *
     * 推荐：
     * - 确保 RequestScopeData 在请求范围内正确注入（例如使用 RequestScope 或通过拦截器设置）。
     * - 若希望对未登录情况返回不同的 HTTP 状态码或格式，可在 ApiResponseUtil 中统一处理或在控制器层处理该响应对象。
     */
    @Around("@annotation(needLogin)")
    public Object around(ProceedingJoinPoint joinPoint, NeedLogin needLogin) throws Throwable {

        // 判断用户是否已登录
        if (!requestScopeData.isLogin()) {
            return ApiResponseUtil.error("用户未登录");
        }

        // 判断用户 ID 是否异常
        if (requestScopeData.getUserId() == null) {
            return ApiResponseUtil.error("用户 ID 异常");
        }
        // 校验通过，继续执行原方法
        return joinPoint.proceed();
    }
}
