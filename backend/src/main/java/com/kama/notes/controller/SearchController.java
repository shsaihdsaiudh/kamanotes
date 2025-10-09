package com.kama.notes.controller;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.entity.Note;
import com.kama.notes.model.entity.User;
import com.kama.notes.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * SearchController
 *
 * 搜索相关 REST 控制器，提供对笔记和用户的简单搜索接口。
 *
 * 说明：
 * - 返回统一使用 ApiResponse 包装结果，便于前端统一处理状态与消息；
 * - 基础分页参数采用 page、pageSize，均要求最小为 1；
 * - 搜索关键字通过 request 参数传入，Service 层负责具体查询逻辑与性能优化（如全文索引、分词、权重等）。
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 搜索笔记
     *
     * 参数：
     * - keyword: 搜索关键字（必传）；
     * - page: 页码，默认 1，最小为 1；
     * - pageSize: 每页大小，默认 20，最小为 1。
     *
     * 返回：
     * - ApiResponse.data 为符合条件的 Note 列表（具体分页信息由 Service 控制）。
     */
    @GetMapping("/notes")
    public ApiResponse<List<Note>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return searchService.searchNotes(keyword, page, pageSize);
    }

    /**
     * 搜索用户
     *
     * 参数与返回格式与 searchNotes 类似：
     * - keyword: 搜索关键字（必传）；
     * - page / pageSize: 分页参数。
     *
     * 返回：
     * - ApiResponse.data 为符合条件的 User 列表。
     */
    @GetMapping("/users")
    public ApiResponse<List<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return searchService.searchUsers(keyword, page, pageSize);
    }

    /**
     * 按标签和关键字搜索笔记
     *
     * 用途：
     * - 在指定 tag 下进一步根据 keyword 搜索笔记，便于前端实现标签内搜索功能。
     *
     * 参数：
     * - keyword: 搜索关键字；
     * - tag: 目标标签；
     * - page / pageSize: 分页参数。
     *
     * 返回：
     * - ApiResponse.data 为符合 tag 与 keyword 条件的 Note 列表。
     */
    @GetMapping("/notes/tag")
    public ApiResponse<List<Note>> searchNotesByTag(
            @RequestParam String keyword,
            @RequestParam String tag,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return searchService.searchNotesByTag(keyword, tag, page, pageSize);
    }
} 