package com.kama.notes.model.vo.note;

import lombok.Data;

/**
 * NoteRankListItem
 *
 * 笔记排行列表项视图对象，用于在前端展示按用户聚合的笔记数量排行信息。
 *
 * 字段说明：
 * - userId：用户 ID；
 * - username：用户显示名称，用于界面展示；
 * - avatarUrl：用户头像地址（URL）；
 * - noteCount：该用户在统计周期内的笔记数量；
 * - rank：该用户在排行榜中的名次（从 1 开始）。
 *
 * 该类为纯展示层 VO，不包含业务逻辑或持久化注解，通常由 Service 层组装并返回给 Controller。
 */
@Data
public class NoteRankListItem {
    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名 / 显示名称
     */
    private String username;

    /**
     * 用户头像 URL（可为空）
     */
    private String avatarUrl;

    /**
     * 笔记数量（用于排行依据）
     */
    private Integer noteCount;

    /**
     * 排名（从 1 开始），用于前端显示名次
     */
    private Integer rank;
}
