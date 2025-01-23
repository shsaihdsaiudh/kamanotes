package com.kama.notes.service.impl;

import com.kama.notes.annotation.NeedLogin;
import com.kama.notes.mapper.NoteLikeMapper;
import com.kama.notes.mapper.NoteMapper;
import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.model.entity.NoteLike;
import com.kama.notes.scope.RequestScopeData;
import com.kama.notes.service.NoteLikeService;
import com.kama.notes.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NoteLikeServiceImpl implements NoteLikeService {

    @Autowired
    private NoteLikeMapper noteLikeMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private RequestScopeData requestScopeData;

    @Override
    public Set<Integer> findUserLikedNoteIds(Long userId, List<Integer> noteIds) {
        List<Integer> userLikedNoteIds = noteLikeMapper.findUserLikedNoteIds(userId, noteIds);
        return new HashSet<>(userLikedNoteIds);
    }

    @Override
    @NeedLogin
    public ApiResponse<EmptyVO> likeNote(Integer noteId) {

        Long userId = requestScopeData.getUserId();
        NoteLike noteLike = noteLikeMapper.findByUserIdAndNoteId(userId, noteId);

        if (noteLike != null) {
            return ApiResponseUtil.success("已经点赞过了");
        }

        noteLike = new NoteLike();
        noteLike.setUserId(userId);
        noteLike.setNoteId(noteId);
        noteLikeMapper.insert(noteLike);

        // 更新笔记点赞数
        noteMapper.likeNote(noteId);

        return ApiResponseUtil.success("点赞成功");
    }

    @Override
    @NeedLogin
    public ApiResponse<EmptyVO> unlikeNote(Integer noteId) {
        Long userId = requestScopeData.getUserId();

        NoteLike noteLike = noteLikeMapper.findByUserIdAndNoteId(userId, noteId);

        if (noteLike == null) {
            return ApiResponseUtil.success("已经取消点赞过了");
        }

        noteLikeMapper.delete(noteLike);

        // 更新笔记点赞数
        noteMapper.unlikeNote(noteId);
        return ApiResponseUtil.success("取消点赞成功");
    }
}
