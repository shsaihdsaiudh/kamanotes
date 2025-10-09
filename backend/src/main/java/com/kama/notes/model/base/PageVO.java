package com.kama.notes.model.base;

import lombok.Data;
import java.util.List;

/**
 * PageVO
 *
 * 分页结果的统一视图对象，用于承载分页查询的元数据与当前页的数据列表。
 *
 * 说明：
 * - page: 当前页码（从 1 开始，建议在 Controller 层校验）；
 * - pageSize: 每页大小（用于前端展示与计算 totalPages）；
 * - total: 总记录数，用于计算总页数与前端分页控件展示；
 * - totalPages: 总页数，由 of(...) 方法计算并设置；
 * - list: 当前页的数据列表，元素类型由泛型 T 指定。
 *
 * 使用示例：
 * PageVO.of(list, page, pageSize, total);
 *
 * @param <T> 列表中元素类型
 */
@Data
public class PageVO<T> {
    /**
     * 当前页码（从 1 开始）
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 总页数（根据 total 与 pageSize 计算）
     */
    private Integer totalPages;

    /**
     * 当前页的数据列表
     */
    private List<T> list;

    /**
     * 全参构造函数，用于手动创建 PageVO 实例。
     *
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param total 总记录数
     * @param list 当前页的数据列表
     */
    public PageVO(Integer page, Integer pageSize, Integer total, List<T> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }

    /**
     * 静态工厂方法：根据查询结果创建 PageVO 并自动计算总页数。
     *
     * 计算公式： totalPages = (total + pageSize - 1) / pageSize
     *
     * @param list 数据列表
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param total 总记录数
     * @param <T> 列表元素类型
     * @return 构造好的 PageVO 实例
     */
    public static <T> PageVO<T> of(List<T> list, Integer page, Integer pageSize, Integer total) {
        PageVO<T> pageVO = new PageVO<>(page, pageSize, total, list);
        pageVO.setTotalPages((total + pageSize - 1) / pageSize);
        return pageVO;
    }
} 