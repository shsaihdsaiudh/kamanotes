package com.kama.notes.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchUtils
 *
 * 搜索相关的工具类，提供搜索关键词的预处理与分页偏移量计算。
 *
 * 主要功能：
 * - preprocessKeyword：对原始搜索关键字做清洗与分词，返回适合查询的词串（空格分隔）；
 *   步骤包括去除标点/符号、分词并用空格连接分词结果，适用于全文检索/搜索引擎/数据库 like 分词组合等场景。
 * - calculateOffset：根据页码与每页大小计算 SQL/查询的偏移量（offset），保证非负。
 *
 * 注意：
 * - 分词使用的是 JiebaSegmenter（中文分词），依赖第三方库；在非中文或不同分词策略的场景应调整实现；
 * - preprocessKeyword 会把所有标点/符号替换为空格，再交由分词器处理，避免分词器将标点当作词元；
 * - 对输入为空或仅空白字符串会返回空串。
 */
public class SearchUtils {
    // Jieba 分词器实例（线程安全性取决于实现，此处作为单例复用）
    private static final JiebaSegmenter segmenter = new JiebaSegmenter();

    /**
     * 预处理搜索关键词
     *
     * 处理流程：
     * 1. 若 keyword 为空或仅空白，返回空字符串；
     * 2. 将标点符号和常见特殊字符替换为空格，避免干扰分词；
     * 3. 使用 Jieba 进行分词；
     * 4. 将分词结果以单个空格连接后返回，方便用于基于空格分词的检索（如 MATCH/AGAINST、全文索引或多关键词 like）。
     *
     * 示例：
     * 输入 "你好，世界！Java-编程" -> 去符号后 "你好 世界 Java 编程" -> 分词后 "你好 世界 Java 编程"
     *
     * @param keyword 原始关键词
     * @return 处理后的关键词（空格分隔），若输入为空则返回空字符串
     */
    public static String preprocessKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "";
        }

        // 1. 去除特殊字符（标点、符号等），统一替换为空格
        keyword = keyword.replaceAll("[\\p{P}\\p{S}]", " ");

        // 2. 分词（Jieba 返回分词列表）
        List<String> words = segmenter.sentenceProcess(keyword);

        // 3. 组合搜索词：用单个空格连接，去除可能的空项
        return words.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(" "));
    }

    /**
     * 计算分页的偏移量（offset）
     *
     * 说明：
     * - page 从 1 开始；若 page 为 1，则 offset 为 0；
     * - 返回值保证非负（使用 Math.max 防止负值）。
     *
     * @param page 页码（从 1 开始）
     * @param pageSize 每页大小
     * @return 计算后的偏移量（>= 0）
     */
    public static int calculateOffset(int page, int pageSize) {
        return Math.max(0, (page - 1) * pageSize);
    }
}