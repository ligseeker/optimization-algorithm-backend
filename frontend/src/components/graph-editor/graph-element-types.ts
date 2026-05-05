import type { ConstraintVO } from '../../types/constraint'
import type { EquipmentVO } from '../../types/equipment'
import type { NodeVO } from '../../types/node'
import type { PathVO } from '../../types/path'

export type GraphElementKind = 'node' | 'path' | 'equipment' | 'constraint'

export type GraphElementValue = NodeVO | PathVO | EquipmentVO | ConstraintVO

export type EditingGraphElement = {
  kind: GraphElementKind
  value?: GraphElementValue
}

export type GraphElementFormValues = {
  nodeCode?: string
  nodeName?: string
  nodeDescription?: string
  equipmentId?: number
  timeCost?: number
  precisionValue?: number
  costValue?: number
  sortNo?: number
  startNodeId?: number
  endNodeId?: number
  relationType?: string
  remark?: string
  name?: string
  description?: string
  color?: string
  imagePath?: string
  conditionCode?: string
  conditionType?: string
  conditionDescription?: string
  nodeId1?: number
  nodeId2?: number
  enabled?: 0 | 1
}
