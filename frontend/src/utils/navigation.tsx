import {
  ApartmentOutlined,
  BarChartOutlined,
  DashboardOutlined,
  EditOutlined,
  NodeIndexOutlined,
  SettingOutlined,
  ShareAltOutlined,
} from '@ant-design/icons'
import type { ReactNode } from 'react'

export type AppNavItem = {
  label: string
  path: string
  icon: ReactNode
}

export const APP_NAV_ITEMS: AppNavItem[] = [
  {
    label: 'Dashboard',
    path: '/dashboard',
    icon: <DashboardOutlined />,
  },
  {
    label: 'Workspaces',
    path: '/workspaces',
    icon: <ApartmentOutlined />,
  },
  {
    label: 'Graphs',
    path: '/graphs',
    icon: <ShareAltOutlined />,
  },
  {
    label: 'Graph Editor',
    path: '/graph-editor',
    icon: <EditOutlined />,
  },
  {
    label: 'Tasks',
    path: '/tasks',
    icon: <NodeIndexOutlined />,
  },
  {
    label: 'Results',
    path: '/results',
    icon: <BarChartOutlined />,
  },
  {
    label: 'Settings',
    path: '/settings',
    icon: <SettingOutlined />,
  },
]
