const ACCESS_TOKEN_KEY = 'oab.auth.token'
const TOKEN_NAME_KEY = 'oab.auth.token-name'
const CURRENT_USER_KEY = 'oab.auth.user'

export const DEFAULT_TOKEN_NAME = 'satoken'

function getStorage() {
  if (typeof window === 'undefined') {
    return null
  }

  return window.localStorage
}

export function getAccessToken() {
  return getStorage()?.getItem(ACCESS_TOKEN_KEY) ?? null
}

export function getTokenName() {
  return getStorage()?.getItem(TOKEN_NAME_KEY) ?? DEFAULT_TOKEN_NAME
}

export function getStoredUser() {
  const rawUser = getStorage()?.getItem(CURRENT_USER_KEY)
  if (!rawUser) {
    return null
  }

  try {
    return JSON.parse(rawUser) as unknown
  } catch {
    return null
  }
}

export function setAccessToken(token: string, tokenName = DEFAULT_TOKEN_NAME) {
  const storage = getStorage()
  if (!storage) {
    return
  }

  storage.setItem(ACCESS_TOKEN_KEY, token)
  storage.setItem(TOKEN_NAME_KEY, tokenName)
}

export function setStoredUser(user: unknown) {
  const storage = getStorage()
  if (!storage) {
    return
  }

  storage.setItem(CURRENT_USER_KEY, JSON.stringify(user))
}

export function saveAuthSession(options: {
  token: string
  tokenName?: string
  user?: unknown
}) {
  setAccessToken(options.token, options.tokenName ?? DEFAULT_TOKEN_NAME)

  if (options.user !== undefined) {
    setStoredUser(options.user)
  }
}

export function clearAuthSession() {
  const storage = getStorage()
  if (!storage) {
    return
  }

  storage.removeItem(ACCESS_TOKEN_KEY)
  storage.removeItem(TOKEN_NAME_KEY)
  storage.removeItem(CURRENT_USER_KEY)
}

export function redirectToLogin() {
  if (typeof window === 'undefined') {
    return
  }

  if (window.location.pathname !== '/login') {
    window.location.assign('/login')
  }
}

export function clearAuthSessionAndRedirect() {
  clearAuthSession()
  redirectToLogin()
}
