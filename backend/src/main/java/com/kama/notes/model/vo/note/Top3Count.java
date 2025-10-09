package com.kama.notes.model.vo.note;

import lombok.Data;

/**
 * Top3Count
 *
 * 表示某段时间内进入 Top3 的统计计数，用于前端展示排行榜相关的汇总数据。
 *
 * 字段说明：
 * - lastMonthTop3Count：上个月进入 Top3 的数量（可为 null 表示未知或未统计）；
 * - thisMonthTop3Count：本月进入 Top3 的数量（可为 null 表示未知或未统计）。
 *
 * 使用场景：
 * - 在用户排行榜或统计面板中返回 Top3 的聚合数据；
 * - 该类为纯展示层 VO，不包含持久化或业务逻辑。
 */
@Data
public class Top3Count {
    /**
     * 上个月进入 Top3 的数量
     */
    private Integer lastMonthTop3Count;

    /**
     * 本月进入 Top3 的数量
     */
    private Integer thisMonthTop3Count;
}
