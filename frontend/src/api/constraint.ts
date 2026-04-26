import request from './request';

export interface ConstraintCondition {
  id: number;
  graphId: number;
  conditionCode: string;
  conditionType: 'FOLLOW' | 'CONNECT' | 'LIMIT';
  conditionDescription?: string;
  nodeId1: number;
  nodeId2: number;
  enabled: number;
}

/**
 * 新增约束
 */
export const createConstraint = (
  graphId: string | number,
  data: Partial<ConstraintCondition>,
): Promise<ConstraintCondition> => {
  return request.post(`/api/graphs/${graphId}/constraints`, data);
};

/**
 * 更新约束
 */
export const updateConstraint = (
  graphId: string | number,
  constraintId: number,
  data: Partial<ConstraintCondition>,
): Promise<ConstraintCondition> => {
  return request.put(`/api/graphs/${graphId}/constraints/${constraintId}`, data);
};

/**
 * 删除约束
 */
export const deleteConstraint = (
  graphId: string | number,
  constraintId: number,
): Promise<boolean> => {
  return request.delete(`/api/graphs/${graphId}/constraints/${constraintId}`);
};
