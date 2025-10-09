package com.kama.notes.event;

import com.kama.notes.model.vo.message.MessageVO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * MessageEvent
 *
 * 系统内部用于消息传递的事件对象，继承自 Spring 的 ApplicationEvent。
 *
 * 设计要点：
 * - 不可变（final 字段），事件创建后内容不应修改，便于在异步/多线程监听器中安全传递；
 * - 包含三部分核心信息：
 *   1. message：要传递的消息载体（MessageVO），包含消息内容、来源等视图层所需信息；
 *   2. receiverId：接收者的用户 ID，便于在监听器中过滤或路由到目标用户；
 *   3. eventType：事件类型字符串（例如 "COMMENT"、"LIKE"、"SYSTEM"），便于监听器按类型分流处理；
 * - 推荐通过静态工厂方法创建不同类型的事件，避免直接使用构造器从而统一 eventType 值。
 *
 * 使用建议：
 * - 在异步监听器中处理消息时，请注意 MessageVO 内部若包含懒加载的实体（如 JPA 代理），
 *   可能需要在事件发布前将必要字段转换为纯 VO 数据以避免延迟加载问题；
 * - 事件发布者（ApplicationEventPublisher.publishEvent）可在事务提交后发布以保证数据一致性，
 *   也可以在事务内发布（则监听器应能处理回滚场景）。
 */
@Getter
public class MessageEvent extends ApplicationEvent {

    /**
     * 要传递的消息载体（视图对象），建议为纯数据对象，不包含数据库代理或可变全局状态。
     */
    private final MessageVO message;

    /**
     * 接收消息的用户 ID，监听器可根据此字段将消息路由到特定用户或队列。
     */
    private final Long receiverId;

    /**
     * 事件类型，用于在监听器中进行分发或策略选择（示例：COMMENT, LIKE, SYSTEM）。
     */
    private final String eventType;

    /**
     * 构造器：内部使用，推荐使用静态工厂方法创建具体类型的事件。
     *
     * @param source     事件源（通常为发布者对象）
     * @param message    消息视图对象
     * @param receiverId 接收者用户 ID
     * @param eventType  事件类型标识
     */
    public MessageEvent(Object source, MessageVO message, Long receiverId, String eventType) {
        super(source);
        this.message = message;
        this.receiverId = receiverId;
        this.eventType = eventType;
    }

    /**
     * 创建评论类型的消息事件。
     *
     * @param source     事件源
     * @param message    消息内容
     * @param receiverId 接收者用户 ID
     * @return MessageEvent 实例，eventType 为 "COMMENT"
     */
    public static MessageEvent createCommentEvent(Object source, MessageVO message, Long receiverId) {
        return new MessageEvent(source, message, receiverId, "COMMENT");
    }

    /**
     * 创建点赞类型的消息事件。
     *
     * @param source     事件源
     * @param message    消息内容
     * @param receiverId 接收者用户 ID
     * @return MessageEvent 实例，eventType 为 "LIKE"
     */
    public static MessageEvent createLikeEvent(Object source, MessageVO message, Long receiverId) {
        return new MessageEvent(source, message, receiverId, "LIKE");
    }

    /**
     * 创建系统通知类型的消息事件（系统广播或后台通知）。
     *
     * @param source     事件源
     * @param message    消息内容
     * @param receiverId 接收者用户 ID
     * @return MessageEvent 实例，eventType 为 "SYSTEM"
     */
    public static MessageEvent createSystemEvent(Object source, MessageVO message, Long receiverId) {
        return new MessageEvent(source, message, receiverId, "SYSTEM");
    }
} 