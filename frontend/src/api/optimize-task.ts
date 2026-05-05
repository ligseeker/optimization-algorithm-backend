import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type {
  CreateOptimizeTaskRequest,
  OptimizeResultVO,
  OptimizeTaskQueryRequest,
  OptimizeTaskSubmitVO,
  OptimizeTaskVO,
} from '../types/optimize-task'

export function createOptimizeTask(
  payload: CreateOptimizeTaskRequest,
): Promise<OptimizeTaskSubmitVO> {
  return request.post<OptimizeTaskSubmitVO, CreateOptimizeTaskRequest>(
    '/api/optimize/tasks',
    payload,
  )
}

export function getOptimizeTaskPage(
  query?: OptimizeTaskQueryRequest,
): Promise<PageResult<OptimizeTaskVO>> {
  return request.get<PageResult<OptimizeTaskVO>>('/api/optimize/tasks', { params: query })
}

export function getOptimizeTaskDetail(taskId: ID): Promise<OptimizeTaskVO> {
  return request.get<OptimizeTaskVO>(`/api/optimize/tasks/${taskId}`)
}

export function getOptimizeResult(taskId: ID): Promise<OptimizeResultVO> {
  return request.get<OptimizeResultVO>(`/api/optimize/tasks/${taskId}/result`)
}

export function retryOptimizeTask(taskId: ID): Promise<OptimizeTaskSubmitVO> {
  return request.post<OptimizeTaskSubmitVO>(`/api/optimize/tasks/${taskId}/retry`)
}
