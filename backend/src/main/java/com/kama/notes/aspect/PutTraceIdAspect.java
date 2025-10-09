package com.kama.notes.aspect;

import java.util.UUID;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * PutTraceIdAspect：为进入 com.kama.notes 包及其子包的方法在 MDC 中注入 traceId，
 * 以便在日志中关联同一次请求或处理流。
 *
 * 说明：
 * - 使用 MDC(traceId) 可以让后续日志自动携带该 traceId，便于链路追踪和日志聚合。
 * - 当前实现仅在 MDC 中不存在 traceId 时生成并设置，避免覆盖已有 traceId（例如来自上游传递的值）。
 * - 注意：如果需要在请求处理完成后清理 MDC，应在合适的位置（如过滤器或切面的 finally 块）移除 traceId。
 */
@Aspect
@Component
public class PutTraceIdAspect {
    // MDC 中保存 traceId 的键名
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 切面切入点，拦截 com.kama.notes 包及其子包下的所有方法调用。
     * 在方法执行前检查 MDC 是否包含 traceId，若无则生成一个 UUID 并放入 MDC。
     *
     * 注意：该切面只负责在 MDC 中放入 traceId，不负责清理。若希望避免线程复用带来的 trace 泄露，
     * 请在请求结束处（如过滤器）显式调用 MDC.remove(TRACE_ID_KEY)。
     */
    @Before("execution(* com.kama.notes..*(..))")
    public void addTraceIdToLog() {
        // 如果当前 MDC 中没有 traceId，则生成一个新的
        if (MDC.get(TRACE_ID_KEY) == null) {
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
        }
    }
}
