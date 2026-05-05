import { request } from './request'
import type { ID } from '../types/common'
import type {
  GraphImportRequest,
  GraphImportResponse,
  GraphYamlExportResponse,
} from '../types/yaml'

export type ImportGraphYamlPayload = GraphImportRequest & {
  file: File | Blob
}

export async function importGraphYaml(
  payload: ImportGraphYamlPayload,
): Promise<GraphImportResponse> {
  const formData = new FormData()
  formData.append('file', payload.file)

  if (payload.workspaceId !== undefined) {
    formData.append('workspaceId', String(payload.workspaceId))
  }

  if (payload.graphName) {
    formData.append('graphName', payload.graphName)
  }

  return request.post<GraphImportResponse, FormData>('/api/import/graphs', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function exportGraphYaml(graphId: ID): Promise<GraphYamlExportResponse> {
  return request.get<GraphYamlExportResponse>(`/api/export/graphs/${graphId}/yaml`)
}
