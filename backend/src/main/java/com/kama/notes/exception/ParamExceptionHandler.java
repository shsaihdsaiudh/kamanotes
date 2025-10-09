package com.kama.notes.exception;

import com.kama.notes.model.base.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * ParamExceptionHandler
 *
 * 全局参数与校验异常处理器（RestControllerAdvice）。
 *
 * 主要职责：
 * - 捕获并处理控制器层抛出的参数校验相关异常，统一返回 ApiResponse 结构；
 * - 将校验失败的字段和错误信息以 Map 形式返回，HTTP 状态使用 400(BAD_REQUEST)；
 * - 对未预期的异常统一返回 500(INTERNAL_SERVER_ERROR) 的 ApiResponse。
 *
 * 注意事项：
 * - MethodArgumentNotValidException：处理 @Valid 注解导致的对象字段校验错误（表单/JSON 请求体）；
 * - ConstraintViolationException：处理方法参数级别（如 @RequestParam/@PathVariable/@Min 等）校验错误；
 * - 对于更细粒度的错误码或国际化消息，可在此处或在 ApiResponse 中进行扩展处理。
 */
@RestControllerAdvice
public class ParamExceptionHandler {

    /**
     * 处理 @Valid 校验失败（请求体绑定）的异常。
     *
     * 返回值示例：
     * {
     *   "code": 400,
     *   "message": "Validation Failed",
     *   "data": { "fieldName": "错误信息", ... }
     * }
     *
     * @param ex MethodArgumentNotValidException
     * @return 包含字段级错误信息的 ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // 收集每个字段的错误信息
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
    }

    /**
     * 处理参数约束校验异常（如方法参数上的 @Min/@Pattern 等）。
     *
     * 返回 data 为 propertyPath -> message 的映射，方便前端定位错误参数。
     *
     * @param ex ConstraintViolationException
     * @return 包含参数级错误信息的 ApiResponse
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
    }

    /**
     * 通用异常处理：捕获未被专门处理的异常，避免将内部栈信息直接暴露给客户端。
     *
     * 返回 500 状态及异常消息（简化信息），具体异常应在日志中记录供排查使用。
     *
     * @param ex Exception 未处理的异常
     * @return 包含异常简要信息的 ApiResponse
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage());
    }
}
