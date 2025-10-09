package com.kama.notes.utils;

/**
 * MarkdownUtil
 *
 * 简单的 Markdown 辅助工具类，基于 MarkdownAST 提供常用的文本处理能力：
 * - needCollapsed：判断是否需要在展示时折叠（收起）长文本或包含图片的内容；
 * - extractIntroduction：从 Markdown 中提取简短的纯文本简介，便于列表或卡片展示。
 *
 * 说明：
 * - 两个方法内部都通过构造 MarkdownAST 来复用解析逻辑；
 * - 若频繁处理同一篇 Markdown，建议复用 MarkdownAST 的实例以避免重复解析开销。
 */
public class MarkdownUtil {

    /**
     * 判断给定的 markdown 文本在展示时是否需要折叠。
     *
     * 判定规则由 MarkdownAST.shouldCollapse 决定（当前阈值为 250 字符）。
     *
     * @param markdown 原始 Markdown 文本（可为 null）
     * @return 需要折叠返回 true，否则返回 false
     */
    public static boolean needCollapsed(String markdown) {
        MarkdownAST ast = new MarkdownAST(markdown);
        return ast.shouldCollapse(250);
    }

    /**
     * 从 markdown 文本中提取一段简短的纯文本简介，用于列表或卡片的预览展示。
     *
     * 目前提取规则基于 MarkdownAST.extractIntroduction，长度限制为 250 字符。
     *
     * @param markdown 原始 Markdown 文本（可为 null）
     * @return 提取出的简介字符串（末尾可能带省略号）
     */
    public static String extractIntroduction(String markdown) {
        MarkdownAST ast = new MarkdownAST(markdown);
        return ast.extractIntroduction(250);
    }
}