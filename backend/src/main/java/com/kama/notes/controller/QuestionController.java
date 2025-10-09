package com.kama.notes.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.kama.notes.model.dto.question.*;
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
import com.kama.notes.model.vo.question.CreateQuestionVO;
import com.kama.notes.model.vo.question.QuestionNoteVO;
import com.kama.notes.model.vo.question.QuestionUserVO;
import com.kama.notes.model.vo.question.QuestionVO;
import com.kama.notes.service.QuestionService;

/**
 * QuestionController
 *
 * 问答模块的 REST 控制器，包含用户端与管理端关于问题的查询、搜索、创建、更新与删除接口。
 *
 * 设计要点：
 * - 用户端接口（如 /questions）返回用户视图对象，管理端接口（如 /admin/questions）返回完整管理视图；
 * - 入参使用 javax.validation 做基础校验（@Valid、@Min）；
 * - 控制器只负责参数校验与路由分发，所有业务逻辑应在 QuestionService 中实现（含权限与事务）；
 * - 返回类型统一使用 ApiResponse<T>，便于前端统一处理状态与消息；无数据时可使用 EmptyVO。
 */
@RestController
@RequestMapping("/api")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 用户端：获取问题列表
     *
     * 行为：
     * - 返回当前可见的问题列表（QuestionUserVO），支持基于 QueryParam 的筛选与分页；
     * - 参数使用 @Valid 验证基本约束。
     *
     * @param queryParams 查询参数（支持关键词、分类、分页等）
     * @return ApiResponse 包含 List<QuestionUserVO>
     */
    @GetMapping("/questions")
    public ApiResponse<List<QuestionUserVO>> userGetQuestions(@Valid QuestionQueryParam queryParams) {
        return questionService.userGetQuestions(queryParams);
    }

    /**
     * 用户端：搜索问题
     *
     * 行为：
     * - 根据请求体中的搜索条件执行搜索，并返回匹配的 QuestionVO 列表；
     * - 使用 @Valid 校验请求体格式。
     *
     * @param body 搜索请求体，包含关键词等搜索条件
     * @return ApiResponse 包含搜索结果 List<QuestionVO>
     */
    @PostMapping("/questions/search")
    public ApiResponse<List<QuestionVO>> searchQuestions(@Valid @RequestBody SearchQuestionBody body) {
        return questionService.searchQuestions(body);
    }

    /**
     * 用户端：获取单个问题详情（含关联笔记）
     *
     * 行为：
     * - 根据 questionId 返回问题详情及其关联的笔记信息（QuestionNoteVO）；
     * - 使用 @Min 校验路径参数为正整数。
     *
     * @param questionId 问题 ID（正整数）
     * @return ApiResponse 包含 QuestionNoteVO
     */
    @GetMapping("/questions/{questionId}")
    public ApiResponse<QuestionNoteVO> userGetQuestion(@Min(value = 1, message = "questionId 必须为正整数")
                                                       @PathVariable Integer questionId) {
        return questionService.userGetQuestion(questionId);
    }

    /**
     * 管理端：获取问题列表
     *
     * 行为：
     * - 为管理后台返回问题列表（QuestionVO），支持更丰富的筛选与分页参数；
     * - 使用 @Valid 校验查询参数。
     *
     * @param queryParams 管理端查询参数
     * @return ApiResponse 包含 List<QuestionVO>
     */
    @GetMapping("/admin/questions")
    public ApiResponse<List<QuestionVO>> getQuestions(@Valid QuestionQueryParam queryParams) {
        return questionService.getQuestions(queryParams);
    }

    /**
     * 管理端：创建新问题
     *
     * 行为：
     * - 接收 CreateQuestionBody 并创建问题，返回创建后的视图对象（含 id 等）。
     * - 使用 @Valid 校验请求体字段。
     *
     * @param createQuestionBody 创建问题所需字段
     * @return ApiResponse 包含 CreateQuestionVO
     */
    @PostMapping("/admin/questions")
    public ApiResponse<CreateQuestionVO> createQuestion(@Valid @RequestBody CreateQuestionBody createQuestionBody) {
        return questionService.createQuestion(createQuestionBody);
    }

    /**
     * 管理端：批量创建问题
     *
     * 行为：
     * - 接收批量创建请求，Service 层负责批量插入、去重与事务控制。
     *
     * @param createQuestionBatchBody 批量创建请求体
     * @return ApiResponse<EmptyVO> 表示批量创建结果
     */
    @PostMapping("/admin/questions/batch")
    public ApiResponse<EmptyVO> createQuestions(@RequestBody CreateQuestionBatchBody createQuestionBatchBody) {
        return questionService.createQuestionBatch(createQuestionBatchBody);
    }

    /**
     * 管理端：更新问题
     *
     * 行为：
     * - 对指定 questionId 执行部分更新，使用 UpdateQuestionBody 指定要修改的字段；
     * - 使用 @Min 校验路径参数并使用 @Valid 校验请求体。
     *
     * @param questionId 问题 ID（正整数）
     * @param updateQuestionBody 更新内容
     * @return ApiResponse<EmptyVO> 表示更新结果
     */
    @PatchMapping("/admin/questions/{questionId}")
    public ApiResponse<EmptyVO> updateQuestion(@Min(value = 1, message = "questionId 必须为正整数")
                                               @PathVariable Integer questionId,
                                               @Valid @RequestBody UpdateQuestionBody updateQuestionBody) {
        return questionService.updateQuestion(questionId, updateQuestionBody);
    }

    /**
     * 管理端：删除问题
     *
     * 行为：
     * - 根据 questionId 删除问题，Service 层负责权限校验与级联清理（如关联笔记、评论等）。
     *
     * @param questionId 问题 ID（正整数）
     * @return ApiResponse<EmptyVO> 表示删除结果
     */
    @DeleteMapping("/admin/questions/{questionId}")
    public ApiResponse<EmptyVO> deleteQuestion(@Min(value = 1, message = "questionId 必须为正整数")
                                               @PathVariable Integer questionId) {
        return questionService.deleteQuestion(questionId);
    }
}
