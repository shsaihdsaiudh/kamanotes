package com.kama.notes.controller;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.dto.questionListItem.CreateQuestionListItemBody;
import com.kama.notes.model.dto.questionListItem.QuestionListItemQueryParams;
import com.kama.notes.model.dto.questionListItem.SortQuestionListItemBody;
import com.kama.notes.model.vo.questionListItem.CreateQuestionListItemVO;
import com.kama.notes.model.vo.questionListItem.QuestionListItemUserVO;
import com.kama.notes.model.vo.questionListItem.QuestionListItemVO;
import com.kama.notes.service.QuestionListItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * QuestionListItemController
 *
 * 题单条目（QuestionListItem）相关的 REST 控制器。
 *
 * 提供：
 * - 用户端查询题单项列表；
 * - 管理端查询、创建、删除、排序题单项。
 *
 * 设计要点：
 * - 统一返回 ApiResponse<T> 以便前端统一处理响应结构；
 * - 使用 javax.validation 注解对入参做基础校验（@Valid, @Min）；
 * - 控制器仅负责参数校验与路由分发，具体业务逻辑与事务由 QuestionListItemService 实现。
 */
@RestController
@RequestMapping("/api")
public class QuestionListItemController {

    @Autowired
    private QuestionListItemService questionListItemService;

    /**
     * 用户端：获取指定题单中的题单项列表。
     *
     * 行为：
     * - 根据 QuestionListItemQueryParams（含题单 id、分页等）返回当前用户可见的题单项列表；
     * - 使用 @Valid 对查询参数做基础校验。
     *
     * @param queryParams 查询参数对象
     * @return ApiResponse 包含 List<QuestionListItemUserVO>
     */
    @GetMapping("/questionlist-items")
    public ApiResponse<List<QuestionListItemUserVO>> userGetQuestionListItems(
            @Valid QuestionListItemQueryParams queryParams) {
        return questionListItemService.userGetQuestionListItems(queryParams);
    }

    /**
     * 管理端：获取指定题单的题单项列表。
     *
     * 行为：
     * - 管理后台接口，返回完整的题单项信息以便管理操作；
     * - 要求 path 中的 questionListId 为正整数。
     *
     * @param questionListId 题单 ID（正整数）
     * @return ApiResponse 包含 List<QuestionListItemVO>
     */
    @GetMapping("/admin/questionlist-items/{questionListId}")
    public ApiResponse<List<QuestionListItemVO>> getQuestionListItems(
            @Min(value = 1, message = "questionListId 必须为正整数")
            @PathVariable Integer questionListId) {
        return questionListItemService.getQuestionListItems(questionListId);
    }

    /**
     * 管理端：创建新的题单项。
     *
     * 行为：
     * - 接收 CreateQuestionListItemBody 并创建题单中的一项，返回创建结果（含 id 等）；
     * - 使用 @Valid 校验请求体。
     *
     * @param body 创建题单项的请求体
     * @return ApiResponse 包含 CreateQuestionListItemVO（新建项的信息）
     */
    @PostMapping("/admin/questionlist-items")
    public ApiResponse<CreateQuestionListItemVO> createQuestionListItem(
            @Valid
            @RequestBody
            CreateQuestionListItemBody body) {
        return questionListItemService.createQuestionListItem(body);
    }

    /**
     * 管理端：删除指定题单中的题目项。
     *
     * 行为：
     * - 根据 questionListId 与 questionId 删除对应的题单项；
     * - 两个路径参数均需为正整数，具体级联影响由 Service 层处理。
     *
     * @param questionListId 题单 ID（正整数）
     * @param questionId     题目 ID（正整数）
     * @return ApiResponse 包含 EmptyVO，表示删除操作结果
     */
    @DeleteMapping("/admin/questionlist-items/{questionListId}/{questionId}")
    public ApiResponse<EmptyVO> deleteQuestionListItem(
            @Min(value = 1, message = "questionListId 必须为正整数")
            @PathVariable Integer questionListId,
            @Min(value = 1, message = "questionId 必须为正整数")
            @PathVariable Integer questionId) {
        return questionListItemService.deleteQuestionListItem(questionListId, questionId);
    }

    /**
     * 管理端：更新题单项顺序（排序）。
     *
     * 行为：
     * - 接收排序请求并在 Service 层执行批量更新以调整题单项顺序；
     * - 使用 @Valid 校验请求体，Service 层负责事务与并发处理。
     *
     * @param body 包含排序信息的请求体
     * @return ApiResponse 包含 EmptyVO，表示排序更新结果
     */
    @PatchMapping("/admin/questionlist-items/sort")
    public ApiResponse<EmptyVO> sortQuestionListItem(
            @Valid
            @RequestBody
            SortQuestionListItemBody body) {
        return questionListItemService.sortQuestionListItem(body);
    }
}
