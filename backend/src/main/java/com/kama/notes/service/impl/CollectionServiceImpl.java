package com.kama.notes.service.impl;

import com.kama.notes.annotation.NeedLogin;
import com.kama.notes.mapper.CollectionMapper;
import com.kama.notes.mapper.CollectionNoteMapper;
import com.kama.notes.mapper.NoteMapper;
import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.dto.collection.CollectionQueryParams;
import com.kama.notes.model.dto.collection.CreateCollectionBody;
import com.kama.notes.model.dto.collection.UpdateCollectionBody;
import com.kama.notes.model.entity.Collection;
import com.kama.notes.model.entity.CollectionNote;
import com.kama.notes.model.vo.collection.CollectionVO;
import com.kama.notes.model.vo.collection.CreateCollectionVO;
import com.kama.notes.scope.RequestScopeData;
import com.kama.notes.service.CollectionService;
import com.kama.notes.utils.ApiResponseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * CollectionServiceImpl
 *
 * 收藏夹业务实现类。
 *
 * 主要职责：
 * - 提供收藏夹的查询、创建、删除及批量修改（对笔记的收藏/取消收藏）操作；
 * - 依赖 RequestScopeData 获取当前请求的用户信息，并调用 Mapper 层完成数据库操作；
 * - 对涉及多表变更的操作使用事务保证一致性。
 *
 * 说明与约定：
 * - 所有对外返回均使用 ApiResponse 包装，便于统一错误/成功处理；
 * - 标注 @NeedLogin 的方法需要登录拦截器确保用户已认证；
 * - 在高并发场景下，收藏计数的增加/减少应注意并发安全与幂等性，目前由简单的计数方法实现。
 */
