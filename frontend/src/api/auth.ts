import { request } from './request'
import type { CurrentUserVO, LoginRequest, LoginResponseVO } from '../types/auth'
import { clearAuthSession, saveAuthSession } from '../utils/auth-token'

export async function login(payload: LoginRequest): Promise<LoginResponseVO> {
  const response = await request.post<LoginResponseVO, LoginRequest>('/api/auth/login', payload)

  saveAuthSession({
    token: response.token,
    tokenName: response.tokenName,
    user: {
      userId: response.userId,
      username: response.username,
      nickname: response.nickname,
      roleCode: response.roleCode,
    },
  })

  return response
}

export async function logout(): Promise<boolean> {
  const response = await request.post<boolean>('/api/auth/logout')
  clearAuthSession()
  return response
}

export function getCurrentUser(): Promise<CurrentUserVO> {
  return request.get<CurrentUserVO>('/api/auth/me')
}
