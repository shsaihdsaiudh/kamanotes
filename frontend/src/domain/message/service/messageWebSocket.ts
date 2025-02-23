import { Message } from '../types'
import { getToken } from '@/utils/auth'

class MessageWebSocketService {
  private ws: WebSocket | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectTimeout = 3000
  private messageHandlers: ((message: Message) => void)[] = []

  constructor() {
    this.connect()
  }

  private connect() {
    const token = getToken()
    if (!token) {
      console.warn('未登录，无法建立WebSocket连接')
      return
    }

    const wsUrl = `${import.meta.env.VITE_WS_URL || 'ws://localhost:8080'}/ws/message`
    this.ws = new WebSocket(wsUrl)

    this.ws.onopen = () => {
      console.log('WebSocket连接已建立')
      // 发送认证信息
      this.ws?.send(JSON.stringify({ type: 'AUTH', token }))
      // 重置重连次数
      this.reconnectAttempts = 0
    }

    this.ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data) as Message
        this.notifyHandlers(message)
      } catch (error) {
        console.error('处理WebSocket消息时发生错误:', error)
      }
    }

    this.ws.onclose = () => {
      console.log('WebSocket连接已关闭')
      this.attemptReconnect()
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket发生错误:', error)
    }
  }

  private attemptReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('WebSocket重连次数超过最大限制')
      return
    }

    this.reconnectAttempts++
    console.log(`尝试第${this.reconnectAttempts}次重连...`)

    setTimeout(() => {
      this.connect()
    }, this.reconnectTimeout)
  }

  public addMessageHandler(handler: (message: Message) => void) {
    this.messageHandlers.push(handler)
  }

  public removeMessageHandler(handler: (message: Message) => void) {
    const index = this.messageHandlers.indexOf(handler)
    if (index !== -1) {
      this.messageHandlers.splice(index, 1)
    }
  }

  private notifyHandlers(message: Message) {
    this.messageHandlers.forEach((handler) => {
      try {
        handler(message)
      } catch (error) {
        console.error('执行消息处理器时发生错误:', error)
      }
    })
  }

  public disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }
}

// 导出单例实例
export const messageWebSocket = new MessageWebSocketService()
