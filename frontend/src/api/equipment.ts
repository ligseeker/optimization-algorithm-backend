import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type {
  CreateEquipmentRequest,
  EquipmentQueryRequest,
  EquipmentVO,
  UpdateEquipmentRequest,
} from '../types/equipment'

export function createEquipment(graphId: ID, payload: CreateEquipmentRequest): Promise<EquipmentVO> {
  return request.post<EquipmentVO, CreateEquipmentRequest>(`/api/graphs/${graphId}/equipments`, payload)
}

export function getEquipmentPage(
  graphId: ID,
  query?: EquipmentQueryRequest,
): Promise<PageResult<EquipmentVO>> {
  return request.get<PageResult<EquipmentVO>>(`/api/graphs/${graphId}/equipments`, {
    params: query,
  })
}

export function getEquipmentDetail(graphId: ID, equipmentId: ID): Promise<EquipmentVO> {
  return request.get<EquipmentVO>(`/api/graphs/${graphId}/equipments/${equipmentId}`)
}

export function updateEquipment(
  graphId: ID,
  equipmentId: ID,
  payload: UpdateEquipmentRequest,
): Promise<EquipmentVO> {
  return request.put<EquipmentVO, UpdateEquipmentRequest>(
    `/api/graphs/${graphId}/equipments/${equipmentId}`,
    payload,
  )
}

export function deleteEquipment(graphId: ID, equipmentId: ID): Promise<boolean> {
  return request.delete<boolean>(`/api/graphs/${graphId}/equipments/${equipmentId}`)
}
