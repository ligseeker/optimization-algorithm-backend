import request from './request';

export interface ProcessPath {
  id: number;
  graphId: number;
  startNodeId: number;
  endNodeId: number;
  relationType: string;
  remark?: string;
}

/**
 * 新增路径
 */
export const createPath = (
  graphId: string | number,
  data: Partial<ProcessPath>,
): Promise<ProcessPath> => {
  return request.post(`/api/graphs/${graphId}/paths`, data);
};

/**
 * 删除路径
 */
export const deletePath = (graphId: string | number, pathId: number): Promise<boolean> => {
  return request.delete(`/api/graphs/${graphId}/paths/${pathId}`);
};
