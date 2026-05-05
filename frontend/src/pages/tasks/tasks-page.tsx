import {
  EyeOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Card,
  Empty,
  InputNumber,
  Select,
  Space,
  Table,
  Typography,
  message,
} from 'antd'
import type { TableColumnsType, TablePaginationConfig } from 'antd'
import { useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import {
  createOptimizeTask,
  getOptimizeTaskPage,
} from '../../api/optimize-task'
import OptimizeTaskFormModal from '../../components/tasks/optimize-task-form-modal'
import type { OptimizeTaskFormValues } from '../../components/tasks/optimize-task-form-modal'
import TaskStatusTag from '../../components/tasks/task-status-tag'
import { useDocumentTitle } from '../../hooks/use-document-title'
import type { TaskStatus } from '../../types/common'
import type { OptimizeTaskVO } from '../../types/optimize-task'

const DEFAULT_PAGE_SIZE = 10

type TaskStatusFilter = '' | Exclude<TaskStatus, 'ALL'>

const TASK_STATUS_OPTIONS: Array<{ label: string; value: TaskStatusFilter }> = [
  { label: '全部', value: '' },
  { label: 'PENDING', value: 'PENDING' },
  { label: 'RUNNING', value: 'RUNNING' },
  { label: 'SUCCESS', value: 'SUCCESS' },
  { label: 'FAILED', value: 'FAILED' },
]

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function getNumericParam(value: string | null) {
  const numericValue = Number(value)
  return Number.isFinite(numericValue) && numericValue > 0 ? numericValue : undefined
}

function TasksPage() {
  useDocumentTitle('Tasks')

  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const queryClient = useQueryClient()
  const [messageApi, messageContextHolder] = message.useMessage()
  const [pageNo, setPageNo] = useState(1)
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE)
  const [workspaceId, setWorkspaceId] = useState<number | undefined>(
    getNumericParam(searchParams.get('workspaceId')),
  )
  const [graphId, setGraphId] = useState<number | undefined>(
    getNumericParam(searchParams.get('graphId')),
  )
  const [taskStatus, setTaskStatus] = useState<TaskStatusFilter>('')
  const [isCreateOpen, setIsCreateOpen] = useState(false)

  const taskQuery = useQuery({
    queryKey: ['optimize-tasks', { pageNo, pageSize, workspaceId, graphId, taskStatus }],
    queryFn: () =>
      getOptimizeTaskPage({
        pageNo,
        pageSize,
        workspaceId,
        graphId,
        taskStatus,
      }),
    refetchInterval: (query) =>
      query.state.data?.records.some((task) =>
        ['PENDING', 'RUNNING'].includes(task.taskStatus),
      )
        ? 3000
        : false,
  })

  const createMutation = useMutation({
    mutationFn: createOptimizeTask,
    onSuccess: async (result) => {
      setIsCreateOpen(false)
      await queryClient.invalidateQueries({ queryKey: ['optimize-tasks'] })
      void messageApi.success('优化任务已提交')
      navigate(`/tasks/${result.taskId}`)
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const rows = taskQuery.data?.records ?? []
  const total = taskQuery.data?.total ?? 0

  const columns: TableColumnsType<OptimizeTaskVO> = [
    {
      title: '任务编号',
      dataIndex: 'taskNo',
      key: 'taskNo',
      render: (taskNo: string, task) => (
        <Button
          type="link"
          style={{ padding: 0 }}
          onClick={(event) => {
            event.stopPropagation()
            navigate(`/tasks/${task.id}`)
          }}
        >
          {taskNo}
        </Button>
      ),
    },
    {
      title: '状态',
      dataIndex: 'taskStatus',
      key: 'taskStatus',
      width: 120,
      render: (status: string) => <TaskStatusTag status={status} />,
    },
    {
      title: '工作空间',
      dataIndex: 'workspaceId',
      key: 'workspaceId',
      width: 110,
    },
    {
      title: '流程图',
      dataIndex: 'graphId',
      key: 'graphId',
      width: 110,
    },
    {
      title: '算法',
      key: 'algorithm',
      width: 140,
      render: (_, task) => `${task.algorithmType} / ${task.algorithmMode}`,
    },
    {
      title: '重试',
      key: 'retry',
      width: 100,
      render: (_, task) => `${task.retryCount}/${task.maxRetryCount}`,
    },
    {
      title: '错误',
      dataIndex: 'errorMessage',
      key: 'errorMessage',
      render: (errorMessage: string | null) => errorMessage || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
    },
    {
      title: '操作',
      key: 'actions',
      width: 220,
      render: (_, task) => (
        <Space>
          <Button
            icon={<EyeOutlined />}
            onClick={(event) => {
              event.stopPropagation()
              navigate(`/tasks/${task.id}`)
            }}
          >
            详情
          </Button>
          {task.taskStatus === 'SUCCESS' ? (
            <Button
              onClick={(event) => {
                event.stopPropagation()
                navigate(`/tasks/${task.id}/result`)
              }}
            >
              结果
            </Button>
          ) : null}
        </Space>
      ),
    },
  ]

  const handleTableChange = (pagination: TablePaginationConfig) => {
    setPageNo(pagination.current ?? 1)
    setPageSize(pagination.pageSize ?? DEFAULT_PAGE_SIZE)
  }

  const handleSubmit = (values: OptimizeTaskFormValues) => {
    createMutation.mutate(values)
  }

  return (
    <>
      {messageContextHolder}
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Space align="start" style={{ justifyContent: 'space-between', width: '100%' }}>
          <div>
            <Typography.Title level={2} style={{ margin: 0 }}>
              任务中心
            </Typography.Title>
            <Typography.Text type="secondary">
              提交优化任务，跟踪运行状态，并进入结果页。
            </Typography.Text>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsCreateOpen(true)}>
            提交优化任务
          </Button>
        </Space>

        <Card>
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <Space wrap>
              <InputNumber
                min={1}
                precision={0}
                prefix={<SearchOutlined />}
                placeholder="workspaceId"
                value={workspaceId}
                onChange={(value) => {
                  setWorkspaceId(value ?? undefined)
                  setPageNo(1)
                }}
                style={{ width: 170 }}
              />
              <InputNumber
                min={1}
                precision={0}
                placeholder="graphId"
                value={graphId}
                onChange={(value) => {
                  setGraphId(value ?? undefined)
                  setPageNo(1)
                }}
                style={{ width: 150 }}
              />
              <Select
                options={TASK_STATUS_OPTIONS}
                value={taskStatus}
                onChange={(value) => {
                  setTaskStatus(value)
                  setPageNo(1)
                }}
                style={{ width: 160 }}
              />
              <Button
                icon={<ReloadOutlined />}
                onClick={() => void taskQuery.refetch()}
                loading={taskQuery.isFetching}
              >
                刷新
              </Button>
            </Space>

            {taskQuery.isError ? (
              <Alert
                type="error"
                showIcon
                message="任务列表加载失败"
                description={getErrorMessage(taskQuery.error)}
                action={<Button onClick={() => void taskQuery.refetch()}>重试</Button>}
              />
            ) : null}

            <Table<OptimizeTaskVO>
              rowKey="id"
              loading={taskQuery.isLoading || taskQuery.isFetching || createMutation.isPending}
              columns={columns}
              dataSource={rows}
              scroll={{ x: 1120 }}
              locale={{
                emptyText: taskQuery.isLoading ? (
                  '加载中...'
                ) : (
                  <Empty description="暂无优化任务" />
                ),
              }}
              pagination={{
                current: pageNo,
                pageSize,
                total,
                showSizeChanger: true,
                showTotal: (count) => `共 ${count} 条`,
              }}
              onChange={handleTableChange}
              onRow={(task) => ({
                onClick: () => navigate(`/tasks/${task.id}`),
                style: { cursor: 'pointer' },
              })}
            />
          </Space>
        </Card>
      </Space>

      <OptimizeTaskFormModal
        open={isCreateOpen}
        initialGraphId={graphId}
        confirmLoading={createMutation.isPending}
        onCancel={() => setIsCreateOpen(false)}
        onSubmit={handleSubmit}
      />
    </>
  )
}

export default TasksPage
