package com.kama.notes.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Comment
 *
 * 评论实体类，通常对应数据库中的 comment 表，用于持久层与业务层之间传递评论相关数据。
 *
 * 说明：
 * - 该类为持久层实体（POJO），不包含业务逻辑；
 * - parentId 用于表示回复关系（null 或 0 可表示顶级评论）；
 * - likeCount / replyCount 建议在数据库层默认为 0，避免 null 引发业务判断错误；
 * - createdAt / updatedAt 使用 LocalDateTime，便于记录精确时间并在展示/统计时使用；
 * - 在并发更新（例如点赞计数）场景下，请在 Service/Mapper 层使用合适的乐观/悲观策略或原子性 SQL 操作。
 */
@Data
public class Comment {
    /**
     * 评论ID
     */
    private Integer commentId;

    /**
     * 笔记ID
     */
    private Integer noteId;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 父评论ID
     */
    private Integer parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}