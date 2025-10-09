package com.kama.notes.model.vo.note;

import lombok.Data;

import java.time.LocalDate;

/**
 * NoteHeatMapItem
 *
 * 笔记热力图项视图对象，用于表示某一天的笔记统计数据（通常用于前端热力图/日历视图）。
 *
 * 字段说明：
 * - date: 统计日期（本地日期，不包含时分秒）；
 * - count: 当日笔记相关的计数（例如新建/活动次数等，根据上层业务定义）；
 * - rank: 当日在一段时间内的排名（可选，用于展示热点排序），若无意义可为 null。
 *
 * 使用场景示例：
 * - 前端日历中展示每一天的笔记数量，并根据 count 显示不同深浅的颜色；
 * - 后端聚合每日数据后返回 List<NoteHeatMapItem> 给前端渲染热力图。
 */
@Data
public class NoteHeatMapItem {
    /**
     * 统计对应的日期（LocalDate）
     */
    private LocalDate date;

    /**
     * 当日计数（例如新增笔记数、活跃度等）
     */
    private Integer count;

    /**
     * 当日排名（可选），用于表示在某一范围内的热度排序
     */
    private Integer rank;
}
