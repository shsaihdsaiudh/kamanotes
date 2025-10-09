package com.kama.notes.model.base;

/**
 * PaginationApiResponse
 *
 * 分页 API 的响应封装，继承自 ApiResponse，并额外携带分页元信息（Pagination）。
 *
 * 说明：
 * - 用于返回带分页信息的接口响应，例如列表查询时同时返回数据列表与分页元信息；
 * - 通过继承 ApiResponse 保持响应结构一致（code/message/data），并在此基础上增加 pagination 字段；
 * - 若需序列化（JSON）返回给前端，确保序列化库能正确处理父类的字段与此类的 pagination 字段。
 *
 * @param <T> 响应数据类型
 */
public class PaginationApiResponse<T> extends ApiResponse<T> {
    /**
     * 分页信息（页码、每页大小、总记录数等）
     */
    private final Pagination pagination;

    /**
     * 构造函数
     *
     * @param code       响应码（例如 200 表示成功）
     * @param msg        响应消息文本
     * @param data       响应数据，类型为泛型 T
     * @param pagination 分页元信息对象
     */
    public PaginationApiResponse(int code, String msg, T data, Pagination pagination) {
        super(code, msg, data);
        this.pagination = pagination;
    }

    /**
     * 获取分页信息
     *
     * @return Pagination 对象，包含 page、pageSize、total 等字段
     */
    public Pagination getPagination() {
        return pagination;
    }
}
