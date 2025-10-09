package com.kama.notes.service.impl;

import com.kama.notes.mapper.CategoryMapper;
import com.kama.notes.mapper.QuestionMapper;
import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.dto.category.CreateCategoryBody;
import com.kama.notes.model.dto.category.UpdateCategoryBody;
import com.kama.notes.model.entity.Category;
import com.kama.notes.model.vo.category.CategoryVO;
import com.kama.notes.model.vo.category.CreateCategoryVO;
import com.kama.notes.service.CategoryService;
import com.kama.notes.utils.ApiResponseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryServiceImpl
 *
 * 分类相关业务实现类。
 *
 * 责任：
 * - 提供分类的查询、创建、更新与删除逻辑；
 * - 构建前端需要的分类树结构；
 * - 在删除涉及多表操作时依赖事务保证一致性。
 *
 * 注意：
 * - 复杂的业务校验（权限、依赖检查等）应在 Service 层补充；
 * - 对可能抛出异常的方法使用事务或在上层捕获以保证数据一致性。
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private QuestionMapper QuestionMapper;

    /**
     * 构建分类树（只包含一级父分类及其直接子分类）。
     *
     * 实现要点：
     * - 从数据库读取所有分类，将 parentCategoryId == 0 的作为父分类；
     * - 子分类封装为 ChildrenCategoryVO 并加入对应父分类的 children 列表；
     *
     * @return 根父分类列表（每个父分类包含其子分类集合）
     */
    public List<CategoryVO> buildCategoryTree() {
        // 获取所有分类
        List<Category> categories = categoryMapper.categoryList();

        // 构建父分类的 Map，用于快速查找
        Map<Integer, CategoryVO> categoryMap = new HashMap<>();

        // 初始化父分类和子分类
        categories.forEach(category -> {
            if (category.getParentCategoryId() == 0) {
                // 父分类
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVO.setChildren(new ArrayList<>());
                categoryMap.put(category.getCategoryId(), categoryVO);
            } else {
                // 子分类
                CategoryVO.ChildrenCategoryVO childrenCategoryVO = new CategoryVO.ChildrenCategoryVO();
                BeanUtils.copyProperties(category, childrenCategoryVO);

                // 将子分类加入对应父分类的 children 列表
                CategoryVO parentCategory = categoryMap.get(category.getParentCategoryId());
                if (parentCategory != null) {
                    parentCategory.getChildren().add(childrenCategoryVO);
                }
            }
        });
        // 构建根分类列表
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public ApiResponse<List<CategoryVO>> categoryList() {
        return ApiResponseUtil.success("获取分类列表成功", buildCategoryTree());
    }

    /**
     * 删除分类（包括其作为父分类的子分类），并删除这些分类下关联的问题。
     *
     * 事务说明：
     * - 使用 @Transactional 保证删除分类与删除问题操作在同一事务中执行；
     * - 发生异常时抛出 RuntimeException 以触发回滚。
     *
     * @param categoryId 要删除的分类 ID
     * @return 操作结果的 ApiResponse（成功或错误信息）
     * @throws RuntimeException 删除失败时抛出以触发事务回滚
     */
    @Override
    @Transactional
    public ApiResponse<EmptyVO> deleteCategory(Integer categoryId) throws RuntimeException {
        // 找出分类 Id = categoryId
        // 或者 parentCategoryId 是 categoryId 的分类
        List<Category> categories = categoryMapper.findByIdOrParentId(categoryId);

        if (categories.isEmpty()) {
            return ApiResponseUtil.error("分类 Id 非法");
        }

        // 获取这些分类的 Ids
        List<Integer> categoryIds = categories.stream()
                .map(Category::getCategoryId)
                .toList();

        // 批量删除所有分类
        try {
            int deleteCount = categoryMapper.deleteByIdBatch(categoryIds);
            if (deleteCount != categoryIds.size()) {
                throw new RuntimeException("删除分类失败");
            }
            // 删除这些分类下的所有题目
            // TODO: 如果用户做了笔记，笔记和问题是对应的，删除了问题，笔记对应的问题就不存在了
            //   需要额外考虑讨论在删除分类的时候是否需要删除对应的笔记信息
            QuestionMapper.deleteByCategoryIdBatch(categoryIds);
            return ApiResponseUtil.success("删除分类成功");
        } catch (Exception e) {
            // 这里不能处理异常，需要抛出异常，让事务自动回滚
            throw new RuntimeException("删除分类失败");
        }
    }

    /**
     * 创建分类。
     *
     * 校验要点：
     * - 如果 parentCategoryId 非 0，验证父分类是否存在；
     * - 插入失败时返回错误响应。
     *
     * @param categoryBody 创建分类请求体
     * @return 包含新创建分类 ID 的 ApiResponse
     */
    @Override
    public ApiResponse<CreateCategoryVO> createCategory(CreateCategoryBody categoryBody) {

        if (categoryBody.getParentCategoryId() != 0) {
            Category parent = categoryMapper.findById(categoryBody.getParentCategoryId());
            if (parent == null) {
                return ApiResponseUtil.error("父分类 Id 不存在");
            }
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryBody, category);

        // 插入分类
        try {
            categoryMapper.insert(category);
            CreateCategoryVO createCategoryVO = new CreateCategoryVO();
            createCategoryVO.setCategoryId(category.getCategoryId());
            return ApiResponseUtil.success("创建分类成功", createCategoryVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("创建分类失败");
        }
    }

    /**
     * 更新分类名称。
     *
     * @param categoryId 分类 ID
     * @param categoryBody 更新请求体（包含新的 name）
     * @return 操作结果的 ApiResponse
     */
    @Override
    public ApiResponse<EmptyVO> updateCategory(Integer categoryId, UpdateCategoryBody categoryBody) {

        Category category = categoryMapper.findById(categoryId);

        if (category == null) {
            return ApiResponseUtil.error("分类 Id 不存在");
        }

        category.setName(categoryBody.getName());

        try {
            categoryMapper.update(category);
            return ApiResponseUtil.success("更新分类成功");
        } catch (Exception e) {
            return ApiResponseUtil.error("更新分类失败");
        }
    }

    /**
     * 根据分类名称查找分类，若不存在则创建一个父分类（parentCategoryId = 0）。
     *
     * @param categoryName 分类名称
     * @return 已存在或新创建的 Category 实例
     * @throws RuntimeException 创建失败时抛出异常
     */
    @Override
    public Category findOrCreateCategory(String categoryName) {
        Category category = categoryMapper.findByName(categoryName.trim());
        if (category != null) return category;

        try {
            Category category2 = new Category();
            category2.setName(categoryName.trim());
            category2.setParentCategoryId(0);
            categoryMapper.insert(category2);
            return category2;
        } catch (Exception e) {
            throw new RuntimeException("创建分类失败");
        }
    }

    /**
     * 根据分类名称查找分类，若不存在则创建并设置指定的父分类。
     *
     * @param categoryName 分类名称
     * @param parentCategoryId 父分类 ID
     * @return 已存在或新创建的 Category 实例
     * @throws RuntimeException 创建失败时抛出异常
     */
    @Override
    public Category findOrCreateCategory(String categoryName, Integer parentCategoryId) {
        Category category = categoryMapper.findByName(categoryName.trim());
        if (category != null) return category;
        try {
            Category category2 = new Category();
            category2.setName(categoryName.trim());
            category2.setParentCategoryId(parentCategoryId);
            categoryMapper.insert(category2);
            return category2;
        } catch (Exception e) {
            throw new RuntimeException("创建分类失败");
        }
    }
}
