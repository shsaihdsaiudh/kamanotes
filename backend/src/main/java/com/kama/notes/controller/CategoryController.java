package com.kama.notes.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.dto.category.CreateCategoryBody;
import com.kama.notes.model.dto.category.UpdateCategoryBody;
import com.kama.notes.model.vo.category.CategoryVO;
import com.kama.notes.model.vo.category.CreateCategoryVO;
import com.kama.notes.service.CategoryService;

/**
 * CategoryController
 *
 * 分类相关的 REST 控制器，提供前端与管理端所需的分类查询与管理接口。
 *
 * 设计说明：
 * - 用户端与管理员端的列表接口目前共用相同的 service 方法，后续若有权限或返回字段差异可拆分处理；
 * - 管理端接口位于 /api/admin/**，应配合鉴权中间件或切面使用（示例中 SecurityConfig 对 /api/** 已放行，
 *   如需限制请在安全配置中收紧规则）。
 *
 * 路径前缀：/api
 */
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表（用户端）。
     *
     * 返回：包含所有分类信息的 ApiResponse，data 为 CategoryVO 的列表。
     *
     * 示例用途：前端展示笔记分类导航或过滤菜单。
     *
     * @return ApiResponse 包含 List<CategoryVO>
     */
    @GetMapping("/categories")
    public ApiResponse<List<CategoryVO>> userCategories() {
        return categoryService.categoryList();
    }

    /**
     * 获取分类列表（管理员端）。
     *
     * 管理端可能需要展示额外操作项（前端可根据权限显示），当前同用户端返回数据结构。
     *
     * @return ApiResponse 包含 List<CategoryVO>
     */
    @GetMapping("/admin/categories")
    public ApiResponse<List<CategoryVO>> categories() {
        return categoryService.categoryList();
    }

    /**
     * 创建新的分类（管理员操作）。
     *
     * 请求体校验：CreateCategoryBody 使用 @Valid 注解进行字段级别校验（如名称非空等）。
     *
     * 返回：创建成功后的分类信息（CreateCategoryVO），包含新建分类的 id 等必要字段。
     *
     * @param createCategoryBody 创建分类所需的请求体
     * @return ApiResponse 包含 CreateCategoryVO
     */
    @PostMapping("/admin/categories")
    public ApiResponse<CreateCategoryVO> createCategory(
            @Valid @RequestBody CreateCategoryBody createCategoryBody) {
        return categoryService.createCategory(createCategoryBody);
    }

    /**
     * 更新指定的分类信息（管理员操作）。
     *
     * 路径参数：
     * - categoryId：分类 ID，必须为正整数（使用 @Min 校验）。
     *
     * 请求体校验：UpdateCategoryBody 使用 @Valid 注解进行字段级别校验。
     *
     * 返回：空数据对象 EmptyVO 的通用响应，表示更新成功或失败信息由 ApiResponse 的状态与消息决定。
     *
     * @param categoryId 分类 ID（正整数）
     * @param updateCategoryBody 更新用请求体
     * @return ApiResponse 包含 EmptyVO
     */
    @PatchMapping("/admin/categories/{categoryId}")
    public ApiResponse<EmptyVO> updateCategory(
            @Min(value = 1, message = "categoryId 必须为正整数") @PathVariable Integer categoryId,
            @Valid @RequestBody UpdateCategoryBody updateCategoryBody) {
        return categoryService.updateCategory(categoryId, updateCategoryBody);
    }

    /**
     * 删除指定的分类（管理员操作）。
     *
     * 路径参数：
     * - categoryId：分类 ID，必须为正整数（使用 @Min 校验）。
     *
     * 返回：空数据对象 EmptyVO 的通用响应，表示删除操作结果。
     *
     * 注意：删除分类时需考虑级联影响（如笔记与分类的关联），该逻辑应在 Service 层处理。
     *
     * @param categoryId 分类 ID（正整数）
     * @return ApiResponse 包含 EmptyVO
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    public ApiResponse<EmptyVO> deleteCategory(
            @Min(value = 1, message = "categoryId 必须为正整数") @PathVariable Integer categoryId) {
        return categoryService.deleteCategory(categoryId);
    }
}
