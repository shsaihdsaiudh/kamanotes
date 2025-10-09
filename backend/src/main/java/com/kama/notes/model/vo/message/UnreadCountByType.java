package com.kama.notes.model.vo.message;

import lombok.Data;

/**
 * UnreadCountByType
 *
 * 按消息类型统计的未读消息数量视图对象，用于接口返回未读数的聚合结果（例如前端消息中心或导航栏未读提示）。
 *
 * 说明：
 * - type：业务定义的消息类型标识（字符串或枚举名），例如 "COMMENT"、"LIKE"、"SYSTEM" 等；
 * - count：对应类型的未读数量，可能为 0；若未设置建议返回 0 而非 null，便于前端处理；
 * - 该类为纯展示层 VO，不包含业务逻辑或持久化注解。
 */
@Data
public class UnreadCountByType {
    /**
     * 消息类型标识（业务侧定义）
     */
    private String type;

    /**
     * 该类型的未读消息数量
     */
    private Integer count;
}