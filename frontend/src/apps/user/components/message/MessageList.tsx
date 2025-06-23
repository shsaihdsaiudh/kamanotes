import React, { useEffect, useState } from 'react'
import { Button, List, message, Tag } from 'antd'
import { getMessages, markAsRead, markAllAsRead } from '@/request/api/message'
import { Message } from '@/domain/message/types'
import { messageWebSocket } from '@/domain/message/service/messageWebSocket'

interface MessageListProps {
  onUnreadCountChange?: () => void
}

const MessageList: React.FC<MessageListProps> = ({ onUnreadCountChange }) => {
  const [messages, setMessages] = useState<Message[]>([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)

  // 处理新消息
  const handleNewMessage = (newMessage: Message) => {
    setMessages((prev) => [newMessage, ...prev])
    message.info('收到新消息')
    onUnreadCountChange?.()
  }

  useEffect(() => {
    // 添加WebSocket消息处理器
    messageWebSocket.addMessageHandler(handleNewMessage)

    // 组件卸载时移除处理器
    return () => {
      messageWebSocket.removeMessageHandler(handleNewMessage)
    }
  }, [])

  const fetchMessages = async () => {
    try {
      setLoading(true)
      const { data } = await getMessages({
        page,
        pageSize: 10,
      })
      if (data.code === 200) {
        setMessages((prev) =>
          page === 1 ? data.data.list : [...prev, ...data.data.list],
        )
        setHasMore(data.data.list.length === 10)
      }
    } catch (err) {
      console.error('获取消息失败:', err)
      message.error('获取消息失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchMessages()
  }, [page])

  const handleMarkAsRead = async (messageId: number) => {
    try {
      const { data } = await markAsRead(messageId)
      if (data.code === 200) {
        setMessages((prev) =>
          prev.map((msg) =>
            msg.messageId === messageId
              ? {
                  ...msg,
                  isRead: true,
                }
              : msg,
          ),
        )
        onUnreadCountChange?.()
      }
    } catch (err: unknown) {
      if (err instanceof Error) message.error(err.message)
    }
  }

  const handleMarkAllAsRead = async () => {
    try {
      const { data } = await markAllAsRead()
      if (data.code === 200) {
        setMessages((prev) =>
          prev.map((msg) => ({
            ...msg,
            isRead: true,
          })),
        )
        onUnreadCountChange?.()
        message.success('已全部标记为已读')
      }
    } catch (err: unknown) {
      if (err instanceof Error) message.error(err.message)
    }
  }

  const renderMessageContent = (msg: Message) => {
    switch (msg.type) {
      case 'COMMENT':
        return (
          <div>
            <Tag color="blue">评论</Tag>
            {msg.content}
          </div>
        )
      case 'LIKE':
        return (
          <div>
            <Tag color="red">点赞</Tag>
            {msg.content}
          </div>
        )
      case 'SYSTEM':
        return (
          <div>
            <Tag color="green">系统</Tag>
            {msg.content}
          </div>
        )
      default:
        return msg.content
    }
  }

  return (
    <div className="message-list">
      {messages.length > 0 && (
        <div className="mb-4 flex justify-end">
          <Button onClick={handleMarkAllAsRead}>全部标记为已读</Button>
        </div>
      )}
      <List
        loading={loading}
        itemLayout="horizontal"
        dataSource={messages}
        locale={{ emptyText: '暂无消息' }}
        renderItem={(msg) => (
          <List.Item
            actions={[
              !msg.isRead && (
                <Button
                  key="mark-read"
                  type="link"
                  onClick={() => handleMarkAsRead(msg.messageId)}
                >
                  标记已读
                </Button>
              ),
            ]}
          >
            <List.Item.Meta
              title={
                <div className="flex items-center">
                  <span className="mr-2">{msg.sender.username}</span>
                  {!msg.isRead && (
                    <Tag color="red" className="ml-2">
                      未读
                    </Tag>
                  )}
                </div>
              }
              description={renderMessageContent(msg)}
            />
          </List.Item>
        )}
        loadMore={
          hasMore && (
            <div className="mt-4 text-center">
              <Button onClick={() => setPage((p) => p + 1)} loading={loading}>
                加载更多
              </Button>
            </div>
          )
        }
      />
    </div>
  )
}

export default MessageList
