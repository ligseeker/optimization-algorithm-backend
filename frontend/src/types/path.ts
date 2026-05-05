import type { DateTimeString, ID, PageQuery } from './common'

export type CreatePathRequest = {
  startNodeId: ID
  endNodeId: ID
  relationType?: string
  remark?: string
}

export type UpdatePathRequest = CreatePathRequest

export type PathQueryRequest = PageQuery

export type PathVO = {
  id: ID
  graphId: ID
  startNodeId: ID
  endNodeId: ID
  relationType: string | null
  remark: string | null
  createdAt: DateTimeString
  updatedAt: DateTimeString
}
