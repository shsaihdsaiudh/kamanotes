package com.kama.notes.task.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kama.notes.model.enums.redisKey.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * EmailTaskConsumer
 *
 * 从 Redis 列表中轮询消费 EmailTask（JSON 字符串），并使用 JavaMailSender 发送邮件。
 *
 * 工作流程：
 * 1. 每隔固定时间（fixedDelay）触发 resume() 方法，循环从队列尾部（rightPop）取出任务；
 * 2. 将取出的 JSON 反序列化为 EmailTask 对象，构建邮件并发送；
 * 3. 将验证码保存到 Redis（作为短期校验码），设置过期时间（5 分钟）；
 *
 * 注意：
 * - 当前实现使用轮询（非阻塞的 rightPop），当队列为空退出循环；若需阻塞式等待可使用 BLPOP/BRPOP 或消息中间件；
 * - 生产中应增加异常处理、重试、幂等与任务失败持久化策略，防止发送失败导致验证码丢失；
 * - 在高并发场景下请注意邮件发送吞吐与速率限制，建议使用异步发送池或第三方邮件服务 API。
 */
@Component
public class EmailTaskConsumer {

    /**
     * Spring 提供的邮件发送器，用于发送简单文本邮件
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Jackson ObjectMapper 用于将队列中的 JSON 字符串反序列化为 EmailTask
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 用于从 Redis 队列读取任务及保存验证码到 Redis（registerVerificationCode）
     * 这里使用 RedisTemplate<String, String>，value 存储为字符串
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 发件人邮箱，从配置 spring.mail.username 注入
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 定时任务：每 3 秒轮询 Redis 队列（非阻塞），消费所有当前可用的邮件任务。
     *
     * 实现细节：
     * - 使用 RedisKey.emailTaskQueue() 获取队列 key；
     * - 使用 rightPop 逐个弹出队列元素（null 表示队列为空，跳出本次轮询）；
     * - 将 JSON 转为 EmailTask，构建 SimpleMailMessage 并发送；
     * - 发送成功后将验证码保存到 Redis，设置 5 分钟过期，便于后续校验。
     *
     * 异常与健壮性建议：
     * - 当前方法声明 throws JsonProcessingException，若反序列化失败会中断本次调度。可捕获并记录错误以保证后续任务继续处理；
     * - 邮件发送失败应考虑重试或将任务放入失败队列以便人工/后台处理；
     * - 若需保证任务不丢失，可采用 BRPOP/事务或使用消息队列（Rabbit/Kafka）替代简单的 Redis 列表。
     */
    @Scheduled(fixedDelay = 3000)
    public void resume() throws JsonProcessingException {
        String emailQueueKey = RedisKey.emailTaskQueue();

        // 从队列中不断取任务，直到队列为空（rightPop 返回 null）
        while (true) {

            // 获取任务对象（JSON 字符串）
            String emailTaskJson = redisTemplate.opsForList().rightPop(emailQueueKey);

            if (emailTaskJson == null) {  // 队列中没有任务对象，退出本次执行
                break;
            }

            // 将 redis 中的 JSON 字符串转成 EmailTask 对象
            EmailTask emailTask = objectMapper.readValue(emailTaskJson, EmailTask.class);
            String email = emailTask.getEmail();
            String verificationCode = emailTask.getCode();

            // 构建并发送简单文本邮件
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(email);
            mailMessage.setSubject("卡码笔记- 验证码");
            mailMessage.setText("您的验证码是：" + verificationCode + "，有效期" + 5 + "分钟，请勿泄露给他人。");

            mailSender.send(mailMessage);

            // 发送成功后将验证码保存到 Redis，过期时间 5 分钟（用于注册/校验）
            redisTemplate.opsForValue().set(RedisKey.registerVerificationCode(email), verificationCode, 5, TimeUnit.MINUTES);
        }
    }
}
