package com.kama.notes.task.email;

import lombok.Data;

/**
 * EmailTask
 *
 * 邮件发送任务的数据载体（用于任务队列或异步处理）。
 *
 * 字段说明：
 * - email: 接收者邮箱地址；
 * - code: 验证码或任务相关的短字符串内容（例如注册/找回密码验证码）；
 * - timestamp: 任务创建时间戳，单位为毫秒（epoch ms），可用于任务过期判断或幂等控制。
 *
 * 使用说明：
 * - 该类为简单 POJO，使用 Lombok 的 @Data 自动生成 Getter/Setter/toString 等方法；
 * - 推荐在入队前校验 email 格式并在消费端根据 timestamp 判断是否过期；
 * - 若作为消息体通过序列化（JSON）传递，请确保消费者端的字段兼容性。
 */
@Data
public class EmailTask {
    /**
     * 接收者邮箱地址
     */
    private String email;

    /**
     * 验证码或任务携带的短文本
     */
    private String code;

    /**
     * 任务创建时间（毫秒），用于过期判断或幂等控制
     */
    private long timestamp;
}
