import request from './request';

export interface ProcessNode {
  id: number;
  graphId: number;
  nodeCode: string;
  nodeName: string;
  nodeDescription?: string;
  equipmentId?: number;
  timeCost: number;
  precisionValue: number;
  costValue: number;
  sortNo?: number;
  positionX?: number;
  positionY?: number;
}

/**
 * 新增节点
 */
export const createNode = (
  graphId: string | number,
  data: Partial<ProcessNode>,
): Promise<ProcessNode> => {
  return request.post(`/api/graphs/${graphId}/nodes`, data);
};

/**
 * 修改节点
 */
export const updateNode = (
  graphId: string | number,
  nodeId: number,
  data: Partial<ProcessNode>,
): Promise<ProcessNode> => {
  return request.put(`/api/graphs/${graphId}/nodes/${nodeId}`, data);
};

/**
 * 删除节点
 */
export const deleteNode = (graphId: string | number, nodeId: number): Promise<boolean> => {
  return request.delete(`/api/graphs/${graphId}/nodes/${nodeId}`);
};
