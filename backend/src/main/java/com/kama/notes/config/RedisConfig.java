package com.kama.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig
 *
 * Redis 相关配置类，主要用于定制 RedisTemplate 和 StringRedisTemplate 的序列化策略。
 *
 * 说明：
 * - redisTemplate 使用 String 作为 key 的序列化器，value 使用 GenericJackson2JsonRedisSerializer 序列化为 JSON，
 *   便于存储复杂对象并在不同语言/平台间保持兼容性。
 * - hash 相关的 key/value 也分别使用 StringRedisSerializer 和 GenericJackson2JsonRedisSerializer。
 * - stringRedisTemplate 提供了基于字符串操作的简便模板，适用于只存取字符串场景。
 *
 * 注意事项：
 * - GenericJackson2JsonRedisSerializer 使用 Jackson 进行序列化，若希望自定义 ObjectMapper（例如开启类型信息或安全配置），
 *   请创建并配置相应的 GenericJackson2JsonRedisSerializer 实例后替换默认构造函数。
 * - 序列化策略需与应用中读写 Redis 的其他代码保持一致，避免反序列化失败或类兼容性问题。
 * - 如果在分布式场景中需要更严格的兼容或性能优化，可考虑使用更轻量的序列化方案（如 kryo、protobuf 等）。
 */
@Configuration
public class RedisConfig {

    /**
     * 创建 RedisTemplate，指定 key/value 以及 hashKey/hashValue 的序列化器。
     *
     * 设计目的：
     * - key 使用 String 序列化，保证可读性及与 Redis-cli 的兼容。
     * - value 使用 JSON 序列化，便于存储复杂对象并在不同客户端间互操作。
     *
     * @param factory Redis 连接工厂，由 Spring 注入
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建一个通用的 RedisTemplate，key 为 String，value 为 Object（在 Java 端会通过序列化器转换为字节存入 Redis）
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置 RedisConnectionFactory，决定如何连接 Redis（由 Spring Boot 根据配置自动构建，可能是 Lettuce/Jedis）
        template.setConnectionFactory(factory);
        // 使用 String 序列化键（key）
        template.setKeySerializer(new StringRedisSerializer());
        // 使用 JSON 序列化值（value）
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 使用 String 序列化哈希键（hash key）和值（hash value）
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * 创建 StringRedisTemplate，简化对字符串类型数据的操作。
     *
     * @param redisConnectionFactory Redis 连接工厂，由 Spring 注入
     * @return StringRedisTemplate 实例
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
