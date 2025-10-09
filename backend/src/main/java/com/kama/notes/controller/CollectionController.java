package com.kama.notes.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.dto.collection.CollectionQueryParams;
import com.kama.notes.model.dto.collection.CreateCollectionBody;
import com.kama.notes.model.dto.collection.UpdateCollectionBody;
import com.kama.notes.model.vo.collection.CollectionVO;
import com.kama.notes.model.vo.collection.CreateCollectionVO;
import com.kama.notes.service.CollectionService;

/**
 * CollectionController
 *
 * 收藏夹相关的 REST 控制器，负责收藏夹的查询、创建、删除与批量修改接口。
 *
 * 设计说明：
 * - 接口均以 /api 前缀暴露，返回类型统一使用 ApiResponse 包装，便于前端统一处理状态与消息。
 * - 请求参数使用 javax.validation 注解进行基本校验（例如 @Valid、@Min）。
 * - 具体权限控制（如仅登录用户可操作）应由拦截器或切面在请求链上处理（例如 TokenInterceptor、NeedLogin 注解等）。
 */
@RestController
@RequestMapping("/api")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    /**
     * 获取收藏夹列表
     *
     * 行为：
     * - 根据 CollectionQueryParams 中的过滤/分页参数返回符合条件的收藏夹列表。
     * - 使用 @Valid 校验传入的查询参数。
     *
     * 返回：
     * - ApiResponse.data 包含 List<CollectionVO>，若无数据则返回空列表。
     *
     * 示例用途：
     * - 前端显示用户的收藏夹列表或管理页展示。
     *
     * @param queryParams 查询参数对象（支持分页/过滤）
     * @return ApiResponse 包含收藏夹视图对象列表
     */
    @GetMapping("/collections")
    public ApiResponse<List<CollectionVO>> getCollections(
            @Valid
            CollectionQueryParams queryParams) {
        return collectionService.getCollections(queryParams);
    }

    /**
     * 创建收藏夹
     *
     * 行为：
     * - 接收 CreateCollectionBody 并创建新的收藏夹记录。
     * - 使用 @Valid 验证请求体必填字段（例如名称等）。
     *
     * 返回：
     * - 创建成功时返回包含新创建收藏夹 ID 的 CreateCollectionVO。
     *
     * @param requestBody 创建收藏夹所需字段
     * @return ApiResponse 包含 CreateCollectionVO（新建收藏夹信息）
     */
    @PostMapping("/collections")
    public ApiResponse<CreateCollectionVO> createCollection(
            @Valid
            @RequestBody
            CreateCollectionBody requestBody) {
        return collectionService.createCollection(requestBody);
    }

    /**
     * 删除收藏夹
     *
     * 行为：
     * - 根据路径参数 collectionId 删除对应的收藏夹。
     * - 使用 @Min 校验 collectionId 为正整数。
     *
     * 注意：
     * - 删除操作可能涉及级联（如收藏夹下的笔记关系），相关业务逻辑应在 Service 层处理并保证数据一致性。
     *
     * @param collectionId 要删除的收藏夹 ID（正整数）
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @DeleteMapping("/collections/{collectionId}")
    public ApiResponse<EmptyVO> deleteCollection(
            @PathVariable
            @Min(value = 1, message = "collectionId 必须为正整数")
            Integer collectionId) {
        return collectionService.deleteCollection(collectionId);
    }

    /**
     * 批量修改收藏夹
     *
     * 行为：
     * - 接收 UpdateCollectionBody，可用于批量更新收藏夹的排序、名称或状态等字段。
     * - 使用 @Valid 校验请求体合法性。
     *
     * 返回：
     * - ApiResponse 包含 EmptyVO，表示批量修改的执行结果与状态信息。
     *
     * @param collectionBody 批量修改请求体（包含需要修改的项与目标值）
     * @return ApiResponse 包含 EmptyVO
     */
    @PostMapping("/collections/batch")
    public ApiResponse<EmptyVO> batchModifyCollection(
            @Valid
            @RequestBody
            UpdateCollectionBody collectionBody) {
        return collectionService.batchModifyCollection(collectionBody);
    }
}
