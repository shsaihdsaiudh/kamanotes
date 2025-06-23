/**
 * 消息发送者信息
 */
export interface MessageSender {
  userId: number
  username: string
  avatarUrl: string
}

/**
 * 消息类型
 */
export type MessageType = 'COMMENT' | 'LIKE' | 'SYSTEM'

/**
 * 消息数据
 */
export interface Message {
  messageId: number
  sender: MessageSender
  type: MessageType
  targetId: number
  content: string
  isRead: boolean
  createdAt: string
}

/**
 * 消息查询参数
 */
export interface MessageQueryParams {
  page: number
  pageSize: number
  type?: MessageType
  isRead?: boolean
}

/**
 * 消息API响应
 */
export interface MessageResponse {
  code: number
  message: string
  data: Message[]
}

/**
 * 未读消息数量响应
 */
export interface UnreadCountResponse {
  code: number
  message: string
  data: number
}
