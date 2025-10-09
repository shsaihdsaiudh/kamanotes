package com.kama.notes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kama.notes.model.enums.redisKey.RedisKey;
import com.kama.notes.service.EmailService;
import com.kama.notes.task.email.EmailTask;
import com.kama.notes.utils.RandomCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * EmailServiceImpl
 *
 * 邮件相关服务实现（针对发送注册/验证邮件的场景）。
 *
 * 主要职责：
 * - 生成注册/验证用的随机验证码，并将发送任务以 JSON 的形式入队到 Redis 列表（作为异步任务队列）；
 * - 提供验证码校验、一次性验证码删除、以及短时发送限流检查。
 *
 * 设计要点与说明：
 * - 异步发送：本类不直接发送邮件，而是将 EmailTask 推入 Redis 队列，由独立消费者（EmailTaskConsumer）读取并调用 JavaMailSender 实际发送；
 * - 限流策略：使用 Redis 的限流键（RedisKey.registerVerificationLimitCode）防止短时间重复发送；
 * - 验证码存储：发送成功后，消费者会将验证码写入 Redis（RedisKey.registerVerificationCode），校验后会删除该键实现一次性使用；
 * - 可观测性：记录日志用于排查发送失败或队列异常；在生产环境建议对失败任务做落库或死信处理以便重试/告警。
 *
 * 安全与生产注意：
 * - 切勿在对外 API 响应中返回明文验证码（当前实现返回验证码用于测试，请在生产环境移除该行为）；
 * - 在高并发或大规模发送场景下建议接入专业邮件服务商（SMTP 池、第三方 API 或云邮件服务）以保证送达率与速率限制；
 * - 对 email 参数做必要校验与规范化，避免 Redis 键注入或键过长引发的问题。
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${mail.verify-code.limit-expire-seconds}")
    private int limitExpireSeconds;

    /**
     * sendVerificationCode
     *
     * 发送注册/验证邮件的入口方法（异步发送，返回当前生成的验证码字符串用于测试或日志）。
     *
     * 实现要点：
     * - 先检查发送频率限流（isVerificationCodeRateLimited）；
     * - 生成随机验证码（6 位数字），构建 EmailTask 并序列化为 JSON；
     * - 将任务 push 到 Redis 列表（作为简单的消息队列），由异步消费者负责真正的邮件发送；
     * - 在 Redis 中设置发送限流键，防止短时间内重复发送（过期时间由配置 mail.verify-code.limit-expire-seconds 决定）。
     *
     * 注意：
     * - 本方法在发送失败时抛出 RuntimeException，调用方应根据需要捕获并友好返回给客户端；
     * - 返回验证码字符串仅便于测试，生产环境请勿在接口响应中返回明文验证码。
     *
     * @param email 目标接收邮箱
     * @return 生成的验证码（当前实现返回验证码字符串）
     * @throws RuntimeException 发送任务入队或序列化失败时抛出
     */
    @Override
    public String sendVerificationCode(String email) {
        // 检查发送频率
        if (isVerificationCodeRateLimited(email)) {
            throw new RuntimeException("验证码发送太频繁，请 60 秒后重试");
        }

        // 生成6位随机验证码
        String verificationCode = RandomCodeUtil.generateNumberCode(6);

        // 实现异步发送邮件的逻辑
        try {

            // 创建邮件任务
            EmailTask emailTask = new EmailTask();

            // 初始化邮件任务内容
            // 1. 邮件目的邮箱
            // 2. 验证码
            // 3. 时间戳
            emailTask.setEmail(email);
            emailTask.setCode(verificationCode);
            emailTask.setTimestamp(System.currentTimeMillis());

            // 将邮件任务存入消息队列
            // 1. 将任务对象转成 JSON 字符串
            // 2. 将 JSON 字符串保存到 Redis 模拟的消息队列中
            String emailTaskJson = objectMapper.writeValueAsString(emailTask);
            String queueKey = RedisKey.emailTaskQueue();
            redisTemplate.opsForList().leftPush(queueKey, emailTaskJson);

            // 设置 email 发送注册验证码的限制
            String emailLimitKey = RedisKey.registerVerificationLimitCode(email);
            redisTemplate.opsForValue().set(emailLimitKey, "1", limitExpireSeconds, TimeUnit.SECONDS);

            return verificationCode;
        } catch (Exception e) {
            log.error("发送验证码邮件失败", e);
            throw new RuntimeException("发送验证码失败，请稍后重试");
        }
    }

    /**
     * checkVerificationCode
     *
     * 校验用户提交的验证码是否与 Redis 中保存的一致，验证通过后删除 Redis 中该键（一次性验证码）。
     *
     * @param email 邮箱
     * @param code  用户提交的验证码
     * @return true 表示校验通过（并已删除该验证码）；false 表示校验失败
     */
    @Override
    public boolean checkVerificationCode(String email, String code) {
        String redisKey = RedisKey.registerVerificationCode(email);
        String verificationCode = redisTemplate.opsForValue().get(redisKey);

        if (verificationCode != null && verificationCode.equals(code)) {
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    /**
     * isVerificationCodeRateLimited
     *
     * 判断给定邮箱是否在短时间内已发送过验证码（限流键是否存在）。
     *
     * @param email 邮箱
     * @return true 表示处于限流期（不可再次发送）；false 表示可以发送
     */
    @Override
    public boolean isVerificationCodeRateLimited(String email) {
        String redisKey = RedisKey.registerVerificationLimitCode(email);
        return redisTemplate.opsForValue().get(redisKey) != null;
    }
}
