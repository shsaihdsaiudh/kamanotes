package com.kama.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * SecurityConfig
 *
 * 应用的 Spring Security 配置类。
 *
 * 职责：
 * - 定义全局安全策略（如禁用 CSRF、开启/关闭默认登录方式等）；
 * - 提供 CORS 配置以支持前端跨域请求；
 * - 暴露 PasswordEncoder 用于密码加密/校验（使用 BCrypt）。
 *
 * 说明：
 * - 当前将 /api/** 路径全部放行（permitAll），适用于开发或当在其它层进行鉴权的场景；
 *   生产环境请根据业务需求收紧访问控制策略。
 * - 禁用 CSRF 适合基于 token 的无状态认证（如 JWT），若使用 cookie/session 登录请谨慎处理 CSRF。
 *
 * 作者：Tong
 * 最后修改：2024-12-17
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 SecurityFilterChain。
     *
     * 行为要点：
     * - 启用 CORS 并禁用 CSRF；
     * - 对 /api/** 允许所有访问，其它请求需认证；
     * - 禁用内置表单登录和 HTTP Basic（通常在使用 JWT 时禁用默认认证方式）。
     *
     * 提示：
     * - 若要集成 JWT 或自定义认证过滤器，请在此方法中添加相应的滤器配置（http.addFilterBefore(...)）。
     *
     * @param http HttpSecurity 构建器
     * @return 已构建的 SecurityFilterChain
     * @throws Exception 配置过程可能抛出的异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable()
            .httpBasic().disable();
        
        return http.build();
    }

    /**
     * 提供 CORS 配置源。
     *
     * 当前配置：
     * - 允许来源： http://localhost:5173 （开发环境前端地址）；
     * - 允许方法： GET, POST, PUT, DELETE, PATCH, OPTIONS；
     * - 允许所有请求头，并允许携带凭证（cookies/Authorization）。
     *
     * 注意事项：
     * - 部署到生产环境时请将 allowed origins 限制为实际前端域名，避免安全风险。
     *
     * @return CorsConfigurationSource 实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // 允许的前端域名
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 提供 PasswordEncoder Bean，使用 BCrypt 实现。
     *
     * 说明：
     * - BCryptPasswordEncoder 是推荐的单向散列算法，适用于存储用户密码；
     * - 可在用户注册/登录处使用该 Bean 进行密码加密与匹配。
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
