import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons'
import { Button, Layout, Menu, Space, Tag, Typography } from 'antd'
import type { MenuProps } from 'antd'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAppShellStore } from '../store/app-shell-store'
import { APP_NAV_ITEMS } from '../utils/navigation'

const { Header, Content, Sider } = Layout

function AppLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const collapsed = useAppShellStore((state) => state.collapsed)
  const toggleCollapsed = useAppShellStore((state) => state.toggleCollapsed)

  const items: MenuProps['items'] = APP_NAV_ITEMS.map((item) => ({
    key: item.path,
    icon: item.icon,
    label: item.label,
  }))

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        trigger={null}
        width={260}
        collapsedWidth={84}
        theme="light"
        style={{ borderRight: '1px solid #e5edf5' }}
      >
        <div style={{ padding: 20 }}>
          <Typography.Title
            level={4}
            style={{ margin: 0, fontSize: collapsed ? 16 : 20 }}
          >
            OAB
          </Typography.Title>
          {!collapsed ? (
            <Typography.Text type="secondary">
              Frontend foundation
            </Typography.Text>
          ) : null}
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={items}
          onClick={({ key }) => navigate(key)}
          style={{ borderInlineEnd: 0 }}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: '#ffffff',
            padding: '0 20px',
            borderBottom: '1px solid #e5edf5',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <Space size="middle">
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={toggleCollapsed}
              aria-label="Toggle menu"
            />
            <div>
              <Typography.Text strong>
                optimization-algorithm-backend
              </Typography.Text>
              <br />
              <Typography.Text type="secondary">
                Phase 1 scaffold and infrastructure
              </Typography.Text>
            </div>
          </Space>
          <Tag color="blue">Phase 1</Tag>
        </Header>
        <Content style={{ padding: 24 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default AppLayout
