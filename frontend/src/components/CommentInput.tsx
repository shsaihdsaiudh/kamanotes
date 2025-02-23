import React, { useState, useEffect } from 'react'
import { Input, Button, message } from 'antd'
import { useUser } from '../domain/user/hooks/useUser'
import { TOKEN_KEY } from '../base/constants'
import { commentService } from '../domain/comment/service/commentService'
import { AxiosError } from 'axios'

const { TextArea } = Input

interface CommentInputProps {
  noteId: number
  parentId?: number
  onCommentAdded?: () => void
}

const CommentInput: React.FC<CommentInputProps> = ({
  noteId,
  parentId,
  onCommentAdded,
}) => {
  const [content, setContent] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { currentUser } = useUser()

  useEffect(() => {
    console.log('CommentInput组件状态:', {
      noteId,
      parentId,
      currentUser: currentUser ? '已登录' : '未登录',
      token: localStorage.getItem(TOKEN_KEY) ? '存在' : '不存在',
    })
  }, [noteId, parentId, currentUser])

  const handleSubmit = async () => {
    const trimmedContent = content.trim()
    if (!trimmedContent) {
      message.warning('评论内容不能为空')
      return
    }

    if (!currentUser) {
      message.warning('请先登录后再发表评论')
      return
    }

    console.log('准备提交评论:', {
      noteId,
      parentId,
      content: trimmedContent,
      token: localStorage.getItem(TOKEN_KEY) ? '存在' : '不存在',
    })

    setIsSubmitting(true)
    try {
      await commentService.createComment({
        noteId,
        parentId,
        content: trimmedContent,
      })

      console.log('评论提交成功')
      message.success('评论发表成功')
      setContent('')
      onCommentAdded?.()
    } catch (error) {
      console.error('评论提交失败:', error)
      const axiosError = error as AxiosError
      if (axiosError.response?.status === 401) {
        message.error('登录已过期，请重新登录')
      } else {
        message.error('评论发表失败，请稍后重试')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="comment-input">
      <TextArea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder={currentUser ? '写下你的评论...' : '请先登录后再发表评论'}
        disabled={!currentUser}
        autoSize={{ minRows: 2, maxRows: 6 }}
        style={{ marginBottom: '10px' }}
      />
      <Button
        type="primary"
        onClick={handleSubmit}
        loading={isSubmitting}
        disabled={!currentUser || !content.trim()}
      >
        发表评论
      </Button>
    </div>
  )
}

export default CommentInput
