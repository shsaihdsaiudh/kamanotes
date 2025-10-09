package com.kama.notes.model.vo.collection;

import lombok.Data;

/**
 * CollectionVO
 *
 * 收藏夹视图对象，用于对外接口返回收藏夹相关信息。
 *
 * 字段说明：
 * - collectionId: 收藏夹主键 ID；
 * - name: 收藏夹名称，供前端展示；
 * - description: 收藏夹描述/备注；
 * - noteStatus: 可选字段，当查询时携带 noteId 参数用以表示该笔记在该收藏夹中的状态
 *   （例如用于在笔记详情页判断当前用户是否已将该笔记加入该收藏夹）。
 *
 * 设计要点：
 * - 该类为纯展示层 VO，不包含持久化或业务逻辑；
 * - noteStatus 为嵌套类，便于在同一响应中同时返回收藏夹信息和与某个笔记的关联状态。
 */
@Data
public class CollectionVO {
    /**
     * 收藏夹 ID
     */
    private Integer collectionId;

    /**
     * 收藏夹名称
     */
    private String name;

    /**
     * 收藏夹描述
     */
    private String description;

    /**
     * 查询收藏夹时，可能会携带的 noteId 参数，这个 noteStatus 可以用来判断该 note 是否被收藏
     */
    private NoteStatus noteStatus;

    /**
     * NoteStatus
     *
     * 嵌套视图对象，用于表示某个笔记在该收藏夹中的状态。
     *
     * 字段：
     * - noteId: 对应的笔记 ID；
     * - isCollected: 是否已收藏（true 表示已收藏，false 或 null 表示未收藏或未知）。
     */
    @Data
    public static class NoteStatus {
        /**
         * 笔记 ID
         */
        private Integer noteId;

        /**
         * 是否已收藏（true = 已收藏）
         */
        private Boolean isCollected;
    }
}
