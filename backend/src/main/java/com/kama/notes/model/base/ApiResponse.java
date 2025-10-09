package com.kama.notes.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse
 *
 * 统一的 API 响应封装类。
 *
 * 设计说明：
 * - 用于 Controller 层对外返回统一的响应格式：code / message / data；
 * - 提供静态工厂方法方便构建成功或失败的响应；
 * - 使用泛型支持任意类型的响应数据。
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {
    /**
     * 响应码（例如 200 表示成功，其他值表示不同的错误类型）
     */
    private int code;

    /**
     * 响应消息（用于描述操作结果或错误详情）
     */
    private String message;

    /**
     * 响应数据（可为具体对象、列表或空对象）
     */
    private T data;

    /**
     * 全参构造函数
     *
     * @param code 响应码
     * @param message 响应消息
     * @param data 响应数据
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T> 响应数据类型
     * @return 构造好的成功 ApiResponse（code = 200, message = "success"）
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 创建成功响应（无数据）
     *
     * @return 不携带业务数据的成功响应（data = EmptyVO）
     */
    public static ApiResponse<EmptyVO> success() {
        return success(new EmptyVO());
    }

    /**
     * 创建错误响应（无数据）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 响应数据类型
     * @return 构造好的错误 ApiResponse（data 为 null）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建错误响应（带错误数据）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param data 错误相关数据（可选）
     * @param <T> 响应数据类型
     * @return 构造好的错误 ApiResponse（包含 data）
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}