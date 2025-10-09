package com.kama.notes.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kama.notes.scope.RequestScopeData;
import com.kama.notes.utils.JwtUtil;

/**
 * TokenInterceptor
 *
 * 请求拦截器：在每次 HTTP 请求到达 Controller 之前解析请求头中的 token（通常位于 Authorization）。
 *
 * 主要职责：
 * - 读取 Authorization 请求头（支持 "Bearer <token>" 格式）；
 * - 使用 JwtUtil 验证 token 的有效性并从中提取 userId；
 * - 将解析出的 token、userId 与登录状态写入 RequestScopeData，供后续业务逻辑/切面使用；
 * - 当 token 缺失或无效时，设置 requestScopeData 为未登录状态（不直接拒绝请求，统一由上层处理策略决定）。
 *
 * 说明与建议：
 * - 本拦截器不负责响应拒绝（如返回 401），只负责解析与初始化请求作用域数据；如需在拦截层拒绝请求，可在此处返回 false 并写入响应；
 * - 为避免重复解析开销，可考虑在 JwtUtil 层增加缓存或短期内复用解析结果；
 * - 请确保 RequestScopeData 在请求结束时被清理（例如 TraceIdFilter / 其它清理机制），以防线程复用导致数据泄露。
 */
@Component
public class TokenInterceptor implements HandlerInterceptor
{
    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * preHandle：请求预处理。
     *
     * 行为：
     * - 从请求头读取 Authorization；
     * - 若无 token：将 requestScopeData 标记为未登录并继续请求处理；
     * - 若有 token：移除 "Bearer " 前缀后验证 token；
     *   - 验证通过：从 token 中提取 userId，设置 requestScopeData 的 userId、token 与登录状态；
     *   - 验证失败：将 requestScopeData 标记为未登录（不抛出异常或直接拒绝，便于上层策略处理）。
     *
     * 返回值：
     * - 返回 true 以继续后续处理；如需阻断请求可返回 false 并设置响应状态/消息。
     *
     * 注意：
     * - 不要在此处将敏感错误信息直接暴露给客户端，日志中记录足够排查即可；
     * - 若系统使用分布式鉴权（如远程黑名单、撤销等），可在此处或 JwtUtil 中扩展校验逻辑。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从请求头读取 token（可能为 "Bearer <token>"）
        String token = request.getHeader("Authorization");

        // 无 token：标记未登录并继续处理
        if (token == null) {
            requestScopeData.setLogin(false);
            requestScopeData.setToken(null);
            requestScopeData.setUserId(null);
            return true;
        }

        token = token.replace("Bearer ", "");

        // 验证 token 并在通过时设置请求范围数据
        if (jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            requestScopeData.setUserId(userId);
            requestScopeData.setToken(token);
            requestScopeData.setLogin(true);
        } else {
            // token 无效：保持或设置为未登录状态
            requestScopeData.setLogin(false);
            requestScopeData.setToken(null);
            requestScopeData.setUserId(null);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
