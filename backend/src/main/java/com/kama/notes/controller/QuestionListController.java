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
import com.kama.notes.model.dto.questionList.CreateQuestionListBody;
import com.kama.notes.model.dto.questionList.UpdateQuestionListBody;
import com.kama.notes.model.entity.QuestionList;
import com.kama.notes.model.vo.questionList.CreateQuestionListVO;
import com.kama.notes.service.QuestionListService;

/**
 * QuestionListController
 *
 * 题单（QuestionList）管理的 REST 控制器。
 *
 * 职责：
 * - 提供题单的增、删、改、查接口（管理端 /admin 前缀）；
 * - 校验路径参数与请求体（@Min, @Valid）并将请求委托给 QuestionListService 处理；
 * - 统一返回 ApiResponse<T>，无数据时使用 EmptyVO。
 *
 * 设计说明：
 * - 控制器不负责复杂业务逻辑与事务，相关逻辑应在 Service 层实现；
 * - 对需要鉴权的接口请在拦截器或切面中处理（当前为管理端接口，需配合鉴权策略）。
 */
@RestController
@RequestMapping("/api")
public class QuestionListController {

    @Autowired
    private QuestionListService questionListService;

    /**
     * 获取指定题单详情（管理端）。
     *
     * @param questionListId 题单 ID，必须为正整数
     * @return ApiResponse 包含 QuestionList 实体
     */
    @GetMapping("/admin/questionlists/{questionListId}")
    public ApiResponse<QuestionList> getQuestionList(@Min(value = 1, message = "questionListId 必须为正整数")
                                                     @PathVariable Integer questionListId) {
        return questionListService.getQuestionList(questionListId);
    }

    /**
     * 获取题单列表（管理端）。
     *
     * 返回所有题单的列表，Service 层可支持分页/过滤扩展。
     *
     * @return ApiResponse 包含题单列表
     */
    @GetMapping("/admin/questionlists")
    public ApiResponse<List<QuestionList>> getQuestionLists() {
        return questionListService.getQuestionLists();
    }

    /**
     * 创建新的题单（管理端）。
     *
     * 入参使用 @Valid 校验 CreateQuestionListBody 中的字段。
     *
     * @param body 创建题单的请求体
     * @return ApiResponse 包含 CreateQuestionListVO（创建结果，如 id 等）
     */
    @PostMapping("/admin/questionlists")
    public ApiResponse<CreateQuestionListVO> createQuestionList(@Valid @RequestBody CreateQuestionListBody body) {
        return questionListService.createQuestionList(body);
    }

    /**
     * 删除指定题单（管理端）。
     *
     * 注意：删除操作的级联影响（如题目关系）应由 Service 层处理并保证数据一致性。
     *
     * @param questionListId 要删除的题单 ID，必须为正整数
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @DeleteMapping("/admin/questionlists/{questionListId}")
    public ApiResponse<EmptyVO> deleteQuestionList(@Min(value = 1, message = "questionListId 必须为正整数")
                                                   @PathVariable Integer questionListId) {
        return questionListService.deleteQuestionList(questionListId);
    }

    /**
     * 更新指定题单信息（管理端，部分更新）。
     *
     * 使用 @Valid 校验更新请求体，Service 层负责权限、存在性与事务处理。
     *
     * @param questionListId 要更新的题单 ID，必须为正整数
     * @param body           更新内容
     * @return ApiResponse 包含 EmptyVO，表示操作结果
     */
    @PatchMapping("/admin/questionlists/{questionListId}")
    public ApiResponse<EmptyVO> updateQuestionList(@Min(value = 1, message = "questionListId 必须为正整数")
                                                   @PathVariable Integer questionListId,
                                                   @Valid @RequestBody UpdateQuestionListBody body) {
        return questionListService.updateQuestionList(questionListId, body);
    }
}
