package com.kama.notes.task.statistic;

import com.kama.notes.mapper.StatisticMapper;
import com.kama.notes.mapper.NoteMapper;
import com.kama.notes.mapper.UserMapper;
import com.kama.notes.model.entity.Statistic;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * DailyStatistics
 *
 * 定时任务组件：每日汇总当天的用户与笔记相关统计并写入统计表（statistic）。
 *
 * 职责：
 * - 每天 23:59（cron = "0 59 23 * * ?"）触发一次，汇总当天产生的登录、注册、笔记提交等数据；
 * - 将汇总结果封装为 Statistic 实体并通过 StatisticMapper 插入数据库；
 * - 对插入操作做异常捕获并记录日志，避免定时任务抛出未捕获异常终止调度。
 *
 * 注意：
 * - 统计方法本身不控制事务边界（Mapper 层为直接数据库写入），若需要保证更复杂的事务一致性请在 Service 层封装并使用事务；
 * - 若数据量或统计逻辑复杂，建议改为异步批处理或分批查询以避免定时窗口阻塞。
 */
@Log4j2
@Component
public class DailyStatistics {

    @Autowired
    UserMapper userMapper;

    @Autowired
    NoteMapper noteMapper;

    @Autowired
    StatisticMapper statisticMapper;

    /**
     * dailyStatistics
     *
     * 定时执行的方法：统计当天的用户与笔记数据并持久化。
     *
     * 触发时间（cron）说明：
     * - "0 59 23 * * ?" 表示每天的 23:59:00 执行一次（基于服务器时区）。
     *
     * 执行步骤：
     * 1. 从 userMapper / noteMapper 中读取当日及累计相关统计值；
     * 2. 封装到 Statistic 实体并设置统计日期为 LocalDate.now()；
     * 3. 使用 statisticMapper 插入统计表，成功/失败均记录日志。
     *
     * 异常处理：
     * - 捕获所有异常并记录错误日志，避免定时任务因异常中断或导致堆栈信息泄露。
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void dailyStatistics() {

        Statistic statistic = new Statistic();
        /**
         * 获取统计数据
         */
        // 用户相关统计
        int todayLoginCount = userMapper.getTodayLoginCount();
        int todayRegisterCount = userMapper.getTodayRegisterCount();
        int totalRegisterCount = userMapper.getTotalRegisterCount();

        // 笔记相关统计
        int todayNoteCount = noteMapper.getTodayNoteCount();
        int todaySubmitNoteUserCount = noteMapper.getTodaySubmitNoteUserCount();
        int totalNoteCount = noteMapper.getTotalNoteCount();

        /**
         * 设置统计数据到实体
         */
        statistic.setLoginCount(todayLoginCount);
        statistic.setRegisterCount(todayRegisterCount);
        statistic.setTotalRegisterCount(totalRegisterCount);

        statistic.setNoteCount(todayNoteCount);
        statistic.setSubmitNoteCount(todaySubmitNoteUserCount);
        statistic.setTotalNoteCount(totalNoteCount);

        statistic.setDate(LocalDate.now());

        try {
            statisticMapper.insert(statistic);
            log.info("[定时任务] 统计每日数据，插入数据成功，statistic={}", statistic);
        } catch (Exception e) {
            // 记录异常信息，不抛出以免影响后续调度
            log.error("[定时任务] 统计每日数据，插入数据失败，statistic={}, 错误详情={}", statistic, e.getMessage());
            log.debug("插入统计异常堆栈：", e);
        }
    }
}
