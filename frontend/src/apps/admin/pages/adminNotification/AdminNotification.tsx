import React, { useEffect, useState } from 'react'
import { useNotification } from '../../../../domain/notification'
import { MarkdownEditor } from '../../../../base/components'
import { Button, message } from 'antd'

const AdminNotification: React.FC = () => {
  const { notification, setNotificationService } = useNotification()

  const [value, setValue] = useState(notification?.content ?? '')

  const [loading, setLoading] = useState(false)

  const setValueHandle = (newValue: string) => {
    setValue(newValue)
  }

  useEffect(() => {
    setValue(notification?.content ?? '')
  }, [notification, notification?.content])

  const clickHandle = async () => {
    setLoading(true)
    await setNotificationService({ content: value })
    setLoading(false)
    message.success('更新成功')
  }

  return (
    <div className="h-[500px]">
      <div className="mb-4 flex justify-end">
        <Button type="primary" onClick={clickHandle} loading={loading}>
          更新通知
        </Button>
      </div>
      <div className="h-full">
        <MarkdownEditor value={value} setValue={setValueHandle} />
      </div>
    </div>
  )
}

export default AdminNotification
