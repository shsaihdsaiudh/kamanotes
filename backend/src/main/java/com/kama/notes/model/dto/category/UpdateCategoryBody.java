package com.kama.notes.model.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * UpdateCategoryBody
 *
 * 更新分类的请求 DTO，用于接收前端提交的分类更新表单数据。
 *
 * 说明：
 * - 仅包含可更新的字段（当前仅 name），不包含持久化或业务逻辑；
 * - 使用 Bean Validation 注解在 Controller 层对请求参数进行校验；
 * - name 不允许为空，长度在 1 到 32 字符之间。
 */
@Data
public class UpdateCategoryBody {

    /**
     * 分类名称
     *
     * 校验规则：
     * - 不能为空（@NotBlank / @NotNull）
     * - 长度限制：1 - 32（@Length）
     */
    @NotBlank(message = "name 不能为空")
    @NotNull(message = "name 不能为空")
    @Length(max = 32, min = 1, message = "name 长度在 1 - 32 之间")
    private String name;
}
