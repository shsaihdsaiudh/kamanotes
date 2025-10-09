package com.kama.notes.controller;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.dto.comment.CommentQueryParams;
import com.kama.notes.model.dto.comment.CreateCommentRequest;
import com.kama.notes.model.dto.comment.UpdateCommentRequest;
import com.kama.notes.model.vo.comment.CommentVO;
import com.kama.notes.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * CommentController
 *
 * 评论相关的 REST 控制器。
 *
 * 功能：
 * - 列表查询：支持分页与筛选（CommentQueryParams）；
 * - 创建评论：接收请求体并返回新评论 ID；
 * - 更新/删除：对指定评论进行更新或删除；
 * - 点赞/取消点赞：对指定评论进行点赞与取消操作。
 *
 * 设计要点：
 * - 统一返回 ApiResponse<T> 结构，便于前端统一处理状态码与消息；
 * - 使用 javax.validation 对入参进行基本校验（@Valid、字段注解）；
 * - 控制器不直接处理鉴权/登录，依赖拦截器或切面（如 TokenInterceptor / @NeedLogin）保证安全性；
 * - 建议在 Service 层处理事务、权限校验与业务一致性逻辑。
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表。
     *
     * 行为：
     * - 根据传入的 CommentQueryParams 返回符合条件的评论列表（含分页信息）；
     * - 入参使用 @Valid 校验。
     *
     * @param params 查询参数（分页/过滤）
     * @return ApiResponse 包含 List<CommentVO>
     */
    @GetMapping("/comments")
    public ApiResponse<List<CommentVO>> getComments(
            @Valid CommentQueryParams params) {
        return commentService.getComments(params);
    }

    /**
     * 创建评论。
     *
     * 行为：
     * - 校验请求体（CreateCommentRequest）后调用 Service 创建评论；
     * - 返回新创建评论的 ID（或错误信息由 ApiResponse 表示）。
     *
     * @param request 创建评论所需字段
     * @return ApiResponse 包含新创建的评论 ID（Integer）
     */
    @PostMapping("/comments")
    public ApiResponse<Integer> createComment(
            @Valid
            @RequestBody
            CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    /**
     * 更新评论。
     *
     * 行为：
     * - 对指定 commentId 的评论进行部分更新（由 UpdateCommentRequest 指定可更新字段）；
     * - 使用 @Valid 校验请求体，Service 层应处理权限与存在性校验。
     *
     * @param commentId 要更新的评论 ID
     * @param request   更新请求体
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @PatchMapping("/comments/{commentId}")
    public ApiResponse<EmptyVO> updateComment(
            @PathVariable("commentId") Integer commentId,
            @Valid
            @RequestBody
            UpdateCommentRequest request) {
        return commentService.updateComment(commentId, request);
    }

    /**
     * 删除评论。
     *
     * 行为：
     * - 删除指定 ID 的评论，Service 层需处理权限与级联删除（如通知、计数等）。
     *
     * @param commentId 要删除的评论 ID
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<EmptyVO> deleteComment(
            @PathVariable("commentId") Integer commentId) {
        return commentService.deleteComment(commentId);
    }

    /**
     * 点赞评论。
     *
     * 行为：
     * - 为指定评论添加一个点赞记录，Service 层应防重与并发处理（例如基于用户+评论唯一约束或去重逻辑）。
     *
     * @param commentId 要点赞的评论 ID
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @PostMapping("/comments/{commentId}/like")
    public ApiResponse<EmptyVO> likeComment(
            @PathVariable("commentId") Integer commentId) {
        return commentService.likeComment(commentId);
    }

    /**
     * 取消点赞评论。
     *
     * 行为：
     * - 移除指定评论的点赞记录，Service 层应保证幂等性与权限检查。
     *
     * @param commentId 要取消点赞的评论 ID
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @DeleteMapping("/comments/{commentId}/like")
    public ApiResponse<EmptyVO> unlikeComment(
            @PathVariable("commentId") Integer commentId) {
        return commentService.unlikeComment(commentId);
    }
}