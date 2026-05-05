import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from '../../store/auth-store'
import FullPageSpin from '../common/full-page-spin'

export function GuestRoute() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  const isRestoring = useAuthStore((state) => state.isRestoring)

  if (isRestoring) {
    return <FullPageSpin tip="正在恢复登录状态..." />
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  return <Outlet />
}

export function ProtectedRoute() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  const isRestoring = useAuthStore((state) => state.isRestoring)

  if (isRestoring) {
    return <FullPageSpin tip="正在恢复登录状态..." />
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <Outlet />
}
