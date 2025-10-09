package com.kama.notes.model.vo.category;

import lombok.Data;

import java.util.List;

/**
 * CategoryVO
 *
 * 分类视图对象（用于对外展示或接口返回）。
 *
 * 说明：
 * - 该类为只包含展示字段的简单 VO，不包含持久化相关注解或业务逻辑；
 * - children 字段用于表示树形结构的子分类列表，便于前端渲染层级关系。
 */
@Data
public class CategoryVO {
    /**
     * 分类 ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类 ID（根分类可为 null）
     */
    private Integer parentCategoryId;

    /**
     * 子分类列表（可能为空）
     */
    private List<ChildrenCategoryVO> children;

    /**
     * ChildrenCategoryVO
     *
     * 子分类的简化视图对象，用于嵌套在父分类的 children 中。
     */
    @Data
    public static class ChildrenCategoryVO {
        /**
         * 子分类 ID
         */
        private Integer categoryId;

        /**
         * 子分类名称
         */
        private String name;

        /**
         * 父分类 ID
         */
        private Integer parentCategoryId;
    }
}
