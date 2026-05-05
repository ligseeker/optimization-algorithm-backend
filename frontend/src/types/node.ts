import type { DateTimeString, ID, PageQuery } from './common'

export type CreateNodeRequest = {
  nodeCode: string
  nodeName?: string
  nodeDescription?: string
  equipmentId?: ID
  timeCost?: number
  precisionValue?: number
  costValue?: number
  sortNo?: number
}

export type UpdateNodeRequest = CreateNodeRequest

export type NodeQueryRequest = PageQuery & {
  keyword?: string
}

export type NodeVO = {
  id: ID
  graphId: ID
  nodeCode: string
  nodeName: string | null
  nodeDescription: string | null
  equipmentId: ID | null
  timeCost: number | null
  precisionValue: number | null
  costValue: number | null
  sortNo: number | null
  createdAt: DateTimeString
  updatedAt: DateTimeString
}
