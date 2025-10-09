package com.kama.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.service.NoteLikeService;

/**
 * NoteLikeController
 *
 * 笔记点赞相关接口控制器：
 * - POST  /api/like/note/{noteId}   -> 为指定 noteId 的笔记添加点赞
 * - DELETE /api/like/note/{noteId}  -> 取消对指定 noteId 的点赞
 *
 * 说明：
 * - 控制器负责接收请求并委托 NoteLikeService 完成具体业务逻辑（如幂等、并发处理、计数维护等）。
 * - 路径参数 noteId 应为正整数，具体校验与异常处理建议在拦截器或 Service 层完成。
 * - 返回值统一使用 ApiResponse<EmptyVO> 表示操作结果（data 为空，状态与 message 表示具体结果）。
 */
@RestController
@RequestMapping("/api")
public class NoteLikeController {
    @Autowired
    private NoteLikeService noteLikeService;

    /**
     * 对指定笔记执行点赞操作。
     *
     * 行为：
     * - 将当前用户对 noteId 的点赞记录写入或更新（具体实现由 service 负责）；
     * - Service 层应保证幂等性（重复点赞不重复计数）并处理并发冲突。
     *
     * @param noteId 要点赞的笔记 ID（建议为正整数）
     * @return ApiResponse<EmptyVO> 操作结果
     */
    @PostMapping("/like/note/{noteId}")
    public ApiResponse<EmptyVO> likeNote(@PathVariable Integer noteId) {
        return noteLikeService.likeNote(noteId);
    }

    /**
     * 取消对指定笔记的点赞。
     *
     * 行为：
     * - 移除当前用户对 noteId 的点赞记录或将状态标记为已取消；
     * - Service 层应保证幂等性（重复取消不产生错误）并维护相关计数。
     *
     * @param noteId 要取消点赞的笔记 ID（建议为正整数）
     * @return ApiResponse<EmptyVO> 操作结果
     */
    @DeleteMapping("/like/note/{noteId}")
    public ApiResponse<EmptyVO> unlikeNote(@PathVariable Integer noteId) {
        return noteLikeService.unlikeNote(noteId);
    }
}
