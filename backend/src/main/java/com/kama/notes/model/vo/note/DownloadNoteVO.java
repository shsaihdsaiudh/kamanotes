package com.kama.notes.model.vo.note;

import lombok.Data;

/**
 * DownloadNoteVO
 *
 * 用于笔记导出/下载的视图对象，封装要写入文件或返回给客户端的 Markdown 文本内容。
 *
 * 用法说明：
 * - 在导出接口中返回该 VO，前端或客户端可将 markdown 字符串保存为 .md 文件；
 * - 建议在返回前对内容长度与敏感信息进行校验/过滤；若文本非常大，考虑使用流式下载或分页导出以避免 OOM。
 */
@Data
public class DownloadNoteVO {
    /**
     * 待下载的 Markdown 文本（UTF-8 编码）
     */
    private String markdown;
}
