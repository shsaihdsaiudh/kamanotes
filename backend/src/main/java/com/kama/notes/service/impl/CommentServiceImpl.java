package com.kama.notes.service.impl;

import com.kama.notes.annotation.NeedLogin;
import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.EmptyVO;
import com.kama.notes.mapper.CommentMapper;
import com.kama.notes.mapper.NoteMapper;
import com.kama.notes.mapper.UserMapper;
import com.kama.notes.mapper.CommentLikeMapper;
import com.kama.notes.model.base.Pagination;
import com.kama.notes.model.dto.message.MessageDTO;
import com.kama.notes.model.entity.Comment;
import com.kama.notes.model.entity.CommentLike;
import com.kama.notes.model.entity.Note;
import com.kama.notes.model.entity.User;
import com.kama.notes.model.dto.comment.CommentQueryParams;
import com.kama.notes.model.dto.comment.CreateCommentRequest;
import com.kama.notes.model.dto.comment.UpdateCommentRequest;
import com.kama.notes.model.enums.message.MessageTargetType;
import com.kama.notes.model.enums.message.MessageType;
import com.kama.notes.model.vo.comment.CommentVO;
import com.kama.notes.model.vo.user.UserActionVO;
import com.kama.notes.scope.RequestScopeData;
import com.kama.notes.service.CommentService;
import com.kama.notes.service.MessageService;
import com.kama.notes.utils.ApiResponseUtil;
import com.kama.notes.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CommentServiceImpl
 *
 * 评论服务实现类，负责评论的增删改查与点赞/取消点赞等业务逻辑。
 *
 * 主要职责：
 * - 创建、更新、删除评论并维护相关计数（笔记评论数、父评论回复数等）；
 * - 点赞/取消点赞操作并记录用户点赞关系；
 * - 获取指定笔记的评论列表（支持分页），将实体组装成 CommentVO 返回给前端；
 * - 在适当时机发送通知（MessageService）。
 *
 * 事务与安全：
 * - 写操作（创建、更新、删除、点赞/取消点赞）使用 @Transactional 以保证数据一致性；
 * - 需要登录的操作标注 @NeedLogin，依赖 RequestScopeData 提供当前请求用户信息。
 *
 * 错误处理：
 * - 方法内部对常见错误返回 ApiResponse 带有合适的 HTTP 状态码与错误信息，并记录日志。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final MessageService messageService;
    private final RequestScopeData requestScopeData;

    /**
     * 创建评论
     *
     * 行为：
     * - 校验笔记是否存在，构造 Comment 并插入；
     * - 增加笔记的评论计数，若为回复则增加父评论的回复计数；
     * - 发送评论通知给笔记作者（通过 MessageService）。
     *
     * 权限与事务：
     * - 需要登录；方法在事务中执行，出现异常会回滚。
     *
     * @param request 创建请求体，包含 noteId、content、parentId 等
     * @return 成功返回新创建的 commentId，否则返回错误信息
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<Integer> createComment(CreateCommentRequest request) {
        log.info("开始创建评论: request={}", request);

        try {
            Long userId = requestScopeData.getUserId();

            // 获取笔记信息
            Note note = noteMapper.findById(request.getNoteId());
            if (note == null) {
                log.error("笔记不存在: noteId={}", request.getNoteId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "笔记不存在");
            }

            // 创建评论
            Comment comment = new Comment();
            comment.setNoteId(request.getNoteId());
            comment.setContent(request.getContent());
            comment.setAuthorId(userId);
            comment.setParentId(request.getParentId());
            comment.setLikeCount(0);
            comment.setReplyCount(0);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());

            commentMapper.insert(comment);
            log.info("评论创建结果: commentId={}", comment.getCommentId());

            // 增加笔记评论数
            noteMapper.incrementCommentCount(request.getNoteId());

            // 如果是回复评论，增加父评论的回复数
            if (request.getParentId() != null) {
                commentMapper.incrementReplyCount(request.getParentId());
            }

            // 发送评论通知
            MessageDTO messageDTO = new MessageDTO();

            messageDTO.setType(MessageType.COMMENT);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(request.getNoteId());
            messageDTO.setReceiverId(note.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setContent(request.getContent());
            messageDTO.setIsRead(false);

            messageService.createMessage(messageDTO);

            return ApiResponse.success(comment.getCommentId());
        } catch (Exception e) {
            log.error("创建评论失败", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "创建评论失败: " + e.getMessage());
        }
    }

    /**
     * 更新评论内容
     *
     * 权限校验：
     * - 仅评论作者可更新。
     *
     * @param commentId 评论 ID
     * @param request 更新请求，包含新的 content
     * @return 操作结果（空数据的成功/错误响应）
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> updateComment(Integer commentId, UpdateCommentRequest request) {
        Long userId = requestScopeData.getUserId();

        // 查询评论
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "评论不存在");
        }

        // 检查权限
        if (!comment.getAuthorId().equals(userId)) {
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "无权修改该评论");
        }

        try {
            // 更新评论
            comment.setContent(request.getContent());
            comment.setUpdatedAt(LocalDateTime.now());
            commentMapper.update(comment);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("更新评论失败", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "更新评论失败");
        }
    }

    /**
     * 删除评论
     *
     * 权限校验：
     * - 仅评论作者可删除（当前实现）。
     *
     * @param commentId 评论 ID
     * @return 操作结果
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> deleteComment(Integer commentId) {
        Long userId = requestScopeData.getUserId();

        // 查询评论
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "评论不存在");
        }

        // 检查权限
        if (!comment.getAuthorId().equals(userId)) {
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "无权删除该评论");
        }

        try {
            // 删除评论
            commentMapper.deleteById(commentId);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "删除评论失败");
        }
    }

    /**
     * 获取评论列表（按评论树结构返回，支持对一级评论的分页）
     *
     * 实现要点：
     * - 一次性拉取当前 note 的所有评论（适用于评论量适中的场景），在内存中构建树并分页一级评论；
     * - 批量查询作者信息与当前用户的点赞记录以减少 DB 调用；
     * - 返回 CommentVO 列表及 Pagination 分页信息。
     *
     * @param params 查询参数（包含 noteId、page、pageSize 等）
     * @return 评论 VO 列表或错误响应
     */
    @Override
    public ApiResponse<List<CommentVO>> getComments(CommentQueryParams params) {
        try {
            // 拉取整棵评论树（一个 note 通常也就几百条，足够了）
            List<Comment> comments = commentMapper.findByNoteId(params.getNoteId());

            System.out.println(comments);

            if (CollectionUtils.isEmpty(comments)) {
                return ApiResponse.success(Collections.emptyList());
            }

            /* ---------- 数据准备：分组 + 批量查询 ---------- */

            // 2.1 一级评论列表
            List<Comment> firstLevel = comments.stream()
                    .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                    .sorted(Comparator.comparing(Comment::getCreatedAt))      // 按时间升序
                    .toList();

            int from = PaginationUtils.calculateOffset(params.getPage(), params.getPageSize());
            if (from >= firstLevel.size()) {
                return ApiResponse.success(Collections.emptyList());          // 页码溢出，直接返回空
            }

            int to = Math.min(from + params.getPageSize(), firstLevel.size());
            List<Comment> pagedFirst = firstLevel.subList(from, to);

            // 2.3 parentId  => children
            Map<Integer, List<Comment>> repliesMap = comments.stream()
                    .filter(c -> c.getParentId() != null)
                    .collect(Collectors.groupingBy(Comment::getParentId));

            // 2.4 批量获取作者信息
            List<Long> authorIds = comments.stream()
                    .map(Comment::getAuthorId)
                    .collect(Collectors.toList());

            Map<Long, User> authorMap = userMapper.findByIdBatch(authorIds)
                    .stream()
                    .collect(Collectors.toMap(User::getUserId, u -> u));

            // 2.5 当前用户一次性查点赞
            Long currentUserId = requestScopeData.getUserId();

            Set<Integer> likedSet;
            if (currentUserId != null) {
                List<Integer> allCommentIds = comments.stream()
                        .map(Comment::getCommentId)
                        .toList();
                likedSet = new HashSet<>(commentLikeMapper.findUserLikedCommentIds(currentUserId, allCommentIds));
            } else {
                likedSet = Collections.emptySet();
            }

            /* ---------- 递归装配 VO ---------- */
            List<CommentVO> result = pagedFirst.stream()
                    .map(c -> toVO(c, repliesMap, authorMap, likedSet))
                    .toList();

            Pagination pagination = new Pagination(params.getPage(), params.getPageSize(), firstLevel.size());

            return ApiResponseUtil.success("", result, pagination);
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取评论列表失败");
        }
    }

    /**
     * 将 Comment 递归转换为 CommentVO，并填充作者信息、当前用户动作及子评论列表
     *
     * @param c 当前 Comment 实体
     * @param repliesMap parentId -> children 列表映射
     * @param authorMap authorId -> User 映射
     * @param likedSet 当前用户已点赞的评论 ID 集合
     * @return 组装好的 CommentVO
     */
    private CommentVO toVO(Comment c,
                           Map<Integer, List<Comment>> repliesMap,
                           Map<Long, User> authorMap,
                           Set<Integer> likedSet) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(c.getCommentId());
        vo.setNoteId(c.getNoteId());
        vo.setContent(c.getContent());
        vo.setLikeCount(c.getLikeCount());
        vo.setReplyCount(c.getReplyCount());
        vo.setCreatedAt(c.getCreatedAt());
        vo.setUpdatedAt(c.getUpdatedAt());

        // 作者信息
        User author = authorMap.get(c.getAuthorId());
        if (author != null) {
            CommentVO.SimpleAuthorVO a = new CommentVO.SimpleAuthorVO();
            a.setUserId(author.getUserId());
            a.setUsername(author.getUsername());
            a.setAvatarUrl(author.getAvatarUrl());
            vo.setAuthor(a);
        }

        // 当前用户动作
        if (!likedSet.isEmpty()) {
            UserActionVO actions = new UserActionVO();
            actions.setIsLiked(likedSet.contains(c.getCommentId()));
            vo.setUserActions(actions);
        } else {
            vo.setUserActions(new UserActionVO());
            vo.getUserActions().setIsLiked(false);
        }

        // 递归子评论
        List<Comment> children = repliesMap.get(c.getCommentId());
        if (children != null && !children.isEmpty()) {
            List<CommentVO> childVOs = children.stream()
                    .map(child -> toVO(child, repliesMap, authorMap, likedSet))
                    .toList();
            vo.setReplies(childVOs);
        } else {
            vo.setReplies(Collections.emptyList());
        }
        return vo;
    }

    /**
     * 给评论点赞
     *
     * 行为：
     * - 校验评论存在，增加评论的 likeCount，并插入 CommentLike 记录；
     * - 发送点赞通知给评论作者。
     *
     * @param commentId 评论 ID
     * @return 操作结果
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> likeComment(Integer commentId) {
        Long userId = requestScopeData.getUserId();

        System.out.println(userId + " liked " + commentId);

        // 查询评论
        Comment comment = commentMapper.findById(commentId);

        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "评论不存在");
        }

        try {
            // 增加评论点赞数
            commentMapper.incrementLikeCount(commentId);
            CommentLike commentLike = new CommentLike();

            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);

            commentLikeMapper.insert(commentLike);

            MessageDTO messageDTO = new MessageDTO();

            messageDTO.setType(MessageType.LIKE);
            messageDTO.setReceiverId(comment.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(comment.getNoteId());
            messageDTO.setIsRead(false);

            messageService.createMessage(messageDTO);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("点赞评论失败", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "点赞评论失败");
        }
    }

    /**
     * 取消评论点赞
     *
     * 行为：
     * - 校验评论存在，减少评论 likeCount，并删除对应的 CommentLike 记录。
     *
     * @param commentId 评论 ID
     * @return 操作结果
     */
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> unlikeComment(Integer commentId) {
        Long userId = requestScopeData.getUserId();

        // 查询评论
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "评论不存在");
        }

        try {
            // 减少评论点赞数
            commentMapper.decrementLikeCount(commentId);
            commentLikeMapper.delete(commentId, userId);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("取消点赞评论失败", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "取消点赞评论失败");
        }
    }
}