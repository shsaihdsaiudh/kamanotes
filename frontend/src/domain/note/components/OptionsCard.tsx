import React from 'react'
import { NoteWithRelations } from '../types/serviceTypes.ts'
import LikeButton from './LikeButton.tsx'
import CollectButton from './CollectButton.tsx'
import { useNoteLike } from '../../noteLike'
import { useApp } from '../../../base/hooks'
import { message } from 'antd'

interface OptionsCardProps {
  note?: NoteWithRelations
  setNoteLikeStatus?: (noteId: number, isLiked: boolean) => void
  toggleIsModalOpen: () => void
  handleCollectionQueryParams: (noteId: number) => void
  handleSelectedNoteId: (noteId: number) => void
}

const OptionsCard: React.FC<OptionsCardProps> = ({
  note,
  setNoteLikeStatus,
  toggleIsModalOpen,
  handleCollectionQueryParams,
  handleSelectedNoteId,
}) => {
  const { like, unLike } = useNoteLike()

  const app = useApp()
  let likeLoading = false

  /**
   * 点赞按钮点击处理函数
   */
  async function likeButtonClickHandle() {
    if (!app.isLogin) {
      message.info('请先登录')
      return
    }

    // 处理没有传递函数的情况
    if (!setNoteLikeStatus) return

    // 处理没有 note 或 userActions 的情况
    if (!note || !note.userActions) return

    if (likeLoading) return

    likeLoading = true
    setNoteLikeStatus(note.noteId, !note.userActions.isLiked)

    if (note.userActions.isLiked) {
      await unLike(note!.noteId)
    } else {
      await like(note.noteId)
    }

    likeLoading = false
  }

  /**
   * 收藏按钮点击处理函数
   */
  async function collectButtonClickHandle() {
    if (!app.isLogin) {
      message.info('请先登录')
      return
    }
    // 处理没有 note 或 userActions 为空的情况
    if (!note || !note.userActions) return

    // 获取收藏列表
    toggleIsModalOpen()
    handleCollectionQueryParams(note.noteId)
    handleSelectedNoteId(note.noteId)
  }

  return (
    <div className="flex gap-4">
      <LikeButton
        key={`li${note?.noteId}`}
        likeCount={note?.likeCount ?? 0}
        currentUserLiked={note?.userActions?.isLiked ?? false}
        clickHandle={likeButtonClickHandle}
      ></LikeButton>
      <CollectButton
        key={`c${note?.noteId}`}
        collectCount={note?.collectCount ?? 0}
        currentUserCollected={note?.userActions?.isCollected ?? false}
        clickHandle={collectButtonClickHandle}
      ></CollectButton>
    </div>
  )
}

export default OptionsCard
