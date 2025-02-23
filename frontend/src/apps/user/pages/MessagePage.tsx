import React from 'react'
import { Card } from 'antd'
import MessageList from '../components/message/MessageList'

const MessagePage: React.FC = () => {
  return (
    <div className="message-page">
      <Card title="消息中心">
        <MessageList />
      </Card>
    </div>
  )
}

export default MessagePage
