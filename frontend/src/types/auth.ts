import type { ID } from './common'

export type LoginRequest = {
  username: string
  password: string
}

export type CurrentUserVO = {
  userId: ID
  username: string
  nickname: string
  roleCode: string
}

export type LoginResponseVO = CurrentUserVO & {
  token: string
  tokenName: string
}
