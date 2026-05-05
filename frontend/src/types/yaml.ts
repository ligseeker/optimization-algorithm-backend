import type { ID } from './common'

export type GraphImportRequest = {
  workspaceId?: ID
  graphName?: string
}

export type GraphImportResponse = {
  graphId: ID
  workspaceId: ID
  graphName: string
  sourceType: string
  nodeCount: number
  pathCount: number
  equipmentCount: number
  constraintCount: number
}

export type GraphYamlExportResponse = {
  graphId: ID
  graphName: string
  fileName: string
  yamlContent: string
}
