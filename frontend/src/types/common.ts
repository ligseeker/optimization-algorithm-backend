export type ID = number

export type ApiResult<T> = {
  code: number
  message: string
  data: T
}

export type PageResult<T> = {
  records: T[]
  pageNo: number
  pageSize: number
  total: number
}

export type PageQuery = {
  pageNo?: number
  pageSize?: number
}

export type EntityStatus = 0 | 1
export type EnabledStatus = 0 | 1

export type TaskStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'ALL'

export type DateTimeString = string

export class ApiError extends Error {
  readonly code: number
  readonly status?: number
  readonly details?: unknown

  constructor(
    message: string,
    options: {
      code: number
      status?: number
      details?: unknown
      cause?: unknown
    },
  ) {
    super(message, options.cause ? { cause: options.cause } : undefined)
    this.name = 'ApiError'
    this.code = options.code
    this.status = options.status
    this.details = options.details
  }
}
