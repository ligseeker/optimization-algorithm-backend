import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type {
  CreateGraphRequest,
  GraphDetailVO,
  GraphQueryRequest,
  GraphVO,
  UpdateGraphRequest,
} from '../types/graph'

export function createGraph(workspaceId: ID, payload: CreateGraphRequest): Promise<GraphVO> {
  return request.post<GraphVO, CreateGraphRequest>(`/api/workspaces/${workspaceId}/graphs`, payload)
}

export function getGraphPage(workspaceId: ID, query?: GraphQueryRequest): Promise<PageResult<GraphVO>> {
  return request.get<PageResult<GraphVO>>(`/api/workspaces/${workspaceId}/graphs`, {
    params: query,
  })
}

export function getGraphBase(graphId: ID): Promise<GraphVO> {
  return request.get<GraphVO>(`/api/graphs/${graphId}`)
}

export function getGraphDetail(graphId: ID): Promise<GraphDetailVO> {
  return request.get<GraphDetailVO>(`/api/graphs/${graphId}/detail`)
}

export function updateGraph(graphId: ID, payload: UpdateGraphRequest): Promise<GraphVO> {
  return request.put<GraphVO, UpdateGraphRequest>(`/api/graphs/${graphId}`, payload)
}

export function deleteGraph(graphId: ID): Promise<boolean> {
  return request.delete<boolean>(`/api/graphs/${graphId}`)
}
