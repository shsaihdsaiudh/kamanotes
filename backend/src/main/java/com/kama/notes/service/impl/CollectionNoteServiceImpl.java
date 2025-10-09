package com.kama.notes.service.impl;

import com.kama.notes.mapper.CollectionNoteMapper;
import com.kama.notes.service.CollectionNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CollectionNoteServiceImpl
 *
 * 收藏夹-笔记关联的业务实现类。
 *
 * 职责：
 * - 提供与用户收藏笔记相关的业务方法（例如查询用户在给定笔记列表中已收藏的笔记 ID 集合）；
 * - 将 Mapper 层返回的列表转换为 Set 以便快速查验/去重并返回给调用方。
 *
 * 说明：
 * - 该类为 Service 层实现，直接依赖于 CollectionNoteMapper 与数据库交互；
 * - 返回的 Set 包含在 noteIds 列表中且被用户收藏的 noteId；若无则返回空集合（非 null）。
 */
@Service
public class CollectionNoteServiceImpl implements CollectionNoteService {

    @Autowired
    private CollectionNoteMapper collectionNoteMapper;

    /**
     * 查找指定用户在给定笔记 ID 列表中已收藏的笔记 ID 集合。
     *
     * 实现细节：
     * - 调用 mapper 的查询方法获取用户已收藏的笔记 ID 列表；
     * - 将结果转换为 HashSet 返回，便于上层进行快速包含判断或去重。
     *
     * @param userId 用户 ID（不能为空）
     * @param noteIds 待检查的笔记 ID 列表
     * @return 用户已收藏的笔记 ID 集合（若无则返回空 Set）
     */
    @Override
    public Set<Integer> findUserCollectedNoteIds(Long userId, List<Integer> noteIds) {
        List<Integer> userCollectedNoteIds
                = collectionNoteMapper.findUserCollectedNoteIds(userId, noteIds);
        return new HashSet<>(userCollectedNoteIds);
    }
}
