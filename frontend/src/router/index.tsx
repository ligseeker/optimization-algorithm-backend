import { Navigate, createBrowserRouter } from 'react-router-dom'
import { GuestRoute, ProtectedRoute } from '../components/app/route-guards'
import NotFoundState from '../components/common/not-found-state'
import UnauthorizedState from '../components/common/unauthorized-state'
import AppLayout from '../layouts/app-layout'
import LoginPage from '../pages/auth/login-page'
import DashboardPage from '../pages/dashboard-page'
import GraphEditorPage from '../pages/graph-editor/graph-editor-page'
import GraphDetailPage from '../pages/graphs/graph-detail-page'
import GraphsPage from '../pages/graphs/graphs-page'
import OptimizeResultPage from '../pages/results/optimize-result-page'
import ResultsPage from '../pages/results-page'
import SettingsPage from '../pages/settings-page'
import TaskDetailPage from '../pages/tasks/task-detail-page'
import TasksPage from '../pages/tasks/tasks-page'
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
            path: 'graphs/:graphId/detail',
            element: <GraphDetailPage />,
          },
          {
            path: 'graphs/:graphId/editor',
            element: <GraphEditorPage />,
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
            path: 'tasks/:taskId',
            element: <TaskDetailPage />,
          },
          {
            path: 'tasks/:taskId/result',
            element: <OptimizeResultPage />,
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
