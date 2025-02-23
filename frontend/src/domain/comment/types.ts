/**
 * 评论作者信息
 */
export interface CommentAuthor {
  userId: number
  username: string
  avatarUrl: string
}

/**
 * 用户操作状态
 */
export interface CommentUserActions {
  isLiked: boolean
}

/**
 * 评论数据
 */
export interface Comment {
  commentId: number
  noteId: number
  content: string
  likeCount: number
  replyCount: number
  createdAt: string
  author: CommentAuthor
  userActions?: CommentUserActions
  replies?: Comment[]
}

/**
 * 创建评论请求参数
 */
export interface CreateCommentParams {
  noteId: number
  parentId?: number
  content: string
}

/**
 * 评论查询参数
 */
export interface CommentQueryParams {
  noteId: number
  parentId?: number
  page: number
  pageSize: number
}

/**
 * 评论API响应
 */
export interface CommentResponse {
  code: number
  message: string
  data: Comment[]
}
