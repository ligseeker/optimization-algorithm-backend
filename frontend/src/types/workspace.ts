import type { DateTimeString, EntityStatus, ID, PageQuery } from './common'

export type CreateWorkspaceRequest = {
  name: string
  description?: string
}

export type UpdateWorkspaceRequest = {
  name: string
  description?: string
  status?: EntityStatus
}

export type WorkspaceQueryRequest = PageQuery & {
  keyword?: string
}

export type WorkspaceVO = {
  id: ID
  ownerUserId: ID
  name: string
  description: string | null
  status: EntityStatus
  createdAt: DateTimeString
  updatedAt: DateTimeString
}
