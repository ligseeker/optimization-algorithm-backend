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
import { Button, Layout, Menu, Typography, message } from 'antd'
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
      <Layout className="console-layout">
        <Sider
          collapsible
          collapsed={collapsed}
          trigger={null}
          width={260}
          collapsedWidth={84}
          theme="light"
          className="console-sider"
        >
          <div className="console-brand">
            <div className="console-brand-mark">OAB</div>
            {!collapsed ? (
              <div className="console-brand-copy">
                <Typography.Text className="console-brand-title">
                  Control Deck
                </Typography.Text>
                <Typography.Text className="console-brand-subtitle">
                  Industrial optimization console
                </Typography.Text>
              </div>
            ) : null}
          </div>
          <Menu
            mode="inline"
            selectedKeys={[String(selectedMenuKey)]}
            items={MENU_ITEMS}
            onClick={({ key }) => navigate(key)}
            className="console-menu"
          />
          {!collapsed ? (
            <div className="console-sider-footer">
              <Typography.Text className="console-sider-footer-label">
                Session Focus
              </Typography.Text>
              <Typography.Text>
                Workspaces, graph orchestration, task telemetry, and result review.
              </Typography.Text>
            </div>
          ) : null}
        </Sider>
        <Layout>
          <Header className="console-header">
            <div className="console-header-cluster">
              <Button
                type="text"
                icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                onClick={toggleCollapsed}
                aria-label="Toggle menu"
              />
              <div className="console-header-copy">
                <Typography.Text className="console-header-kicker">
                  Operations Surface
                </Typography.Text>
                <Typography.Text strong>
                  optimization-algorithm-backend
                </Typography.Text>
                <Typography.Text type="secondary">
                  Structured control for graphs, tasks, and optimization output.
                </Typography.Text>
              </div>
            </div>
            <div className="console-header-cluster">
              <div className="console-status-pill">
                <span className="console-status-dot" />
                Live operations
              </div>
              <div className="console-user-card">
                <Typography.Text strong>
                  {userInfo?.nickname || userInfo?.username || '未登录'}
                </Typography.Text>
                <Typography.Text type="secondary">
                  {userInfo?.roleCode || 'GUEST'}
                </Typography.Text>
              </div>
              <Button
                type="primary"
                icon={<LogoutOutlined />}
                onClick={() => void handleLogout()}
                loading={isLoggingOut}
              >
                退出
              </Button>
            </div>
          </Header>
          <Content className="console-content">
            <div className="console-page-frame">
              <Outlet />
            </div>
          </Content>
        </Layout>
      </Layout>
    </>
  )
}

export default AppLayout
