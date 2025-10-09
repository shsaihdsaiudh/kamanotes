package com.kama.notes.utils;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * MarkdownAST
 *
 * 用于解析并从 Markdown 文本中提取常用信息（简介、图片、是否需要收起等）的工具类。
 *
 * 设计说明：
 * - 使用 flexmark 解析 Markdown 文本生成 AST（抽象语法树），再遍历 AST 提取需要的内容；
 * - 类对外暴露的都是与文本处理相关的纯计算方法，不依赖 Web 环境或持久层；
 * - 返回的字符串尽量为纯文本片段，便于在前端展示简介/预览。
 *
 * 注意：
 * - 该类将整个 Markdown 文本解析为 AST 并保存在实例中，适合对同一文本进行多次提取；
 * - getNodeText 使用递归遍历节点并拼接子节点文本，能处理常见的文本、强调、链接等节点类型；
 * - 如果需要更精确的渲染（保留 Markdown 语法或 HTML），应改用 renderer 而非简单文本拼接。
 */
@Getter
public class MarkdownAST {
    private final Document markdownAST;
    private final String markdownText;

    /**
     * 构造函数：解析并保存 Markdown 文本的 AST。
     *
     * @param markdownText 原始 Markdown 文本（允许为空字符串）
     */
    public MarkdownAST(String markdownText) {
        this.markdownText = markdownText == null ? "" : markdownText;

        // 创建解析器实例并解析 Markdown 文本生成 AST
        Parser parser = Parser.builder().build();
        this.markdownAST = parser.parse(this.markdownText);
    }

    /**
     * 提取简介：遍历顶层节点，收集 Heading 和 Paragraph 的文本内容，最终限制为 maxChars 长度。
     *
     * 说明：
     * - 只遍历 Document 的直接子节点（不在段落内部进一步拼接换行符等格式）；
     * - 返回的文本末尾会加上 "..." 作为省略标记（若文本较短也会追加，调用方可根据需要调整）。
     *
     * @param maxChars 简介最大字符数
     * @return 截断后的纯文本简介（末尾带省略号）
     */
    public String extractIntroduction(int maxChars) {
        StringBuilder introText = new StringBuilder();
        for (Node node : markdownAST.getChildren()) {
            if (node instanceof Heading || node instanceof Paragraph) {
                String renderedText = getNodeText(node);

                int remainingChars = maxChars - introText.length();
                if (remainingChars <= 0) break;

                introText.append(renderedText, 0, Math.min(remainingChars, renderedText.length()));

                if (introText.length() >= maxChars) {
                    break;
                }
            }
        }

        return introText.toString().trim() + "...";
    }

    /**
     * 检查并返回文档中所有图片的 URL 列表。
     *
     * @return 图片地址列表（按出现顺序），若无图片返回空列表
     */
    public List<String> extractImages() {
        List<String> imageUrls = new ArrayList<>();

        // 只遍历顶层子节点中的 Image 节点；复杂场景可改为深度优先遍历整棵树
        for (Node node : markdownAST.getChildren()) {
            if (node instanceof Image imageNode) {
                imageUrls.add(imageNode.getUrl().toString());
            }
        }

        return imageUrls;
    }

    /**
     * 判断文本是否需要在列表/卡片中收起（折叠）。
     *
     * 判断规则示例：
     * - 包含图片则返回 true；
     * - 或原始 Markdown 字符数超过 maxChars 则返回 true。
     *
     * @param maxChars 折叠阈值字符数
     * @return 是否需要折叠
     */
    public boolean shouldCollapse(int maxChars) {
        return hasImages() || markdownText.length() > maxChars;
    }

    /**
     * 获取精简后的 Markdown 文本（当前实现基于 extractIntroduction 的结果并追加省略号）。
     *
     * @return 精简后的纯文本简介
     */
    public String getCollapsedMarkdown() {
        String introText = extractIntroduction(150);
        return introText + "...";
    }

    /**
     * 递归获取节点的纯文本内容（拼接子节点文本）。
     *
     * 说明：
     * - 该方法旨在提取节点可见文本，忽略 Markdown 的格式信息（如 **、_ 等）；
     * - 对于某些节点（如链接、代码块、表格）可能需要特殊处理，当前实现作通用拼接；
     *
     * @param node 要提取文本的节点
     * @return 节点及其子节点的文本内容
     */
    private String getNodeText(Node node) {
        StringBuilder text = new StringBuilder();

        // 处理 Text 节点（最常见的叶子节点）
        if (node instanceof Text) {
            text.append(((Text) node).getChars());
        }

        // 递归拼接所有子节点的文本
        for (Node child : node.getChildren()) {
            text.append(getNodeText(child));
        }
        return text.toString();
    }

    /**
     * 获取 Heading 节点的文本内容（便捷方法）。
     *
     * @param headingNode Heading 节点
     * @return Heading 的纯文本（去首尾空白）
     */
    public String getHeadingText(Heading headingNode) {
        return headingNode.getText().toString().trim();
    }

    /**
     * 提取 ListItem 节点的文本（适用于无嵌套复杂结构的简单列表项）。
     *
     * @param listItem 列表项节点
     * @return 列表项的拼接文本（去首尾空白）
     */
    public String getListItemText(ListItem listItem) {
        StringBuilder sb = new StringBuilder();
        for (Node node = listItem.getFirstChild(); node != null; node = node.getNext()) {
            sb.append(node.getChars().toString());
        }
        return sb.toString().trim();
    }

