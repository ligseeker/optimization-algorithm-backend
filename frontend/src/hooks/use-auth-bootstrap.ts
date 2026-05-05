import { useEffect } from 'react'
import { useAuthStore } from '../store/auth-store'

let hasBootstrappedAuthSession = false

export function useAuthBootstrap() {
  const restoreSession = useAuthStore((state) => state.restoreSession)

  useEffect(() => {
    if (hasBootstrappedAuthSession) {
      return
    }

    hasBootstrappedAuthSession = true
    void restoreSession()
  }, [restoreSession])
}
