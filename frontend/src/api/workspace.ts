import request from './request';

export interface Workspace {
  id: number;
  name: string;
  description: string;
  ownerUserId: number;
  status: number;
  createdAt: string;
}

export interface WorkspaceCreateParams {
  name: string;
  description?: string;
}

import type { PageResult } from '../types/api';

/**
 * 获取工作空间列表
 */
export const getWorkspaces = (params?: any): Promise<PageResult<Workspace>> => {
  return request.get('/api/workspaces', { params });
};

/**
 * 获取工作空间详情
 */
export const getWorkspaceDetail = (id: number): Promise<Workspace> => {
  return request.get(`/api/workspaces/${id}`);
};

/**
 * 创建工作空间
 */
export const createWorkspace = (data: WorkspaceCreateParams): Promise<Workspace> => {
  return request.post('/api/workspaces', data);
};

/**
 * 更新工作空间
 */
export const updateWorkspace = (id: number, data: WorkspaceCreateParams): Promise<Workspace> => {
  return request.put(`/api/workspaces/${id}`, data);
};

/**
 * 删除工作空间
 */
export const deleteWorkspace = (id: number): Promise<boolean> => {
  return request.delete(`/api/workspaces/${id}`);
};
