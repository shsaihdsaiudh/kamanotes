package com.kama.notes.mapper;

import com.kama.notes.model.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CollectionMapper
 *
 * MyBatis 映射接口，负责收藏夹（Collection）相关的数据库访问操作。
 *
 * 说明：
 * - Mapper 方法由对应的 XML 或注解 SQL 提供实现；
 * - 接口仅负责与数据库交互，事务与业务逻辑应在 Service 层处理；
 * - 方法参数使用 @Param 明确映射到 SQL 中的命名参数，便于编写对应的 Mapper XML。
 */
@Mapper
public interface CollectionMapper {
    /**
     * 根据ID查询收藏夹
     *
     * @param collectionId 收藏夹的ID
     * @return 返回查询到的收藏夹对象
     */
    Collection findById(@Param("collectionId") Integer collectionId);

    /**
     * 根据创建者 ID 查询收藏夹
     *
     * @param creatorId 创建者 ID
     * @return 返回查询到的收藏夹列表
     */
    List<Collection> findByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 根据收藏夹 ID 和创建者 ID 查询收藏夹
     *
     * @param collectionId 收藏夹 ID
     * @param creatorId    创建者 ID
     * @return 返回查询到的收藏夹对象
     */
    Collection findByIdAndCreatorId(@Param("collectionId") Integer collectionId, @Param("creatorId") Long creatorId);

    /**
     * 根据收藏夹 ID、创建者 ID 和笔记 ID 查询收藏夹
     *
     * @param collectionId 收藏夹 ID
     * @param creatorId    创建者 ID
     * @param noteId       笔记 ID
     * @return 返回查询到的收藏夹对象
     */
    int countByCreatorIdAndNoteId(
            @Param("creatorId") Long creatorId,
            @Param("noteId") Integer noteId);

    /**
     * 创建收藏夹
     *
     * @param collection 要创建的收藏夹对象
     * @return 返回插入操作的影响行数
     */
    int insert(Collection collection);

    /**
     * 更新收藏夹
     *
     * @param collection 要更新的收藏夹对象
     * @return 返回更新操作的影响行数
     */
    int update(Collection collection);

    /**
     * 删除收藏夹
     *
     * @param collectionId 要删除的收藏夹的ID
     * @return 返回删除操作的影响行数
     */
    int deleteById(@Param("collectionId") Integer collectionId);
}