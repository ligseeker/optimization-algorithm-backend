import type { DateTimeString, ID, PageQuery } from './common'
import type { ConstraintVO } from './constraint'
import type { EquipmentVO } from './equipment'
import type { NodeVO } from './node'
import type { PathVO } from './path'

export type CreateGraphRequest = {
  name: string
  description?: string
  sourceType?: string
  graphStatus?: string
}

export type UpdateGraphRequest = {
  name: string
  description?: string
  graphStatus?: string
  totalTime?: number
  totalPrecision?: number
  totalCost?: number
}

export type GraphQueryRequest = PageQuery & {
  keyword?: string
}

export type GraphVO = {
  id: ID
  workspaceId: ID
  name: string
  description: string | null
  sourceType: string | null
  graphStatus: string | null
  graphVersion: number | null
  totalTime: number | null
  totalPrecision: number | null
  totalCost: number | null
  createdAt: DateTimeString
  updatedAt: DateTimeString
}

export type GraphDetailVO = {
  graph: GraphVO
  nodes: NodeVO[]
  paths: PathVO[]
  equipments: EquipmentVO[]
  constraints: ConstraintVO[]
}
