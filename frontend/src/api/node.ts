import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type { CreateNodeRequest, NodeQueryRequest, NodeVO, UpdateNodeRequest } from '../types/node'

export function createNode(graphId: ID, payload: CreateNodeRequest): Promise<NodeVO> {
  return request.post<NodeVO, CreateNodeRequest>(`/api/graphs/${graphId}/nodes`, payload)
}

export function getNodePage(graphId: ID, query?: NodeQueryRequest): Promise<PageResult<NodeVO>> {
  return request.get<PageResult<NodeVO>>(`/api/graphs/${graphId}/nodes`, { params: query })
}

export function getNodeDetail(graphId: ID, nodeId: ID): Promise<NodeVO> {
  return request.get<NodeVO>(`/api/graphs/${graphId}/nodes/${nodeId}`)
}

export function updateNode(graphId: ID, nodeId: ID, payload: UpdateNodeRequest): Promise<NodeVO> {
  return request.put<NodeVO, UpdateNodeRequest>(`/api/graphs/${graphId}/nodes/${nodeId}`, payload)
}

export function deleteNode(graphId: ID, nodeId: ID): Promise<boolean> {
  return request.delete<boolean>(`/api/graphs/${graphId}/nodes/${nodeId}`)
}
