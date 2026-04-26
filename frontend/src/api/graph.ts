import request from './request';
import type { PageResult } from '../types/api';

export interface FlowGraph {
  id: number;
  workspaceId: number;
  name: string;
  description: string;
  sourceType: 'MANUAL' | 'YAML_IMPORT';
  graphStatus: 'DRAFT' | 'RELEASED';
  totalTime?: number;
  totalPrecision?: number;
  totalCost?: number;
  createdAt: string;
}

export interface GraphCreateParams {
  name: string;
  description?: string;
  sourceType?: string;
}

/**
 * 分页获取工作空间下的流程图
 */
export const getGraphs = (
  workspaceId: string | number,
  params?: Record<string, unknown>,
): Promise<PageResult<FlowGraph>> => {
  return request.get(`/api/workspaces/${workspaceId}/graphs`, { params });
};

/**
 * 创建流程图
 */
export const createGraph = (
  workspaceId: string | number,
  data: GraphCreateParams,
): Promise<FlowGraph> => {
  return request.post(`/api/workspaces/${workspaceId}/graphs`, data);
};

/**
 * 更新流程图基础信息
 */
export const updateGraph = (
  graphId: number,
  data: Partial<GraphCreateParams>,
): Promise<FlowGraph> => {
  return request.put(`/api/graphs/${graphId}`, data);
};

/**
 * 删除流程图
 */
export const deleteGraph = (graphId: number): Promise<boolean> => {
  return request.delete(`/api/graphs/${graphId}`);
};

/**
 * 导入 YAML
 */
export const importYaml = (file: File, graphName?: string): Promise<unknown> => {
  const formData = new FormData();
  formData.append('file', file);
  if (graphName) {
    formData.append('graphName', graphName);
  }
  return request.post('/api/import/graphs', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

/**
 * 导出流程图 YAML
 */
export const exportGraphYaml = (graphId: number): Promise<Blob> => {
  return request.get(`/api/export/graphs/${graphId}/yaml`, {
    responseType: 'blob',
  });
};
