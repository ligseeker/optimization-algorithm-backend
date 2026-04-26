/**
 * 通用响应结构
 */
export interface Result<T = unknown> {
  code: number;
  message: string;
  data: T;
}

/**
 * 分页数据结构
 */
export interface PageResult<T> {
  records: T[];
  pageNo: number;
  pageSize: number;
  total: number;
}

/**
 * 业务错误码定义
 */
export const ErrorCode = {
  SUCCESS: 0,
  PARAM_ERROR: 400001,
  UNAUTHORIZED: 401001,
  FORBIDDEN: 403001,
  NOT_FOUND: 404001,
  CONFLICT: 409001,
  SYSTEM_ERROR: 500001,
  TASK_FAILED: 600001,
  YAML_ERROR: 700001,
} as const;

export type ErrorCodeType = (typeof ErrorCode)[keyof typeof ErrorCode];
