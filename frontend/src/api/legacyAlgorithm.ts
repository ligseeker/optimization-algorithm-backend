import { rawRequest } from './request'

type LegacyParams = Record<string, string | number | boolean | null | undefined>
type LegacyJsonPayload = Record<string, unknown>

export function optimizeByFile(payload: FormData): Promise<unknown> {
  return rawRequest.post<unknown, FormData>('/optimizeByFile', payload, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function optimizeByInput(payload: LegacyJsonPayload): Promise<unknown> {
  return rawRequest.post<unknown, LegacyJsonPayload>('/optimizeByInput', payload)
}

export function uploadFile(payload: FormData): Promise<unknown> {
  return rawRequest.post<unknown, FormData>('/uploadFile', payload, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function downloadFile(params?: LegacyParams): Promise<Blob> {
  return rawRequest.get<Blob>('/downloadFile', {
    params,
    responseType: 'blob',
  })
}
