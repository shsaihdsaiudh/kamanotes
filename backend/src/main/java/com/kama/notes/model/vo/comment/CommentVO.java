package com.kama.notes.model.vo.comment;

import com.kama.notes.model.vo.user.UserActionVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CommentVO
 *
 * 评论视图对象（用于 API 对外返回评论相关数据）。
 *
 * 说明：
 * - 该类为展示层 VO，仅包含前端需要的字段，不包含持久化或业务逻辑；
 * - replies 字段用于嵌套回复列表（可用于展示二级回复或有限层级的评论树）；
 * - userActions 用于表示当前用户对该评论的操作状态（是否已点赞等），便于前端展示交互按钮状态。
 */
@Data
public class CommentVO {
    /**
     * 评论 ID
     */
    private Integer commentId;

    /**
     * 所属笔记 ID
     */
    private Integer noteId;

    /**
     * 评论内容（纯文本或已渲染后的 HTML，视接口约定而定）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数（直接子回复数量）
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

    /**
     * 作者的简要信息（ID、用户名、头像等）
     */
    private SimpleAuthorVO author;

    /**
     * 当前登录用户对该评论的操作信息（例如是否已点赞）
     */
    private UserActionVO userActions;

    /**
     * 回复列表（嵌套的 CommentVO，用于展示评论的回复）
     */
    private List<CommentVO> replies;

    /**
     * SimpleAuthorVO
     *
     * 简化的作者视图对象，仅包含在评论列表/详情中需要展示的基本字段，
     * 避免在评论返回中暴露过多用户信息。
     */
    @Data
    public static class SimpleAuthorVO {
        /**
         * 作者用户 ID
         */
        private Long userId;

        /**
         * 作者显示名称 / 用户名
         */
        private String username;

        /**
         * 作者头像地址（URL）
         */
        private String avatarUrl;
    }
}
