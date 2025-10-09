package com.kama.notes.config;

import com.kama.notes.filter.TraceIdFilter;
import com.kama.notes.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 *
 * Web MVC 配置类：
 * - 将上传目录映射为静态资源（/images/** -> 本地 uploadPath）；
 * - 注册 TokenInterceptor，用于在请求开始时解析 token 并初始化请求作用域用户信息；
 * - 配置全局 CORS，允许前端跨域访问并携带凭证（开发环境为 http://localhost:5173）；
 * - 注册 TraceIdFilter，为每个请求注入 traceId 以便日志追踪。
 *
 * 注意：
 * - uploadPath 可通过 application.properties/application.yml 的 upload.path 覆盖（默认 D:/kamaNotes/upload）。
 * - TokenInterceptor 的具体行为（认证/鉴权/初始化数据）应保证线程/请求作用域安全。
 * - TraceIdFilter 在请求结束处应清理 MDC（如果 TraceIdFilter 未处理清理，请在过滤器中补充）。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 上传文件存放路径，默认 D:/kamaNotes/upload，可通过配置覆盖。
     */
    @Value("${upload.path:D:/kamaNotes/upload}")
    private String uploadPath;

    /**
     * TokenInterceptor：解析 token 并在请求范围内设置用户上下文。
     */
    @Autowired
    private TokenInterceptor tokenInterceptor;

    /**
     * 将 /images/** 映射到本地文件系统的 uploadPath 目录，支持通过 URL 直接访问上传的图片文件。
     *
     * 示例：请求 /images/avatar.png -> 读取文件 {uploadPath}/avatar.png
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

    /**
     * 添加拦截器：
     * - tokenInterceptor 拦截所有请求以进行 token 校验/用户信息初始化；
     * - 排除登录与错误路径（/login, /error）。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/error");
    }

    /**
     * 全局 CORS 配置：
     * - 允许开发环境前端域名访问（可按需调整为生产域名）；
     * - 允许常用 HTTP 方法，允许所有请求头，并支持携带凭证（Cookie/Authorization）。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")  // 允许的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")// 允许的 HTTP 方法
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 注册 TraceIdFilter：将 TraceIdFilter 应用到所有 URL，便于在请求链中注入唯一 traceId 用于日志关联。
     * 提示：若 TraceIdFilter 在请求结束处未清理 MDC，需要在过滤器中补充 MDC.remove(...) 以防线程复用导致 trace 泄露。
     *
     * @return FilterRegistrationBean<TraceIdFilter>
     */
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}