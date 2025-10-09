package com.kama.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * SchedulerConfig
 *
 * 定时任务调度配置类。
 *
 * 说明：
 * - @EnableScheduling：启用 Spring 的定时任务支持（@Scheduled 注解生效）。
 * - 提供一个 ThreadPoolTaskScheduler Bean，替代默认单线程调度器，避免定时任务阻塞。
 *
 * 配置要点：
 * - poolSize：线程池大小，决定并发执行定时任务的能力。当前设置为 10，视业务复杂度调整。
 * - threadNamePrefix：调度线程名的前缀，便于在日志中定位定时任务的线程。
 *
 * 建议：
 * - 根据任务执行时间与并发量调整 poolSize，避免线程过多或过少。
 * - 若任务可能被取消，考虑调用 scheduler.setRemoveOnCancelPolicy(true) 来清理已取消任务的引用。
 * - 在应用关闭时，Spring 会自动销毁该 Bean 并关闭线程池；如需自定义关闭行为，可在此 Bean 上设置相应属性。
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    /**
     * 创建并配置 ThreadPoolTaskScheduler。
     *
     * @return ThreadPoolTaskScheduler 实例，用于执行 @Scheduled 标注的任务
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 线程池大小：控制同时并发执行定时任务的线程数
        scheduler.setPoolSize(10);
        // 线程名前缀：便于在日志或线程剖析中识别调度线程
        scheduler.setThreadNamePrefix("ScheduledTask-");
        return scheduler;
    }
}