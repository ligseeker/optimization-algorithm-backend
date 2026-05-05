import { Navigate, createBrowserRouter } from 'react-router-dom'
import AppLayout from '../layouts/app-layout'
import DashboardPage from '../pages/dashboard-page'
import GraphEditorPage from '../pages/graph-editor-page'
import GraphsPage from '../pages/graphs-page'
import LoginPage from '../pages/login-page'
import ResultsPage from '../pages/results-page'
import SettingsPage from '../pages/settings-page'
import TasksPage from '../pages/tasks-page'
import WorkspacesPage from '../pages/workspaces-page'

export const appRouter = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
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
    ],
  },
])
