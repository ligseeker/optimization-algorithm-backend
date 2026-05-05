import { beforeEach, describe, expect, it } from 'vitest'
import { getInitialAuthState } from './auth-store'

describe('getInitialAuthState', () => {
  beforeEach(() => {
    window.localStorage.clear()
  })

  it('starts unauthenticated when no local session exists', () => {
    expect(getInitialAuthState()).toMatchObject({
      token: null,
      tokenName: 'satoken',
      userInfo: null,
      isAuthenticated: false,
      isRestoring: false,
      isSubmitting: false,
      isLoggingOut: false,
      lastError: null,
    })
  })
})
