import React, { useEffect, useState } from 'react'
import { Badge } from 'antd'
import { BellOutlined } from '@ant-design/icons'
import { Link } from 'react-router-dom'
import { getUnreadCount } from '@/request/api/message'

const MessageBadge: React.FC = () => {
  const [unreadCount, setUnreadCount] = useState(0)

  const fetchUnreadCount = async () => {
    try {
      const { data } = await getUnreadCount()
      if (data.code === 200) {
        setUnreadCount(data.data)
      }
    } catch (err) {
      console.error('获取未读消息数量失败:', err)
    }
  }

  useEffect(() => {
    fetchUnreadCount()
    // 每分钟刷新一次未读消息数量
    const timer = setInterval(fetchUnreadCount, 60000)
    return () => clearInterval(timer)
  }, [])

  return (
    <Link to="/messages">
      <Badge count={unreadCount} overflowCount={99}>
        <BellOutlined style={{ fontSize: '20px' }} />
      </Badge>
    </Link>
  )
}

export default MessageBadge
