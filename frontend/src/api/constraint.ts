import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type {
  ConstraintQueryRequest,
  ConstraintVO,
  CreateConstraintRequest,
  UpdateConstraintRequest,
} from '../types/constraint'

export function createConstraint(graphId: ID, payload: CreateConstraintRequest): Promise<ConstraintVO> {
  return request.post<ConstraintVO, CreateConstraintRequest>(
    `/api/graphs/${graphId}/constraints`,
    payload,
  )
}

export function getConstraintPage(
  graphId: ID,
  query?: ConstraintQueryRequest,
): Promise<PageResult<ConstraintVO>> {
  return request.get<PageResult<ConstraintVO>>(`/api/graphs/${graphId}/constraints`, {
    params: query,
  })
}

export function getConstraintDetail(graphId: ID, constraintId: ID): Promise<ConstraintVO> {
  return request.get<ConstraintVO>(`/api/graphs/${graphId}/constraints/${constraintId}`)
}

export function updateConstraint(
  graphId: ID,
  constraintId: ID,
  payload: UpdateConstraintRequest,
): Promise<ConstraintVO> {
  return request.put<ConstraintVO, UpdateConstraintRequest>(
    `/api/graphs/${graphId}/constraints/${constraintId}`,
    payload,
  )
}

export function deleteConstraint(graphId: ID, constraintId: ID): Promise<boolean> {
  return request.delete<boolean>(`/api/graphs/${graphId}/constraints/${constraintId}`)
}
