package com.kama.notes.model.request.message;

import lombok.Data;

import java.util.List;

/**
 * ReadMessageBatchRequest
 *
 * 批量标记消息为已读的请求对象。
 *
 * 用途：
 * - 用于 API 接收前端传来的要标记为已读的消息 ID 列表；
 * - 由 Controller 层接收并传递到 Service 层执行批量更新操作。
 *
 * 注意：
 * - messageIds 可能为空或包含无效 ID，Service 层应进行必要的校验与容错处理；
 * - 推荐在接口文档中说明权限检查（仅允许消息接收者或管理员操作）。
 */
@Data
public class ReadMessageBatchRequest {
    /**
     * 待标记为已读的消息 ID 列表
     */
    private List<Integer> messageIds;
}
