import request from './request';

export interface Equipment {
  id: number;
  graphId: number;
  name: string;
  description?: string;
  color?: string;
  imagePath?: string;
}

/**
 * 新增装备
 */
export const createEquipment = (
  graphId: string | number,
  data: Partial<Equipment>,
): Promise<Equipment> => {
  return request.post(`/api/graphs/${graphId}/equipments`, data);
};

/**
 * 更新装备
 */
export const updateEquipment = (
  graphId: string | number,
  equipmentId: number,
  data: Partial<Equipment>,
): Promise<Equipment> => {
  return request.put(`/api/graphs/${graphId}/equipments/${equipmentId}`, data);
};

/**
 * 删除装备
 */
export const deleteEquipment = (
  graphId: string | number,
  equipmentId: number,
): Promise<boolean> => {
  return request.delete(`/api/graphs/${graphId}/equipments/${equipmentId}`);
};
