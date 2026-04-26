import React from 'react';
import { Layout, Menu, theme, Space, Avatar, Dropdown } from 'antd';
import {
  DashboardOutlined,
  BlockOutlined,
  LineChartOutlined,
  SettingOutlined,
  LogoutOutlined,
  UserOutlined,
  DesktopOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation, useParams } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

const { Header, Sider, Content } = Layout;

const MainLayout: React.FC = () => {
  const { userInfo, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();
  const { workspaceId } = useParams();

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  // 菜单项定义
  const menuItems = [
    {
      key: `/workspace/${workspaceId}/dashboard`,
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: `/workspace/${workspaceId}/graph`,
      icon: <BlockOutlined />,
      label: '流程图管理',
    },
    {
      key: `/workspace/${workspaceId}/task`,
      icon: <LineChartOutlined />,
      label: '优化任务',
    },
    {
      key: '/workspace',
      icon: <DesktopOutlined />,
      label: '切换工作空间',
    },
    {
      key: `/workspace/${workspaceId}/settings`,
      icon: <SettingOutlined />,
      label: '系统设置',
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider breakpoint="lg" collapsedWidth="0">
        <div
          style={{
            height: 32,
            margin: 16,
            background: 'rgba(255, 255, 255, 0.2)',
            borderRadius: 6,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 'bold',
          }}
        >
          OA SYSTEM
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            padding: '0 24px',
            background: colorBgContainer,
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center',
          }}
        >
          <Space size="middle">
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} />
                <span>{userInfo?.nickname || userInfo?.username}</span>
              </Space>
            </Dropdown>
          </Space>
        </Header>
        <Content style={{ margin: '24px 16px' }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
