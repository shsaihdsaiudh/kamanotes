package com.kama.notes.mapper;

import com.kama.notes.model.entity.CollectionNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * CollectionNoteMapper
 *
 * 收藏夹与笔记关联表（collection_note） 的 MyBatis 映射接口。
 *
 * 职责：
 * - 提供对收藏夹与笔记关系的查询、插入与删除操作；
 * - 接口方法应尽可能保持单一职责，复杂的业务逻辑（事务、权限校验、级联删除策略）应在 Service 层实现；
 *
 * 使用与注意事项：
 * - 对于批量操作（如批量删除/插入），请在 Service 层控制事务并避免在单条方法中执行大量循环插入，必要时使用批量 SQL；
 * - 查询返回的集合类型（List / Set）应与 Mapper XML 中的 resultType/collection 配置一致；
 * - 方法参数使用 @Param 明确命名，便于在 XML 中引用并提高可读性。
 */
@Mapper
public interface CollectionNoteMapper {
    /**
     * 查询用户收藏的笔记 ID 列表
     *
     * @param userId  用户 ID
     * @param noteIds 笔记 ID 列表
     * @return 用户收藏的笔记 ID 列表
     */
    List<Integer> findUserCollectedNoteIds(
            @Param("userId") Long userId,
            @Param("noteIds") List<Integer> noteIds
    );

    /**
     * 筛选出所给的收藏夹 ID 列表中，收藏了 noteId 对应的 note 的记录
     *
     * @param noteId        笔记 ID
     * @param collectionIds 收藏夹 ID 列表
     * @return 筛选结果
     */
    Set<Integer> filterCollectionIdsByNoteId(
            @Param("noteId") Integer noteId,
            @Param("collectionIds") List<Integer> collectionIds);

    /**
     * 插入记录
     *
     * @param collectionNote 收藏笔记记录
     * @return 插入记录数
     */
    int insert(CollectionNote collectionNote);

    /**
     * 根据 collectionId 删除记录
     *
     * @param collectionId 收藏夹 ID
     * @return 删除记录数
     */
    int deleteByCollectionId(@Param("collectionId") Integer collectionId);

    /**
     * 根据 collectionId 和 noteId 删除记录
     *
     * @param collectionId 收藏夹 ID
     * @param noteId       笔记 ID
     * @return 删除记录数
     */
    int deleteByCollectionIdAndNoteId(
            @Param("collectionId") Integer collectionId,
            @Param("noteId") Integer noteId);
}
