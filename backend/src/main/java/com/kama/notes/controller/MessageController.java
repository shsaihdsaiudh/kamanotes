package com.kama.notes.controller;

import com.kama.notes.annotation.NeedLogin;
import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.request.message.ReadMessageBatchRequest;
import com.kama.notes.model.vo.message.MessageVO;
import com.kama.notes.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MessageController
 *
 * 消息相关 REST 控制器。
 *
 * 提供的功能：
 * - 获取当前用户的消息列表；
 * - 标记单条/多条/全部消息为已读；
 * - 删除消息；
 * - 查询未读消息数量。
 *
 * 说明：
 * - 部分接口需要用户登录（通过 @NeedLogin 或拦截器保证），具体鉴权在拦截器或切面中实现；
 * - 返回统一使用 ApiResponse<T>，空结果使用 EmptyVO 表示；
 * - 业务逻辑与事务在 MessageService 中实现，控制器职责为参数校验与路由转发。
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取消息列表。
     *
     * 说明：
     * - 返回当前登录用户的消息列表，数据类型为 List<MessageVO>。
     * - 使用 @NeedLogin 注解确保请求方已登录。
     *
     * @return ApiResponse 包含消息视图对象列表
     */
    @GetMapping
    @NeedLogin
    public ApiResponse<List<MessageVO>> getMessages() {
        return messageService.getMessages();
    }

    /**
     * 标记指定消息为已读。
     *
     * @param messageId 要标记为已读的消息 ID
     * @return ApiResponse<EmptyVO> 空数据表示操作结果，具体成功/失败信息在 ApiResponse 中
     */
    @PatchMapping("/{messageId}/read")
    public ApiResponse<EmptyVO> markAsRead(@PathVariable Integer messageId) {
        return messageService.markAsRead(messageId);
    }

    /**
     * 标记当前用户的所有消息为已读。
     *
     * 说明：请确保 Service 层对大量数据的更新做合适的批处理或性能优化。
     *
     * @return ApiResponse<EmptyVO> 操作结果
     */
    @PatchMapping("/all/read")
    public ApiResponse<EmptyVO> markAllAsRead() {
        return messageService.markAllAsRead();
    }

    /**
     * 批量标记消息为已读。
     *
     * 请求体示例：{"messageIds":[1,2,3]}
     *
     * @param request 包含要标记已读的消息 ID 列表
     * @return ApiResponse<EmptyVO> 操作结果
     */
    @PatchMapping("/batch/read")
    public ApiResponse<EmptyVO> markAsReadBatch(@RequestBody ReadMessageBatchRequest request) {
        return messageService.markAsReadBatch(request.getMessageIds());
    }

    /**
     * 删除指定消息。
     *
     * 说明：删除操作应由 Service 层处理权限校验与级联清理（如通知、计数等）。
     *
     * @param messageId 要删除的消息 ID
     * @return ApiResponse<EmptyVO> 操作结果
     */
    @DeleteMapping("/{messageId}")
    public ApiResponse<EmptyVO> deleteMessage(@PathVariable Integer messageId) {
        return messageService.deleteMessage(messageId);
    }

    /**
     * 获取当前用户的未读消息数量。
     *
     * @return ApiResponse<Integer> 返回未读消息的数量
     */
    @GetMapping("/unread/count")
    public ApiResponse<Integer> getUnreadCount() {
        return messageService.getUnreadCount();
    }
}
