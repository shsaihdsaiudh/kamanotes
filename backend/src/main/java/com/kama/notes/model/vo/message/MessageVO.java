package com.kama.notes.model.vo.message;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * MessageVO
 *
 * 消息视图对象（用于对外接口返回的消息结构）。
 *
 * 说明：
 * - 该类为纯展示层 VO，仅包含前端需要的字段，不包含持久化或业务逻辑；
 * - 适用于站内信、通知、系统消息等场景的传输与展示；
 * - 建议在发布事件或通过 websocket 推送前，将持久化实体转换为该 VO，避免携带懒加载或敏感字段。
 */
@Data
public class MessageVO {
    /**
     * 消息 ID（主键或业务唯一标识）
     */
    private Integer messageId;

    /**
     * 发送者信息（简化）
     */
    private Sender sender;

    /**
     * 消息类型（由业务定义的枚举或整型标识，例如 1=评论 2=点赞 3=系统通知）
     */
    private Integer type;

    /**
     * 消息目标信息（例如目标笔记、问题等的摘要）
     */
    private Target target;

    /**
     * 消息主体内容（纯文本或已渲染的简单 HTML，按接口约定）
     */
    private String content;

    /**
     * 是否已读（用于前端展示已读/未读状态）
     */
    private Boolean isRead;

    /**
     * 创建时间（消息生成时间）
     */
    private LocalDateTime createdAt;

    /**
     * Sender
     *
     * 发送者的简化视图对象，避免在消息结构中暴露过多用户信息。
     */
    @Data
    public static class Sender {
        /**
         * 发送者用户 ID
         */
        private Long userId;

        /**
         * 发送者显示名 / 用户名
         */
        private String username;

        /**
         * 发送者头像 URL（如果有）
         */
        private String avatarUrl;
    }

    /**
     * Target
     *
     * 消息目标的通用描述（可包含目标 ID、类型及关联的摘要信息）。
     */
    @Data
    public static class Target {
        /**
         * 目标 ID（例如笔记 ID、问题 ID 等）
         */
        private Integer targetId;

        /**
         * 目标类型（由业务定义的枚举或整型标识）
         */
        private Integer targetType;

        /**
         * 针对问题/文章等类型时的简要摘要，便于在通知中展示上下文
         */
        private QuestionSummary questionSummary;
    }

    /**
     * QuestionSummary
     *
     * 针对问题/文章类目标的简要信息，用于通知摘要展示（例如标题与 ID）。
     */
    @Data
    public static class QuestionSummary {
        /**
         * 关联的问题/文章 ID
         */
        private Integer questionId;

        /**
         * 目标标题（用于在通知中展示）
         */
        private String title;
    }
} 