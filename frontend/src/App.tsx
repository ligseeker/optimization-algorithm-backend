import { ConfigProvider, App as AntdApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Layouts
import MainLayout from './layouts/MainLayout';

// Pages
import LoginPage from './pages/Login';
import WorkspaceList from './pages/WorkspaceList';
import GraphList from './pages/GraphList';
import GraphDetail from './pages/GraphDetail';
import TaskCenter from './pages/TaskCenter';
import OptimizeResult from './pages/OptimizeResult';
import Dashboard from './pages/Dashboard';
import SettingsPage from './pages/Settings';

// Components
import AuthGuard from './components/AuthGuard';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      refetchOnWindowFocus: false,
    },
  },
});

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <Navigate to="/workspace" replace />,
  },
  {
    path: '/workspace',
    element: (
      <AuthGuard>
        <WorkspaceList />
      </AuthGuard>
    ),
  },
  {
    path: '/workspace/:workspaceId',
    element: (
      <AuthGuard>
        <MainLayout />
      </AuthGuard>
    ),
    children: [
      {
        path: 'dashboard',
        element: <Dashboard />,
      },
      {
        path: 'graph',
        element: <GraphList />,
      },
      {
        path: 'graph/:graphId',
        element: <GraphDetail />,
      },
      {
        path: 'task',
        element: <TaskCenter />,
      },
      {
        path: 'task/:taskId/result',
        element: <OptimizeResult />,
      },
      {
        path: 'settings',
        element: <SettingsPage />,
      },
    ],
  },
]);

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider locale={zhCN}>
        <AntdApp>
          <RouterProvider router={router} />
        </AntdApp>
      </ConfigProvider>
    </QueryClientProvider>
  );
}

export default App;
