package com.kama.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * NotesApplication
 *
 * 应用启动类（Spring Boot）。
 *
 * 说明：
 * - 标注 @SpringBootApplication：开启 Spring Boot 自动配置、组件扫描等功能；
 * - 标注 @EnableScheduling：启用 Spring 的定时任务调度功能（项目中若存在 @Scheduled 方法则会被激活）；
 * - main 方法用于启动 Spring 应用上下文并启动内嵌服务器（如 Tomcat/Netty，取决于依赖）。
 *
 * 使用建议：
 * - 若需在启动时执行初始化逻辑，可在此类或其它被 Spring 管理的 Bean 中添加 CommandLineRunner/ ApplicationRunner；
 * - 生产环境请通过配置文件（application.yml/properties）和外部化配置管理运行参数与数据源等信息。
 *
 * 原作者信息保留在类注释中（不影响功能）。
 *
 * @author Tong
 * @since 2024-12-16
 */
@SpringBootApplication
@EnableScheduling
public class NotesApplication {
    /**
     * 应用入口点。
     *
     * @param args 启动参数（可包含 Spring 配置如 --spring.profiles.active=prod 等）
     */
    public static void main(String[] args) {
        SpringApplication.run(NotesApplication.class, args);
    }
}
