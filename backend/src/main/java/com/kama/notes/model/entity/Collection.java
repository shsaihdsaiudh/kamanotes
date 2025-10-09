package com.kama.notes.model.entity;

import lombok.Data;
import java.util.Date;

/**
 * Collection
 *
 * 收藏夹实体类，通常对应数据库中的 collection 表。
 *
 * 说明：
 * - 用于持久层与业务层之间传递收藏夹相关数据；
 * - 使用 Lombok 的 @Data 自动生成 getter/setter/toString/equals/hashCode 等方法；
 * - createdAt / updatedAt 使用 java.util.Date，若需更精确或时区控制可考虑使用 LocalDateTime。
 */
@Data
public class Collection {
    /**
     * 收藏夹ID（主键）
     */
    private Integer collectionId;

    /**
     * 收藏夹名称
     */
    private String name;

    /**
     * 收藏夹描述/备注
     */
    private String description;

    /**
     * 收藏夹创建者用户ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
} 