package com.kama.notes.model.enums.redisKey;

/**
 * RedisKey
 *
 * 统一管理项目中使用的 Redis 键名生成器。
 *
 * 设计要点：
 * - 使用冒号分隔的层级命名，便于运维、统计与清理；
 * - 通过方法生成键名，避免在代码中散落硬编码字符串，便于集中维护与修改。
 */
public class RedisKey {
    /**
     * 生成用于保存注册场景验证码的 Redis 键名。
     *
     * 格式：email:register_verification_code:{email}
     *
     * @param email 用户邮箱（建议在调用前进行必要的校验或归一化）
     * @return 用于存储注册验证码的 Redis 键名
     */
    public static String registerVerificationCode(String email) {
        return "email:register_verification_code:" + email;
    }

    /**
     * 生成用于记录注册验证码发送频率限制的 Redis 键名。
     *
     * 格式：email:register_verification_code:limit:{email}
     * 用途：防止短时间内重复发送验证码（节流/限速）。
     *
     * @param email 用户邮箱
     * @return 用于限制发送频率的 Redis 键名
     */
    public static String registerVerificationLimitCode(String email) {
        return "email:register_verification_code:limit:" + email;
    }

    /**
     * 生成邮件发送任务队列的 Redis 键名。
     *
     * 格式：queue:email:task
     *
     * @return 邮件任务队列在 Redis 中使用的键名
     */
    public static String emailTaskQueue() {
        return "queue:email:task";
    }
}
