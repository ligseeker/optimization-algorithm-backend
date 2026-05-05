import { Navigate, createBrowserRouter } from 'react-router-dom'
import { GuestRoute, ProtectedRoute } from '../components/app/route-guards'
import NotFoundState from '../components/common/not-found-state'
import UnauthorizedState from '../components/common/unauthorized-state'
import AppLayout from '../layouts/app-layout'
import LoginPage from '../pages/auth/login-page'
import DashboardPage from '../pages/dashboard-page'
import GraphEditorPage from '../pages/graph-editor-page'
import GraphsPage from '../pages/graphs-page'
import ResultsPage from '../pages/results-page'
import SettingsPage from '../pages/settings-page'
import TasksPage from '../pages/tasks-page'
import WorkspacesPage from '../pages/workspaces/workspaces-page'

export const appRouter = createBrowserRouter([
  {
    element: <GuestRoute />,
    children: [
      {
        path: '/login',
        element: <LoginPage />,
      },
    ],
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: '/',
        element: <AppLayout />,
        children: [
          {
            index: true,
            element: <Navigate to="/dashboard" replace />,
          },
          {
            path: 'dashboard',
            element: <DashboardPage />,
          },
          {
            path: 'workspaces',
            element: <WorkspacesPage />,
          },
          {
            path: 'workspaces/:workspaceId/graphs',
            element: <GraphsPage />,
          },
          {
            path: 'graphs',
            element: <GraphsPage />,
          },
          {
            path: 'graph-editor',
            element: <GraphEditorPage />,
          },
          {
            path: 'tasks',
            element: <TasksPage />,
          },
          {
            path: 'results',
            element: <ResultsPage />,
          },
          {
            path: 'settings',
            element: <SettingsPage />,
          },
          {
            path: 'unauthorized',
            element: <UnauthorizedState />,
          },
        ],
      },
    ],
  },
  {
    path: '*',
    element: <NotFoundState />,
  },
])
