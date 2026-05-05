import { request } from './request'
import type { ID, PageResult } from '../types/common'
import type {
  CreateWorkspaceRequest,
  UpdateWorkspaceRequest,
  WorkspaceQueryRequest,
  WorkspaceVO,
} from '../types/workspace'

export function createWorkspace(payload: CreateWorkspaceRequest): Promise<WorkspaceVO> {
  return request.post<WorkspaceVO, CreateWorkspaceRequest>('/api/workspaces', payload)
}

export function getWorkspacePage(query?: WorkspaceQueryRequest): Promise<PageResult<WorkspaceVO>> {
  return request.get<PageResult<WorkspaceVO>>('/api/workspaces', { params: query })
}

export function getWorkspaceDetail(workspaceId: ID): Promise<WorkspaceVO> {
  return request.get<WorkspaceVO>(`/api/workspaces/${workspaceId}`)
}

export function updateWorkspace(workspaceId: ID, payload: UpdateWorkspaceRequest): Promise<WorkspaceVO> {
  return request.put<WorkspaceVO, UpdateWorkspaceRequest>(`/api/workspaces/${workspaceId}`, payload)
}

export function deleteWorkspace(workspaceId: ID): Promise<boolean> {
  return request.delete<boolean>(`/api/workspaces/${workspaceId}`)
}
