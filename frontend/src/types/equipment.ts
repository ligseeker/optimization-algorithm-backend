import type { DateTimeString, ID, PageQuery } from './common'

export type CreateEquipmentRequest = {
  name: string
  description?: string
  color?: string
  imagePath?: string
}

export type UpdateEquipmentRequest = CreateEquipmentRequest

export type EquipmentQueryRequest = PageQuery & {
  keyword?: string
}

export type EquipmentVO = {
  id: ID
  graphId: ID
  name: string
  description: string | null
  color: string | null
  imagePath: string | null
  createdAt: DateTimeString
  updatedAt: DateTimeString
}
