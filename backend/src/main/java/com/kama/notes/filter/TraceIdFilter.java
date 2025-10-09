package com.kama.notes.filter;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * TraceIdFilter
 *
 * Servlet 过滤器：负责在请求处理完成后清理 MDC 中的 traceId，避免线程池复用时残留导致的 trace 泄露。
 *
 * 说明：
 * - 本过滤器并不负责生成或写入 traceId（该工作可在上游网关、切面或另一个过滤器中完成）；
 * - 在 doFilter 的 finally 块中移除 MDC 键，确保无论请求成功或抛出异常都会执行清理；
 * - 若项目中需要在此处兼顾 traceId 的生成/传递，可将生成逻辑放在 doFilter 的 try 前并在 finally 中移除。
 *
 * 使用建议：
 * - 将该过滤器注册为最外层的过滤器之一，保证在请求链结束时清理 MDC；
 * - 遇到异步或线程切换场景（如使用异步任务池），需确保在新线程中也正确设置与清理 MDC。
 */
public class TraceIdFilter implements Filter {

    // MDC 中用于存放 traceId 的键名，需与日志框架中的 pattern 保持一致
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 过滤方法：
     * - 仅在 finally 中进行 MDC.remove(TRACE_ID_KEY) 清理，保证即使发生异常也能清理上下文；
     * - 当前实现不覆盖或设置 traceId，避免覆盖上游传递的值。
     *
     * @param request  ServletRequest
     * @param response ServletResponse
     * @param chain    FilterChain
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            // 清理 MDC 中的 traceId，防止线程复用导致 trace 泄露
            MDC.remove(TRACE_ID_KEY);
        }
    }

    /**
     * 初始化方法（当前无特殊初始化逻辑）。
     *
     * @param filterConfig FilterConfig
     * @throws ServletException Servlet 异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * 销毁方法（当前无特殊销毁逻辑）。
     */
    @Override
    public void destroy() {}
}