    /**
     * 判断文档是否包含图片（内部使用）。
     *
     * @return 包含图片返回 true，否则 false
     */
    private boolean hasImages() {
        return !extractImages().isEmpty();
    }
}
```// filepath: c:\Users\yangy\Desktop\卡玛笔记\kamanotes\backend\src\main\java\com\kama\notes\utils\MarkdownAST.java
// ...existing code...
package com.kama.notes.utils;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * MarkdownAST
 *
 * 用于解析并从 Markdown 文本中提取常用信息（简介、图片、是否需要收起等）的工具类。
 *
 * 设计说明：
 * - 使用 flexmark 解析 Markdown 文本生成 AST（抽象语法树），再遍历 AST 提取需要的内容；
 * - 类对外暴露的都是与文本处理相关的纯计算方法，不依赖 Web 环境或持久层；
 * - 返回的字符串尽量为纯文本片段，便于在前端展示简介/预览。
 *
 * 注意：
 * - 该类将整个 Markdown 文本解析为 AST 并保存在实例中，适合对同一文本进行多次提取；
 * - getNodeText 使用递归遍历节点并拼接子节点文本，能处理常见的文本、强调、链接等节点类型；
 * - 如果需要更精确的渲染（保留 Markdown 语法或 HTML），应改用 renderer 而非简单文本拼接。
 */
@Getter
public class MarkdownAST {
    private final Document markdownAST;
    private final String markdownText;

    /**
     * 构造函数：解析并保存 Markdown 文本的 AST。
     *
     * @param markdownText 原始 Markdown 文本（允许为空字符串）
     */
    public MarkdownAST(String markdownText) {
        this.markdownText = markdownText == null ? "" : markdownText;

        // 创建解析器实例并解析 Markdown 文本生成 AST
        Parser parser = Parser.builder().build();
        this.markdownAST = parser.parse(this.markdownText);
    }

    /**
     * 提取简介：遍历顶层节点，收集 Heading 和 Paragraph 的文本内容，最终限制为 maxChars 长度。
     *
     * 说明：
     * - 只遍历 Document 的直接子节点（不在段落内部进一步拼接换行符等格式）；
     * - 返回的文本末尾会加上 "..." 作为省略标记（若文本较短也会追加，调用方可根据需要调整）。
     *
     * @param maxChars 简介最大字符数
     * @return 截断后的纯文本简介（末尾带省略号）
     */
    public String extractIntroduction(int maxChars) {
        StringBuilder introText = new StringBuilder();
        for (Node node : markdownAST.getChildren()) {
            if (node instanceof Heading || node instanceof Paragraph) {
                String renderedText = getNodeText(node);

                int remainingChars = maxChars - introText.length();
                if (remainingChars <= 0) break;

                introText.append(renderedText, 0, Math.min(remainingChars, renderedText.length()));

                if (introText.length() >= maxChars) {
                    break;
                }
            }
        }

        return introText.toString().trim() + "...";
    }

    /**
     * 检查并返回文档中所有图片的 URL 列表。
     *
     * @return 图片地址列表（按出现顺序），若无图片返回空列表
     */
    public List<String> extractImages() {
        List<String> imageUrls = new ArrayList<>();

        // 只遍历顶层子节点中的 Image 节点；复杂场景可改为深度优先遍历整棵树
        for (Node node : markdownAST.getChildren()) {
            if (node instanceof Image imageNode) {
                imageUrls.add(imageNode.getUrl().toString());
            }
        }

        return imageUrls;
    }

    /**
     * 判断文本是否需要在列表/卡片中收起（折叠）。
     *
     * 判断规则示例：
     * - 包含图片则返回 true；
     * - 或原始 Markdown 字符数超过 maxChars 则返回 true。
     *
     * @param maxChars 折叠阈值字符数
     * @return 是否需要折叠
     */
    public boolean shouldCollapse(int maxChars) {
        return hasImages() || markdownText.length() > maxChars;
    }

    /**
     * 获取精简后的 Markdown 文本（当前实现基于 extractIntroduction 的结果并追加省略号）。
     *
     * @return 精简后的纯文本简介
     */
    public String getCollapsedMarkdown() {
        String introText = extractIntroduction(150);
        return introText + "...";
    }

    /**
     * 递归获取节点的纯文本内容（拼接子节点文本）。
     *
     * 说明：
     * - 该方法旨在提取节点可见文本，忽略 Markdown 的格式信息（如 **、_ 等）；
     * - 对于某些节点（如链接、代码块、表格）可能需要特殊处理，当前实现作通用拼接；
     *
     * @param node 要提取文本的节点
     * @return 节点及其子节点的文本内容
     */
    private String getNodeText(Node node) {
        StringBuilder text = new StringBuilder();

        // 处理 Text 节点（最常见的叶子节点）
        if (node instanceof Text) {
            text.append(((Text) node).getChars());
        }

        // 递归拼接所有子节点的文本
        for (Node child : node.getChildren()) {
            text.append(getNodeText(child));
        }
        return text.toString();
    }

    /**
     * 获取 Heading 节点的文本内容（便捷方法）。
     *
     * @param headingNode Heading 节点
     * @return Heading 的纯文本（去首尾空白）
     */
    public String getHeadingText(Heading headingNode) {
        return headingNode.getText().toString().trim();
    }

    /**
     * 提取 ListItem 节点的文本（适用于无嵌套复杂结构的简单列表项）。
     *
     * @param listItem 列表项节点
     * @return 列表项的拼接文本（去首尾空白）
     */
    public String getListItemText(ListItem listItem) {
        StringBuilder sb = new StringBuilder();
        for (Node node = listItem.getFirstChild(); node != null; node = node.getNext()) {
            sb.append(node.getChars().toString());
        }
        return sb.toString().trim();
    }

    /**
     * 判断文档是否包含图片（内部使用）。
     *
     * @return 包含图片返回 true，否则 false
     */
    private boolean hasImages() {
        return !extractImages().isEmpty();
    }
}
