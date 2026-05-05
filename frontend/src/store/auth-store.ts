import { create } from 'zustand'
import { getCurrentUser, login as loginRequest, logout as logoutRequest } from '../api/auth'
import type { CurrentUserVO, LoginRequest } from '../types/auth'
import {
  DEFAULT_TOKEN_NAME,
  clearAuthSession,
  getAccessToken,
  getStoredUser,
  getTokenName,
  setStoredUser,
} from '../utils/auth-token'

type AuthState = {
  token: string | null
  tokenName: string
  userInfo: CurrentUserVO | null
  isAuthenticated: boolean
  isRestoring: boolean
  isSubmitting: boolean
  isLoggingOut: boolean
  lastError: string | null
}

type AuthActions = {
  login: (payload: LoginRequest) => Promise<CurrentUserVO>
  logout: () => Promise<void>
  restoreSession: () => Promise<void>
  clearSession: () => void
}

export type AuthStore = AuthState & AuthActions

function toCurrentUserVO(value: unknown): CurrentUserVO | null {
  if (!value || typeof value !== 'object') {
    return null
  }

  const candidate = value as Record<string, unknown>

  if (
    typeof candidate.userId !== 'number' ||
    typeof candidate.username !== 'string' ||
    typeof candidate.nickname !== 'string' ||
    typeof candidate.roleCode !== 'string'
  ) {
    return null
  }

  return {
    userId: candidate.userId,
    username: candidate.username,
    nickname: candidate.nickname,
    roleCode: candidate.roleCode,
  }
}

export function getInitialAuthState(): AuthState {
  const token = getAccessToken()
  const userInfo = toCurrentUserVO(getStoredUser())

  return {
    token,
    tokenName: token ? getTokenName() : DEFAULT_TOKEN_NAME,
    userInfo,
    isAuthenticated: false,
    isRestoring: Boolean(token),
    isSubmitting: false,
    isLoggingOut: false,
    lastError: null,
  }
}

export const useAuthStore = create<AuthStore>((set) => ({
  ...getInitialAuthState(),
  async login(payload) {
    set({
      isSubmitting: true,
      lastError: null,
    })

    try {
      const response = await loginRequest(payload)
      const userInfo: CurrentUserVO = {
        userId: response.userId,
        username: response.username,
        nickname: response.nickname,
        roleCode: response.roleCode,
      }

      setStoredUser(userInfo)

      set({
        token: response.token,
        tokenName: response.tokenName,
        userInfo,
        isAuthenticated: true,
        isRestoring: false,
        isSubmitting: false,
        lastError: null,
      })

      return userInfo
    } catch (error) {
      const message =
        error instanceof Error ? error.message : '登录失败，请稍后重试'

      set({
        isSubmitting: false,
        isAuthenticated: false,
        lastError: message,
      })

      throw error
    }
  },
  async logout() {
    set({
      isLoggingOut: true,
      lastError: null,
    })

    try {
      await logoutRequest()
    } finally {
      clearAuthSession()
      set({
        ...getInitialAuthState(),
        isLoggingOut: false,
      })
    }
  },
  async restoreSession() {
    const token = getAccessToken()

    if (!token) {
      set({
        ...getInitialAuthState(),
        isRestoring: false,
      })
      return
    }

    set({
      token,
      tokenName: getTokenName(),
      userInfo: toCurrentUserVO(getStoredUser()),
      isAuthenticated: false,
      isRestoring: true,
      lastError: null,
    })

    try {
      const userInfo = await getCurrentUser()
      setStoredUser(userInfo)

      set({
        token,
        tokenName: getTokenName(),
        userInfo,
        isAuthenticated: true,
        isRestoring: false,
        lastError: null,
      })
    } catch (error) {
      const message =
        error instanceof Error ? error.message : '登录态恢复失败，请重新登录'

      clearAuthSession()
      set({
        ...getInitialAuthState(),
        isRestoring: false,
        lastError: message,
      })
    }
  },
  clearSession() {
    clearAuthSession()
    set({
      ...getInitialAuthState(),
    })
  },
}))
