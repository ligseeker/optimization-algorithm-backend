import { Tag } from 'antd'

type TaskStatusTagProps = {
  status: string
}

const STATUS_COLOR: Record<string, string> = {
  PENDING: 'gold',
  CREATED: 'gold',
  RUNNING: 'processing',
  SUCCESS: 'success',
  FAILED: 'error',
  CANCELED: 'default',
  ALL: 'default',
}

function TaskStatusTag({ status }: TaskStatusTagProps) {
  return <Tag color={STATUS_COLOR[status] ?? 'default'}>{status || '-'}</Tag>
}

export default TaskStatusTag
