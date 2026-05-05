import type { DateTimeString, EnabledStatus, ID, PageQuery } from './common'

export type CreateConstraintRequest = {
  conditionCode: string
  conditionType: string
  conditionDescription?: string
  nodeId1: ID
  nodeId2: ID
  enabled?: EnabledStatus
}

export type UpdateConstraintRequest = CreateConstraintRequest

export type ConstraintQueryRequest = PageQuery & {
  keyword?: string
}

export type ConstraintVO = {
  id: ID
  graphId: ID
  conditionCode: string
  conditionType: string
  conditionDescription: string | null
  nodeId1: ID
  nodeId2: ID
  enabled: EnabledStatus
  createdAt: DateTimeString
  updatedAt: DateTimeString
}
