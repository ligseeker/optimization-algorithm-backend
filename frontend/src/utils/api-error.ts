import { ApiError } from '../types/common'

export const API_SUCCESS_CODE = 0
export const API_UNAUTHORIZED_CODE = 401001
export const HTTP_UNAUTHORIZED_STATUS = 401
export const HTTP_NETWORK_ERROR_CODE = -1
export const HTTP_UNKNOWN_ERROR_CODE = -2

type ApiErrorOptions = {
  code: number
  message: string
  status?: number
  details?: unknown
  cause?: unknown
}

export function createApiError(options: ApiErrorOptions) {
  return new ApiError(options.message, options)
}

export function createHttpError(
  status: number,
  message: string,
  details?: unknown,
  cause?: unknown,
) {
  return createApiError({
    code: status,
    message,
    status,
    details,
    cause,
  })
}

export function createNetworkError(message = 'Network request failed', cause?: unknown) {
  return createApiError({
    code: HTTP_NETWORK_ERROR_CODE,
    message,
    cause,
  })
}

export function createUnknownApiError(cause?: unknown) {
  return createApiError({
    code: HTTP_UNKNOWN_ERROR_CODE,
    message: 'Unexpected API response',
    cause,
  })
}

export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError
}

export function isUnauthorizedCode(code: number) {
  return code === API_UNAUTHORIZED_CODE
}

export function isUnauthorizedStatus(status?: number) {
  return status === HTTP_UNAUTHORIZED_STATUS
}
