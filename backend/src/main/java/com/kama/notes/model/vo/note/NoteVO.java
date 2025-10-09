package com.kama.notes.model.vo.note;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * NoteVO
 *
 * 笔记的视图对象（用于对外接口返回），包含笔记内容、统计、作者信息和当前用户的交互状态等。
 *
 * 设计要点：
 * - 仅作为展示层 VO，不包含持久化或业务逻辑；
 * - displayContent 用于前端展示的处理后文本（例如 HTML 渲染或摘要）；
 * - needCollapsed 表示前端是否需要对内容进行折叠显示（例如长文本或包含图片时）。
 */
@Data
public class NoteVO {
    /**
     * 笔记 ID（主键）
     */
    private Integer noteId;

    /**
     * 原始 Markdown 内容或存储的笔记文本
     */
    private String content;

    /**
     * 是否需要折叠显示（由后端判断或前端根据 displayContent 控制）
     */
    private Boolean needCollapsed = false;

    /**
     * 用于展示的处理后内容（例如渲染成 HTML、截断后的简介等）
     */
    private String displayContent;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 作者简要信息（避免在 NoteVO 中暴露完整用户实体）
     */
    private SimpleAuthorVO author;

    /**
     * 当前登录用户对该笔记的操作状态（是否已点赞、已收藏）
     */
    private UserActionsVO userActions;

    /**
     * 关联的问题/来源的简要信息（可为空）
     */
    private SimpleQuestionVO question;

    @Data
    public static class SimpleAuthorVO {
        /**
         * 作者用户 ID
         */
        private Long userId;

        /**
         * 作者显示名 / 用户名
         */
        private String username;

        /**
         * 作者头像地址（URL）
         */
        private String avatarUrl;
    }

    @Data
    public static class UserActionsVO {
        /**
         * 当前用户是否已对该笔记点赞
         */
        private Boolean isLiked = false;

        /**
         * 当前用户是否已将该笔记收藏
         */
        private Boolean isCollected = false;
    }

    @Data
    public static class SimpleQuestionVO {
        /**
         * 关联的问题/文章 ID
         */
        private Integer questionId;

        /**
         * 关联项的标题或摘要
         */
        private String title;
    }
}
