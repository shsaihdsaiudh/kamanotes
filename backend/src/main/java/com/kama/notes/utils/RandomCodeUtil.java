package com.kama.notes.utils;

import java.util.Random;

/**
 * RandomCodeUtil
 *
 * 简单的随机数字码生成工具类。
 *
 * 用途：
 * - 生成仅包含数字的验证码/临时码（例如短信验证码、表单临时校验码等）。
 *
 * 注意事项：
 * - 该实现使用 java.util.Random，适用于非安全敏感场景。
 *   若需用于安全相关场景（防止预测），请改用 java.security.SecureRandom；
 * - java.util.Random 不是并发安全的高性能随机数生成器；在高并发场景可考虑使用
 *   ThreadLocalRandom 或线程隔离的 Random 实例；
 * - 传入的 length 若为 0 或负数，方法会返回空字符串（当前实现通过循环行为自然实现）。
 */
public class RandomCodeUtil {
    /**
     * 生成指定长度的数字字符串（每位 0-9）。
     *
     * @param length 目标长度（非负数），当 length <= 0 时返回空字符串
     * @return 指定长度的数字字符串（例如 "034182"）
     */
    public static String generateNumberCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // random.nextInt(10) 生成 0 到 9 的随机整数
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
