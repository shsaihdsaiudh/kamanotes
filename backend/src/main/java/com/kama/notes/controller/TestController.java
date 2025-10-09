package com.kama.notes.controller;

import com.kama.notes.scope.RequestScopeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * 用于提供简单的测试接口，便于本地或开发环境验证请求作用域数据和异常处理。
 *
 * 功能：
 * - GET /api/hello：读取 RequestScopeData 中的用户信息并返回简单文本响应，便于验证拦截器/过滤器
 *   是否已正确设置请求范围数据（如 token、userId 等）。
 * - GET /api/exception：抛出运行时异常，用于测试全局异常处理器或日志上报链路。
 *
 * 注意：
 * - 生产环境请谨慎暴露此类测试接口，或在部署配置中移除/保护（例如仅在 dev profile 下启用）。
 */
@RestController
@RequestMapping("/api")
public class TestController {

    /**
     * 注入请求作用域数据。RequestScopeData 应在请求开始时由拦截器或过滤器初始化，
     * 并保证在请求结束时清理以避免线程复用导致的数据泄露。
     */
    @Autowired
    private RequestScopeData requestScopeData;

    /**
     * 简单测试接口：输出并返回欢迎文本。
     *
     * 行为说明：
     * - 打印用于调试的 requestScopeData 信息（System.out 仅用于开发测试，生产环境建议使用日志框架）；
     * - 返回固定字符串 "Hello World!"。
     *
     * 用途：
     * - 验证拦截器/Filter 是否将 token、userId 等正确写入 RequestScopeData；
     * - 快速检查服务是否可达。
     *
     * @return 简单的字符串响应
     */
    @GetMapping("/hello")
    public String hello() {

        System.out.println("get data in /test/hello");
        System.out.println(requestScopeData.getUserId());
        System.out.println(requestScopeData.getToken());
        return "Hello World!";
    }

    /**
     * 测试异常抛出接口：直接抛出 RuntimeException。
     *
     * 用途：
     * - 用于验证全局异常处理（ControllerAdvice）、错误返回格式以及日志上报是否生效。
     * - 在压测/自动化测试中可触发错误路径以检查告警链路。
     *
     * 注意：调用此接口会导致请求返回 500 错误，请仅在测试场景调用。
     *
     * @throws RuntimeException 用于触发全局异常处理
     */
    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException("test exception");
    }
}
