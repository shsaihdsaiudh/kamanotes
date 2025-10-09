package com.kama.notes.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.dto.statistic.StatisticQueryParam;
import com.kama.notes.model.entity.Statistic;
import com.kama.notes.service.StatisticService;

/**
 * StatisticController
 *
 * 统计相关接口控制器：
 * - 提供对统计数据的查询接口（/api/statistic）。
 *
 * 设计要点：
 * - 控制器负责路由与参数校验（使用 @Valid），实际业务逻辑由 StatisticService 实现；
 * - 返回统一使用 ApiResponse<List<Statistic>>，便于前端统一处理状态与消息；
 * - 若查询可能返回大量数据，建议在 Service 层支持分页或聚合查询以保证性能。
 */
@RestController
@RequestMapping("/api")
public class StatisticController {

    @Autowired
    StatisticService statisticService;

    /**
     * 获取统计数据列表。
     *
     * 行为：
     * - 接收并校验 StatisticQueryParam（例如时间范围、类型等过滤条件）；
     * - 委托 StatisticService 执行查询并返回结果。
     *
     * 注意：
     * - 若查询维度较多或数据量大，建议在 StatisticService 做分页/聚合优化或使用异步/缓存方案。
     *
     * @param queryParam 查询参数（由 StatisticQueryParam 定义），使用 @Valid 验证
     * @return ApiResponse 包含 Statistic 列表
     */
    @GetMapping("/statistic")
    public ApiResponse<List<Statistic>> getStatistic(@Valid StatisticQueryParam queryParam) {
        return statisticService.getStatistic(queryParam);
    }
}
