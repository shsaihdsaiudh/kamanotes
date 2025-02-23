import { http } from '../../../request/http'

export interface CreateCommentParams {
  noteId: number
  parentId?: number
  content: string
}

class CommentService {
  async createComment(params: CreateCommentParams) {
    return http.post('/api/comments', params)
  }

  async getComments(noteId: number) {
    return http.get(`/api/comments/${noteId}`)
  }
}

export const commentService = new CommentService()
