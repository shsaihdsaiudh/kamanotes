package com.kama.notes.model.entity;

import lombok.Data;
import java.util.Date;

/**
 * CollectionNote
 *
 * 收藏夹与笔记关联实体类，对应数据库中的 collection_note 表（或关联表）。
 *
 * 说明：
 * - 用于表示某个笔记被加入到某个收藏夹的关系（通常 collectionId + noteId 为联合主键）；
 * - 包含创建时间与更新时间，便于审计与清理过期/历史记录；
 * - 该类为持久层实体，用于 Mapper/Repository 层与业务层数据传递。
 *
 * 作者与版本信息保留于类注释中（不影响功能）。
 */
@Data
public class CollectionNote {
    /**
     * 收藏夹 ID（联合主键的一部分）
     */
    private Integer collectionId;

    /**
     * 笔记 ID（联合主键的一部分）
     */
    private Integer noteId;

    /**
     * 记录创建时间
     */
    private Date createdAt;

    /**
     * 记录最后更新时间
     */
    private Date updatedAt;
}
