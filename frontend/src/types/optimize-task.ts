import type { DateTimeString, ID, PageQuery, TaskStatus } from './common'

export type CreateOptimizeTaskRequest = {
  graphId: ID
  algorithmType: number
  algorithmMode: number
  timeWeight?: number
  precisionWeight?: number
  costWeight?: number
}

export type OptimizeTaskQueryRequest = PageQuery & {
  workspaceId?: ID
  graphId?: ID
  taskStatus?: TaskStatus | ''
}

export type OptimizeTaskSubmitVO = {
  taskId: ID
  taskNo: string
  taskStatus: TaskStatus
}

export type OptimizeTaskVO = {
  id: ID
  taskNo: string
  workspaceId: ID
  graphId: ID
  userId: ID
  algorithmType: number
  algorithmMode: number
  timeWeight: number
  precisionWeight: number
  costWeight: number
  taskStatus: TaskStatus
  retryCount: number
  maxRetryCount: number
  queueTime: DateTimeString | null
  startedAt: DateTimeString | null
  finishedAt: DateTimeString | null
  errorCode: string | null
  errorMessage: string | null
  resultId: ID | null
  createdAt: DateTimeString
  updatedAt: DateTimeString
}

export type OptimizeResultVO = {
  id: ID
  taskId: ID
  workspaceId: ID
  sourceGraphId: ID
  resultName: string
  resultGraph: unknown
  diff: unknown
  mapCode: string
  totalTimeBefore: number
  totalPrecisionBefore: number
  totalCostBefore: number
  totalTimeAfter: number
  totalPrecisionAfter: number
  totalCostAfter: number
  scoreRatio: number
  createdAt: DateTimeString
}
