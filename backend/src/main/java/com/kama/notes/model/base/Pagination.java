package com.kama.notes.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Pagination
 *
 * 分页元信息对象，用于在分页接口中传递分页参数与结果统计。
 *
 * 字段说明：
 * - page: 当前页码（从 1 开始，接口层/调用方应保证合法性）；
 * - pageSize: 每页记录数（用于计算偏移量或返回给前端展示）；
 * - total: 总记录数（用于前端计算总页数或显示总数）。
 *
 * 使用场景：
 * - 与 PaginationApiResponse/分页查询结果一起返回，或作为 Service/Mapper 层的分页参数载体。
 */
@Data
@AllArgsConstructor
public class Pagination {
    /**
     * 当前页码（从 1 开始）
     */
    private Integer page;  // 当前页码

    /**
     * 每页显示的记录数
     */
    private Integer pageSize;  // 每页显示的记录数

    /**
     * 总记录数
     */
    private Integer total;  // 总记录数
}
