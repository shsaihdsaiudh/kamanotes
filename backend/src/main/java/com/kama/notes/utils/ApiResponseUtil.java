package com.kama.notes.utils;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.Pagination;
import com.kama.notes.model.base.PaginationApiResponse;
import com.kama.notes.model.base.TokenApiResponse;
import org.springframework.http.HttpStatus;

/**
 * ApiResponseUtil
 *
 * 快速构造统一响应对象的工具类，简化 Controller / Service 层返回 ApiResponse 的代码。
 *
 * 设计说明：
 * - 统一使用 ApiResponse、TokenApiResponse、PaginationApiResponse 等封装响应结构；
 * - 提供常见的 success / error 构造方法，便于在业务层快速返回标准化结果；
 * - 注意：部分方法传入的 message 参数当前未被使用（保持与现有 ApiResponse.success 方法签名一致），
 *   如需返回自定义 message 可在 ApiResponse 类或这里做调整。
 */
public class ApiResponseUtil {
    /**
     * 构建成功的响应（无数据）。
     *
     * @param message 响应消息（当前实现未将该参数透传到返回对象）
     * @param <T>     返回数据类型
     * @return ApiResponse，data 为 null，状态为 success（由 ApiResponse.success 实现决定）
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.success(null);
    }

    /**
     * 构建成功的响应（包含数据）。
     *
     * @param message 响应消息（当前实现未将该参数透传到返回对象）
     * @param data    返回的数据对象
     * @param <T>     返回数据类型
     * @return ApiResponse，data 为传入的 data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(data);
    }

    /**
     * 构建参数错误 / 通用错误响应，默认使用 HTTP 400 状态码。
     *
     * @param msg 错误消息文本
     * @param <T> 返回数据类型
     * @return ApiResponse，封装错误码与消息
     */
    public static <T> ApiResponse<T> error(String msg) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 构建包含 token 的成功响应（用于登录/续期等场景）。
     *
     * @param msg   响应消息
     * @param data  返回的数据对象
     * @param token 返回的 token 字符串
     * @param <T>   返回数据类型
     * @return TokenApiResponse，包含 data 与 token
     */
    public static <T> TokenApiResponse<T> success(String msg, T data, String token) {
        return new TokenApiResponse<>(HttpStatus.OK.value(), msg, data, token);
    }

    /**
     * 构建分页查询的成功响应。
     *
     * @param msg        响应消息
     * @param data       分页返回的数据（通常为 List<T>）
     * @param pagination 分页信息（当前页、总数、页大小等）
     * @param <T>        返回数据类型
     * @return PaginationApiResponse，包含 data 与 pagination 元信息
     */
    public static <T> PaginationApiResponse<T> success(String msg, T data, Pagination pagination) {
        return new PaginationApiResponse<>(HttpStatus.OK.value(), msg, data, pagination);
    }
}
