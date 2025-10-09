package com.kama.notes.model.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CreateCategoryBody
 *
 * 创建分类的请求 DTO，用于接收前端提交的创建分类表单数据。
 *
 * 说明：
 * - 使用 Bean Validation 注解在 Controller 层对请求参数进行校验；
 * - name: 分类名称，非空且长度限制（1-32）；
 * - parentCategoryId: 父分类 ID，允许为 0 表示根分类，必须为非负整数。
 *
 * 校验建议：
 * - 在 Controller 方法上使用 @Valid 或 @Validated 以触发注解校验；
 * - 业务层在创建前仍应检查同名分类/命名冲突与权限。
 */
@Data
public class CreateCategoryBody {

    /**
     * 分类名称
     *
     * - 不能为空（NotBlank / NotNull）；
     * - 最大长度 32，最小长度 1（Hibernate Validator 的 @Length）。
     */
    @NotBlank(message = "name 不能为空")
    @NotNull(message = "name 不能为空")
    @Length(max = 32, min = 1, message = "name 长度在 1 - 32 之间")
    private String name;

    /**
     * 父分类 ID
     *
     * - 必须非空；
     * - 使用 @Min(0) 限制为非负整数，0 可表示根分类。
     * - 调用方/业务层应验证 parentCategoryId 是否存在且合法（避免引用不存在的父分类）。
     */
    @NotNull(message = "parentCategoryId 不能为空")
    @Min(value = 0, message = "parentCategoryId 必须为正整数")
    private Integer parentCategoryId;
}
