import React, { useState } from 'react'
import { Button, Input, message } from 'antd'
import { createComment } from '../../../../request/api/comment'
import { useUser } from '../../../../domain/user/hooks/useUser'

interface CommentInputProps {
  noteId: number
  parentId?: number
  onSuccess?: () => void
  onCancel?: () => void
  placeholder?: string
}

export const CommentInput: React.FC<CommentInputProps> = ({
  noteId,
  parentId,
  onSuccess,
  onCancel,
  placeholder = '写下你的评论...',
}) => {
  const [content, setContent] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const user = useUser()

  const handleSubmit = async () => {
    if (!content.trim()) {
      message.warning('评论内容不能为空')
      return
    }

    if (!user.userId) {
      message.warning('请先登录后再发表评论')
      return
    }

    try {
      setSubmitting(true)
      const { data: response } = await createComment({
        noteId,
        parentId,
        content: content.trim(),
      })

      if (response.code === 200) {
        setContent('')
        message.success('评论成功')
        onSuccess?.()
      } else {
        throw new Error(response.message || '评论失败')
      }
    } catch (error: any) {
      console.error('评论失败:', error)
      if (error.response?.status === 401) {
        message.error('登录已过期，请重新登录')
      } else {
        message.error(error.message || '评论失败，请稍后重试')
      }
    } finally {
      setSubmitting(false)
    }
  }

  if (!user.userId) {
    return (
      <div className="comment-input">
        <Input.TextArea
          disabled
          placeholder="请先登录后再发表评论"
          autoSize={{ minRows: 2, maxRows: 6 }}
        />
      </div>
    )
  }

  return (
    <div className="comment-input">
      <Input.TextArea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder={placeholder}
        autoSize={{ minRows: 2, maxRows: 6 }}
        maxLength={500}
        showCount
      />
      <div className="mt-2 flex justify-end space-x-2">
        {parentId && (
          <Button onClick={onCancel} disabled={submitting}>
            取消回复
          </Button>
        )}
        <Button type="primary" onClick={handleSubmit} loading={submitting}>
          发布评论
        </Button>
      </div>
    </div>
  )
}

export default CommentInput
