import axios, {
  AxiosError,
  AxiosHeaders,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
} from 'axios'
import type { ApiResult } from '../types/common'
import {
  clearAuthSessionAndRedirect,
  getAccessToken,
  getTokenName,
} from '../utils/auth-token'
import {
  API_SUCCESS_CODE,
  createApiError,
  createHttpError,
  createNetworkError,
  createUnknownApiError,
  isUnauthorizedCode,
  isUnauthorizedStatus,
} from '../utils/api-error'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL

const DEFAULT_TIMEOUT = 15_000

function attachAuthHeader(config: InternalAxiosRequestConfig) {
  const token = getAccessToken()
  if (!token) {
    return config
  }

  const tokenName = getTokenName()
  const headers = AxiosHeaders.from(config.headers)
  headers.set(tokenName, token)
  config.headers = headers

  return config
}

function shouldClearSession(status?: number, code?: number) {
  return isUnauthorizedStatus(status) || (code !== undefined && isUnauthorizedCode(code))
}

function rejectApiError(error: AxiosError<ApiResult<unknown>>) {
  const status = error.response?.status
  const payload = error.response?.data

  if (payload && typeof payload.code === 'number') {
    if (shouldClearSession(status, payload.code)) {
      clearAuthSessionAndRedirect()
    }

    return Promise.reject(
      createApiError({
        code: payload.code,
        message: payload.message || error.message,
        status,
        details: payload.data,
        cause: error,
      }),
    )
  }

  if (shouldClearSession(status)) {
    clearAuthSessionAndRedirect()
  }

  if (status !== undefined) {
    return Promise.reject(
      createHttpError(status, error.message || 'HTTP request failed', error.response?.data, error),
    )
  }

  return Promise.reject(createNetworkError(error.message, error))
}

const resultClient = axios.create({
  baseURL: apiBaseUrl,
  timeout: DEFAULT_TIMEOUT,
})

const rawClient = axios.create({
  baseURL: apiBaseUrl,
  timeout: DEFAULT_TIMEOUT,
})

resultClient.interceptors.request.use(attachAuthHeader)
rawClient.interceptors.request.use(attachAuthHeader)

resultClient.interceptors.response.use(
  <T>(response: { data: ApiResult<T> }) => {
    const payload = response.data

    if (!payload || typeof payload.code !== 'number') {
      throw createUnknownApiError(payload)
    }

    if (payload.code !== API_SUCCESS_CODE) {
      if (shouldClearSession(undefined, payload.code)) {
        clearAuthSessionAndRedirect()
      }

      throw createApiError({
        code: payload.code,
        message: payload.message,
        details: payload.data,
      })
    }

    return payload.data
  },
  rejectApiError,
)

rawClient.interceptors.response.use(
  (response) => response.data,
  (error: AxiosError) => {
    const status = error.response?.status
    if (shouldClearSession(status)) {
      clearAuthSessionAndRedirect()
    }

    if (status !== undefined) {
      return Promise.reject(
        createHttpError(status, error.message || 'HTTP request failed', error.response?.data, error),
      )
    }

    return Promise.reject(createNetworkError(error.message, error))
  },
)

export const request = {
  get<T>(url: string, config?: AxiosRequestConfig) {
    return resultClient.get<ApiResult<T>, T>(url, config)
  },
  post<T, D = unknown>(url: string, data?: D, config?: AxiosRequestConfig<D>) {
    return resultClient.post<ApiResult<T>, T, D>(url, data, config)
  },
  put<T, D = unknown>(url: string, data?: D, config?: AxiosRequestConfig<D>) {
    return resultClient.put<ApiResult<T>, T, D>(url, data, config)
  },
  delete<T>(url: string, config?: AxiosRequestConfig) {
    return resultClient.delete<ApiResult<T>, T>(url, config)
  },
  request<T, D = unknown>(config: AxiosRequestConfig<D>) {
    return resultClient.request<ApiResult<T>, T, D>(config)
  },
}

export const rawRequest = {
  get<T>(url: string, config?: AxiosRequestConfig) {
    return rawClient.get<T, T>(url, config)
  },
  post<T, D = unknown>(url: string, data?: D, config?: AxiosRequestConfig<D>) {
    return rawClient.post<T, T, D>(url, data, config)
  },
  put<T, D = unknown>(url: string, data?: D, config?: AxiosRequestConfig<D>) {
    return rawClient.put<T, T, D>(url, data, config)
  },
  delete<T>(url: string, config?: AxiosRequestConfig) {
    return rawClient.delete<T, T>(url, config)
  },
  request<T, D = unknown>(config: AxiosRequestConfig<D>) {
    return rawClient.request<T, T, D>(config)
  },
}
