package com.kama.notes.model.vo.category;

import lombok.Data;

/**
 * CreateCategoryVO
 *
 * 创建分类后的返回视图对象（用于 API 返回新建分类的关键信息）。
 *
 * 说明：
 * - 仅包含客户端在创建成功后需要的最小字段（例如 categoryId）；
 * - 作为响应 VO，不包含持久化或校验逻辑。
 */
@Data
public class CreateCategoryVO {
    /**
     * 新创建分类的 ID
     */
    private Integer categoryId;
}