@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionNoteMapper collectionNoteMapper;

    @Autowired
    private NoteMapper noteMapper;

    /**
     * 获取指定用户的收藏夹列表，并可根据传入的 noteId 标注每个收藏夹是否包含该笔记。
     *
     * 行为说明：
     * - 若 queryParams.noteId 非空，将会查询哪些收藏夹包含该 noteId，并将结果写入 CollectionVO.noteStatus；
     * - 若 noteId 为空，则返回纯收藏夹列表（不设置 noteStatus）。
     *
     * @param queryParams 查询参数（包含 creatorId 与可选的 noteId）
     * @return 包含 CollectionVO 列表的 ApiResponse，提示信息与数据一并返回
     */
    @Override
    public ApiResponse<List<CollectionVO>> getCollections(CollectionQueryParams queryParams) {
        // 收藏夹列表
        List<Collection> collections = collectionMapper.findByCreatorId(queryParams.getCreatorId());
        List<Integer> collectionIds = collections.stream().map(Collection::getCollectionId).toList();
        final Set<Integer> collectedNoteIdCollectionIds;

        // 查看是否传入了 noteId
        if (queryParams.getNoteId() != null) {
            // 收藏了 noteId 的收藏夹列表
            collectedNoteIdCollectionIds = collectionNoteMapper.filterCollectionIdsByNoteId(queryParams.getNoteId(), collectionIds);
        } else {
            collectedNoteIdCollectionIds = Collections.emptySet();
        }

        // 将 collections 映射为 CollectionVOList
        List<CollectionVO> collectionVOList = collections.stream().map(collection -> {
            CollectionVO collectionVO = new CollectionVO();
            BeanUtils.copyProperties(collection, collectionVO);

            // 检查是否传入了 noteId 参数并且当前收藏夹收藏了该 note
            if (queryParams.getNoteId() == null) return collectionVO;

            // 设置收藏夹收藏笔记状态
            CollectionVO.NoteStatus noteStatus = new CollectionVO.NoteStatus();

            noteStatus.setIsCollected(collectedNoteIdCollectionIds.contains(collection.getCollectionId()));
            noteStatus.setNoteId(queryParams.getNoteId());
            collectionVO.setNoteStatus(noteStatus);

            return collectionVO;
        }).toList();

        return ApiResponseUtil.success("获取收藏夹列表成功", collectionVOList);
    }

    /**
     * 创建收藏夹。
     *
     * 约定：
     * - 需要登录，方法通过 @NeedLogin 注解声明；
     * - 使用 requestScopeData 获取当前用户 ID 作为创建者；
     * - 若插入成功，返回新建收藏夹的 ID 给客户端。
     *
     * @param requestBody 创建请求体
     * @return 包含新建收藏夹 ID 的 ApiResponse，失败时返回错误信息
     */
    @Override
    @NeedLogin
    public ApiResponse<CreateCollectionVO> createCollection(CreateCollectionBody requestBody) {

        Long creatorId = requestScopeData.getUserId();

        Collection collection = new Collection();
        BeanUtils.copyProperties(requestBody, collection);
        collection.setCreatorId(creatorId);

        try {
            collectionMapper.insert(collection);
            CreateCollectionVO createCollectionVO = new CreateCollectionVO();

            createCollectionVO.setCollectionId(collection.getCollectionId());
            return ApiResponseUtil.success("创建成功", createCollectionVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("创建失败");
        }
    }

    /**
     * 删除收藏夹及其包含的所有笔记关联记录。
     *
     * 行为说明：
     * - 需要登录，并且当前用户必须是收藏夹的创建者；
     * - 在事务中先删除收藏夹记录，再删除关联的 collection_note 记录，保证一致性；
     * - 若发生异常，事务回滚并返回错误响应。
     *
     * @param collectionId 要删除的收藏夹 ID
     * @return 操作结果的 ApiResponse（成功或失败）
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> deleteCollection(Integer collectionId) {
        // 校验是否是收藏夹创建者
        Long creatorId = requestScopeData.getUserId();
        Collection collection = collectionMapper.findByIdAndCreatorId(collectionId, creatorId);

        if (collection == null) {
            return ApiResponseUtil.error("收藏夹不存在或者无权限删除");
        }

        try {
            // 删除收藏夹
            collectionMapper.deleteById(collectionId);
            // 删除收藏夹中的所有的笔记
            collectionNoteMapper.deleteByCollectionId(collectionId);
            return ApiResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ApiResponseUtil.error("删除失败");
        }
    }

    /**
     * 批量修改收藏夹集合：对指定 noteId 在多个收藏夹中执行添加或移除操作。
     *
     * 行为说明：
     * - 需要登录；当前用户必须对所操作的每个收藏夹拥有删除/修改权限（即为创建者）；
     * - 对每个 UpdateItem，根据 action 字段执行 create / delete 操作；
     * - 在 create 操作中，如果用户之前未在任何收藏夹收藏过该 note，会调用 noteMapper.collectNote 增加笔记收藏数；
     * - 在 delete 操作中，如果用户在所有收藏夹中都已移除该 note，会调用 noteMapper.unCollectNote 减少笔记收藏数；
     *
     * 并发/幂等性考虑：
     * - 当前实现为逐条处理并捕获异常返回错误信息；在高并发或需要严格幂等性的场景下应改为乐观锁或幂等检查。
     *
     * @param requestBody 批量修改请求，包含 noteId 与多个 UpdateItem
     * @return 操作结果的 ApiResponse（成功或失败）
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> batchModifyCollection(UpdateCollectionBody requestBody) {

        Long userId = requestScopeData.getUserId();
        Integer noteId = requestBody.getNoteId();

        UpdateCollectionBody.UpdateItem[] collections = requestBody.getCollections();

        for (UpdateCollectionBody.UpdateItem collection : collections) {
            Integer collectionId = collection.getCollectionId();
            String action = collection.getAction();

            // 校验是否是收藏夹创建者
            Collection collectionEntity = collectionMapper.findByIdAndCreatorId(collectionId, userId);

            if (collectionEntity == null) {
                return ApiResponseUtil.error("收藏夹不存在或者无权限操作");
            }

            if ("create".equals(action)) {
                try {
                    // 获取用户是否收藏过该笔记
                    if (collectionMapper.countByCreatorIdAndNoteId(userId, noteId) == 0) {
                        // 笔记不存在，给笔记增加收藏量
                        noteMapper.collectNote(noteId);
                    }
                    CollectionNote collectionNote = new CollectionNote();
                    collectionNote.setCollectionId(collectionId);
                    collectionNote.setNoteId(noteId);
                    collectionNoteMapper.insert(collectionNote);
                } catch (Exception e) {
                    return ApiResponseUtil.error("收藏失败");
                }
            }

            if ("delete".equals(action)) {
                try {
                    collectionNoteMapper.deleteByCollectionIdAndNoteId(collectionId, noteId);
                    if (collectionMapper.countByCreatorIdAndNoteId(userId, noteId) == 0) {
                        // 笔记不存在，给笔记减少收藏量
                        noteMapper.unCollectNote(noteId);
                    }
                } catch (Exception e) {
                    return ApiResponseUtil.error("取消收藏失败");
                }
            }
        }
        return ApiResponseUtil.success("操作成功");
    }
}
