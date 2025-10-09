package com.kama.notes.model.dto.question;

import lombok.Data;

/**
 * CreateQuestionBatchBody
 *
 * 批量创建问题的请求 DTO。
 *
 * 用途：
 * - 接收前端提交的一段 Markdown 文本，服务端负责将该文本解析并拆分为多条问题后入库；
 *
 * 说明：
 * - markdown 字段包含待解析的原始 Markdown 内容；
 * - 建议在 Controller/Service 层做必要的格式与长度校验（例如 @NotBlank 或 @Size），并处理解析错误与去重逻辑。
 */
@Data
public class CreateQuestionBatchBody {
    /**
     * 待解析并批量创建问题的 Markdown 文本
     */
    private String markdown;
}
