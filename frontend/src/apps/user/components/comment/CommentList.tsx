import React, { useEffect, useState, useContext } from 'react'
import { Avatar, Button, List, message } from 'antd'
import {
  LikeOutlined,
  LikeFilled,
  StarOutlined,
  StarFilled,
  MessageOutlined,
} from '@ant-design/icons'
import { formatDistanceToNow } from 'date-fns'
import { zhCN } from 'date-fns/locale'
import CommentInput from './CommentInput'
import {
  getComments,
  likeComment,
  unlikeComment,
} from '../../../../request/api/comment'
import { UserContext } from '../../../../domain/user/context/UserContext'
import { Comment } from '../../../../domain/comment/types'
import CommentItem from './CommentItem'

interface CommentListProps {
  noteId: number
  onCommentCountChange?: () => void
}

const CommentList: React.FC<CommentListProps> = ({
  noteId,
  onCommentCountChange,
}) => {
  const [comments, setComments] = useState<Comment[]>([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)
  const [replyTo, setReplyTo] = useState<Comment | null>(null)
  const { currentUser } = useContext(UserContext)

  const fetchComments = async () => {
    if (!noteId) {
      console.warn('noteId is required to fetch comments')
      return
    }

    try {
      setLoading(true)
      const response = await getComments({
        noteId,
        page,
        pageSize: 10,
      })

      if (response?.data?.code === 200) {
        const newComments = response.data.data || []
        setComments((prev) =>
          page === 1 ? newComments : [...prev, ...newComments],
        )
        setHasMore(newComments.length === 10)
        onCommentCountChange?.()
      } else {
        console.error('获取评论失败:', response)
        message.error(response?.data?.message || '获取评论失败')
      }
    } catch (err) {
      console.error('获取评论失败:', err)
      message.error('获取评论失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (noteId) {
      setPage(1)
      fetchComments()
    }
  }, [noteId])

  useEffect(() => {
    if (noteId && page > 1) {
      fetchComments()
    }
  }, [page])

  const handleLike = async (commentId: number, isLiked: boolean) => {
    if (!currentUser) {
      message.warning('请先登录后再点赞')
      return
    }

    try {
      const response = await (isLiked
        ? unlikeComment(commentId)
        : likeComment(commentId))
      if (response?.data?.code === 200) {
        setComments((prev) =>
          prev.map((comment) =>
            comment.commentId === commentId
              ? {
                  ...comment,
                  likeCount: isLiked
                    ? Math.max(0, comment.likeCount - 1)
                    : comment.likeCount + 1,
                  userActions: {
                    ...comment.userActions,
                    isLiked: !isLiked,
                  },
                }
              : comment,
          ),
        )
      } else {
        message.error(
          response?.data?.message || (isLiked ? '取消点赞失败' : '点赞失败'),
        )
      }
    } catch (err) {
      console.error(isLiked ? '取消点赞失败:' : '点赞失败:', err)
      message.error(isLiked ? '取消点赞失败' : '点赞失败')
    }
  }

  const handleReply = (comment: Comment) => {
    if (!currentUser) {
      message.warning('请先登录后再回复')
      return
    }
    setReplyTo(comment)
  }

  const handleCommentSuccess = () => {
    setPage(1)
    fetchComments()
    setReplyTo(null)
    onCommentCountChange?.()
  }

  return (
    <div className="comment-list">
      <CommentInput
        noteId={noteId}
        parentId={replyTo?.commentId}
        onSuccess={handleCommentSuccess}
        onCancel={() => setReplyTo(null)}
        placeholder={
          replyTo ? `回复 ${replyTo.author.username}：` : '写下你的评论...'
        }
      />
      <List
        className="mt-4"
        loading={loading}
        itemLayout="horizontal"
        dataSource={comments || []}
        locale={{ emptyText: '暂无评论' }}
        loadMore={
          hasMore &&
          !loading &&
          comments.length > 0 && (
            <div className="mt-4 text-center">
              <Button onClick={() => setPage((p) => p + 1)} loading={loading}>
                加载更多
              </Button>
            </div>
          )
        }
        renderItem={(comment) => (
          <CommentItem
            key={comment.commentId}
            comment={comment}
            onCommentSuccess={() => handleReply(comment)}
            onLike={(isLiked) => handleLike(comment.commentId, isLiked)}
          />
        )}
      />
    </div>
  )
}

export default CommentList
