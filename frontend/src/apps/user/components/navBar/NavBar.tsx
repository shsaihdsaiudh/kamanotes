import React, { useEffect, useState } from 'react'
import { Menu } from 'antd'
import { NavLink, useLocation } from 'react-router-dom'
import { MenuProps } from 'antd'
import {
  HOME_PAGE,
  MESSAGE_CENTER,
  QUESTION_LIST,
  QUESTION_SET,
} from '../../router/config.ts'
import Logo from '../logo/Logo.tsx'
import { useApp } from '@/base/hooks'
import { LoginModal, UserAvatarMenu } from '../../../../domain/user'
import SearchInput from '../searchInput/SearchInput.tsx'
import { ColumnDivider } from '../../../../base/components'
import DownloadNoteItem from '../../../../domain/note/components/DownloadNoteItem.tsx'
import { BellOutlined } from '@ant-design/icons'

type MenuItem = Required<MenuProps>['items'][number]

const items: MenuItem[] = [
  {
    label: <NavLink to={HOME_PAGE}>首页</NavLink>,
    key: 'home',
  },
  {
    label: <NavLink to={QUESTION_SET}>题库</NavLink>,
    key: 'question-set',
  },
  {
    label: <NavLink to={QUESTION_LIST}>题单</NavLink>,
    key: 'question-list',
  },
]

const NavBar: React.FC = () => {
  /**
   * 监听路由变化，设置选中菜单项
   */
  const [selectedMenuItem, setSelectedMenuItem] = useState<string[]>()
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/') {
      setSelectedMenuItem(['home'])
    } else {
      setSelectedMenuItem([location.pathname.split('/')[1]])
    }
  }, [location.pathname])

  /**
   * 获取 app 信息
   */
  const app = useApp()

  return (
    <nav className="flex justify-between bg-[#ffffff] px-32 dark:bg-[#141414]">
      <div className="flex items-center gap-2">
        <Logo />
        <Menu
          items={items}
          mode="horizontal"
          style={{ lineHeight: 'var(--header-height)' }}
          selectedKeys={selectedMenuItem}
        />
        <ColumnDivider />
        <div>
          <DownloadNoteItem />
        </div>
      </div>
      <div className="flex items-center gap-8">
        <SearchInput />
        <NavLink to={MESSAGE_CENTER}>
          <BellOutlined className="hover:text-gray-600" />
        </NavLink>
        {app.isLogin ? <UserAvatarMenu /> : <LoginModal />}
      </div>
    </nav>
  )
}

export default NavBar
