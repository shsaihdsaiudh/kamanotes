package com.kama.notes.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatisConfig
 *
 * MyBatis 相关配置类。
 *
 * 说明：
 * - @Configuration：标识为 Spring 配置类，Spring 容器会扫描并加载该类。
 * - @MapperScan("com.kama.notes.mapper")：自动扫描 mapper 接口所在包，
 *   无需在每个 Mapper 接口上使用 @Mapper 注解。
 *   请确保参数指定的包路径与项目中的 mapper 包路径一致。
 * - @EnableTransactionManagement：启用 Spring 注解事务支持，
 *   可在 Service 层使用 @Transactional 管理事务。
 *
 * 使用建议：
 * - 若项目中存在多个 mapper 包，可将扫描路径改为更通用的根包，例如 "com.kama.notes.**" 或添加多个 @MapperScan。
 * - 若使用 XML 映射文件，请确保 resources/mapper 路径与 MyBatis 配置一致。
 *
 * 作者：Tong
 * 最后修改：2024-12-17
 */
@Configuration
@MapperScan("com.kama.notes.mapper")
@EnableTransactionManagement
public class MyBatisConfig {
}
