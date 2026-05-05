import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type { CreatePathRequest, PathQueryRequest, PathVO, UpdatePathRequest } from '../types/path'

export function createPath(graphId: ID, payload: CreatePathRequest): Promise<PathVO> {
  return request.post<PathVO, CreatePathRequest>(`/api/graphs/${graphId}/paths`, payload)
}

export function getPathPage(graphId: ID, query?: PathQueryRequest): Promise<PageResult<PathVO>> {
  return request.get<PageResult<PathVO>>(`/api/graphs/${graphId}/paths`, { params: query })
}

export function getPathDetail(graphId: ID, pathId: ID): Promise<PathVO> {
  return request.get<PathVO>(`/api/graphs/${graphId}/paths/${pathId}`)
}

export function updatePath(graphId: ID, pathId: ID, payload: UpdatePathRequest): Promise<PathVO> {
  return request.put<PathVO, UpdatePathRequest>(`/api/graphs/${graphId}/paths/${pathId}`, payload)
}

export function deletePath(graphId: ID, pathId: ID): Promise<boolean> {
  return request.delete<boolean>(`/api/graphs/${graphId}/paths/${pathId}`)
}
