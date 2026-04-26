import request from './request';
import type { PageResult } from '../types/api';

export interface OptimizeTask {
  taskId: number;
  taskNo: string;
  workspaceId: number;
  graphId: number;
  algorithmType: number;
  algorithmMode: number;
  timeWeight: number;
  precisionWeight: number;
  costWeight: number;
  taskStatus: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED';
  retryCount: number;
  queueTime: string;
  startedAt?: string;
  finishedAt?: string;
  errorCode?: string;
  errorMessage?: string;
}

export interface TaskCreateParams {
  graphId: number;
  algorithmType: number;
  algorithmMode: number;
  timeWeight: number;
  precisionWeight: number;
  costWeight: number;
}

/**
 * 提交优化任务
 */
export const createOptimizeTask = (data: TaskCreateParams): Promise<OptimizeTask> => {
  return request.post('/api/optimize/tasks', data);
};

/**
 * 获取任务列表
 */
export const getOptimizeTasks = (
  params?: Record<string, unknown>,
): Promise<PageResult<OptimizeTask>> => {
  return request.get('/api/optimize/tasks', { params });
};

/**
 * 获取任务详情/状态
 */
export const getTaskStatus = (taskId: number): Promise<OptimizeTask> => {
  return request.get(`/api/optimize/tasks/${taskId}`);
};

/**
 * 重试失败任务
 */
export const retryTask = (taskId: number, reason?: string): Promise<OptimizeTask> => {
  return request.post(`/api/optimize/tasks/${taskId}/retry`, { reason });
};

/**
 * 获取优化结果
 */
export const getOptimizeResult = (taskId: number): Promise<unknown> => {
  return request.get(`/api/optimize/tasks/${taskId}/result`);
};
