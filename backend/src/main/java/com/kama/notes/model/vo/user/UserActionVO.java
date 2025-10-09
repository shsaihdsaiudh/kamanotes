package com.kama.notes.model.vo.user;

import lombok.Data;

/**
 * UserActionVO
 *
 * 表示当前登录用户对某项资源（例如笔记、评论）的操作状态，用于前端展示交互按钮的开关状态。
 *
 * 说明：
 * - 该类为展示层 VO，不包含业务逻辑或持久化注解；
 * - 字段使用包装类型 Boolean，以便能表达未知/未登录（null）状态，与 true/false 区分开来。
 */
@Data
public class UserActionVO {
    /**
     * 当前用户是否对该对象已点赞。
     *
     * 取值含义：
     * - true：已点赞
     * - false：未点赞
     * - null：未知（例如用户未登录或未执行查询）
     */
    private Boolean isLiked;
}