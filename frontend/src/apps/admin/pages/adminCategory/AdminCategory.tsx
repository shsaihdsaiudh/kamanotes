import React from 'react'
import { CategoryList } from '../../../../domain/category'

/**
 * AdminCategory
 *
 * 管理后台的分类页面容器组件。
 *
 * 说明：
 * - 作为管理端页面的最简单容器，仅负责渲染 CategoryList 域组件；
 * - 不承担业务逻辑或数据加载，相关功能（分页、筛选、增删改）应在 CategoryList 或上层容器中实现；
 * - 建议在路由级别使用懒加载（React.lazy + Suspense）以优化管理端的首屏加载性能。
 *
 * 用法：
 * <AdminCategory />
 */
const AdminCategory: React.FC = () => {
  return (
    <div>
      <CategoryList />
    </div>
  )
}

export default AdminCategory
