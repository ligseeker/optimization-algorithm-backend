import {
  ApartmentOutlined,
  BarChartOutlined,
  DashboardOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  NodeIndexOutlined,
  SettingOutlined,
  ShareAltOutlined,
} from '@ant-design/icons'
import { Button, Layout, Menu, Space, Tag, Typography, message } from 'antd'
import type { MenuProps } from 'antd'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAppShellStore } from '../store/app-shell-store'
import { useAuthStore } from '../store/auth-store'

const { Header, Content, Sider } = Layout

type MenuItem = NonNullable<MenuProps['items']>[number]

const MENU_ITEMS: MenuItem[] = [
  {
    key: '/dashboard',
    icon: <DashboardOutlined />,
    label: 'Dashboard',
  },
  {
    key: '/workspaces',
    icon: <ApartmentOutlined />,
    label: 'Workspaces',
  },
  {
    key: '/graphs',
    icon: <ShareAltOutlined />,
    label: 'Graphs',
  },
  {
    key: '/tasks',
    icon: <NodeIndexOutlined />,
    label: 'Tasks',
  },
  {
    key: '/results',
    icon: <BarChartOutlined />,
    label: 'Results',
  },
  {
    key: '/settings',
    icon: <SettingOutlined />,
    label: 'Settings',
  },
]

function AppLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const [messageApi, contextHolder] = message.useMessage()
  const collapsed = useAppShellStore((state) => state.collapsed)
  const toggleCollapsed = useAppShellStore((state) => state.toggleCollapsed)
  const userInfo = useAuthStore((state) => state.userInfo)
  const isLoggingOut = useAuthStore((state) => state.isLoggingOut)
  const logout = useAuthStore((state) => state.logout)

  const selectedMenuKey =
    MENU_ITEMS.find((item) => location.pathname.startsWith(String(item?.key)))?.key ??
    '/dashboard'

  const handleLogout = async () => {
    try {
      await logout()
    } catch {
      void messageApi.warning('退出接口未成功返回，但本地登录态已清理。')
    } finally {
      navigate('/login', { replace: true })
    }
  }

  return (
    <>
      {contextHolder}
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
                Optimization Console
              </Typography.Text>
            ) : null}
          </div>
          <Menu
            mode="inline"
            selectedKeys={[String(selectedMenuKey)]}
            items={MENU_ITEMS}
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
                  Authenticated workspace
                </Typography.Text>
              </div>
            </Space>
            <Space size="middle">
              <div style={{ textAlign: 'right' }}>
                <Typography.Text strong>
                  {userInfo?.nickname || userInfo?.username || '未登录'}
                </Typography.Text>
                <br />
                <Typography.Text type="secondary">
                  {userInfo?.roleCode || 'GUEST'}
                </Typography.Text>
              </div>
              <Tag color="blue">Phase 3</Tag>
              <Button
                icon={<LogoutOutlined />}
                onClick={() => void handleLogout()}
                loading={isLoggingOut}
              >
                退出
              </Button>
            </Space>
          </Header>
          <Content style={{ padding: 24 }}>
            <Outlet />
          </Content>
        </Layout>
      </Layout>
    </>
  )
}

export default AppLayout
