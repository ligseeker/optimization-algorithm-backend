import { Alert, Button, Card, Descriptions, Empty, Space, Spin, Typography, message } from 'antd'
import { ReloadOutlined, RollbackOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import {
  getOptimizeTaskDetail,
  retryOptimizeTask,
} from '../../api/optimize-task'
import TaskStatusTag from '../../components/tasks/task-status-tag'
import { useDocumentTitle } from '../../hooks/use-document-title'

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function TaskDetailPage() {
  useDocumentTitle('Task Detail')

  const { taskId } = useParams()
  const taskIdValue = Number(taskId)
  const canLoad = Number.isFinite(taskIdValue) && taskIdValue > 0
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [messageApi, messageContextHolder] = message.useMessage()

  const taskQuery = useQuery({
    queryKey: ['optimize-task', taskIdValue],
    queryFn: () => getOptimizeTaskDetail(taskIdValue),
    enabled: canLoad,
    refetchInterval: (query) =>
      query.state.data && ['PENDING', 'RUNNING'].includes(query.state.data.taskStatus)
        ? 3000
        : false,
  })

  const retryMutation = useMutation({
    mutationFn: retryOptimizeTask,
    onSuccess: async (result) => {
      await queryClient.invalidateQueries({ queryKey: ['optimize-task'] })
      await queryClient.invalidateQueries({ queryKey: ['optimize-tasks'] })
      void messageApi.success('重试任务已提交')
      navigate(`/tasks/${result.taskId}`)
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  if (!canLoad) {
    return (
      <Card>
        <Empty description="任务 ID 无效">
          <Button type="primary" onClick={() => navigate('/tasks')}>
            返回任务中心
          </Button>
        </Empty>
      </Card>
    )
  }

  if (taskQuery.isLoading) {
    return (
      <Card>
        <Spin tip="正在加载任务详情..." />
      </Card>
    )
  }

  if (taskQuery.isError) {
    return (
      <Alert
        type="error"
        showIcon
        message="任务详情加载失败"
        description={getErrorMessage(taskQuery.error)}
        action={<Button onClick={() => void taskQuery.refetch()}>重试</Button>}
      />
    )
  }

  if (!taskQuery.data) {
    return (
      <Card>
        <Empty description="未找到任务" />
      </Card>
    )
  }

  const task = taskQuery.data
  const isRunning = ['PENDING', 'RUNNING'].includes(task.taskStatus)

  return (
    <>
      {messageContextHolder}
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Space align="start" style={{ justifyContent: 'space-between', width: '100%' }}>
          <div>
            <Typography.Title level={2} style={{ margin: 0 }}>
              {task.taskNo}
            </Typography.Title>
            <Space>
              <TaskStatusTag status={task.taskStatus} />
              {isRunning ? (
                <Typography.Text type="secondary">运行中任务会自动刷新</Typography.Text>
              ) : null}
            </Space>
          </div>
          <Space>
            <Button icon={<RollbackOutlined />} onClick={() => navigate('/tasks')}>
              返回任务中心
            </Button>
            <Button
              icon={<ReloadOutlined />}
              onClick={() => void taskQuery.refetch()}
              loading={taskQuery.isFetching}
            >
              刷新
            </Button>
            {task.taskStatus === 'FAILED' ? (
              <Button
                type="primary"
                loading={retryMutation.isPending}
                onClick={() => retryMutation.mutate(task.id)}
              >
                重试
              </Button>
            ) : null}
            {task.taskStatus === 'SUCCESS' ? (
              <Button type="primary" onClick={() => navigate(`/tasks/${task.id}/result`)}>
                查看结果
              </Button>
            ) : null}
          </Space>
        </Space>

        {task.taskStatus === 'FAILED' ? (
          <Alert
            type="error"
            showIcon
            message={task.errorCode || '任务执行失败'}
            description={task.errorMessage || '后端未返回详细错误信息'}
          />
        ) : null}

        <Card>
          <Descriptions bordered column={2}>
            <Descriptions.Item label="任务 ID">{task.id}</Descriptions.Item>
            <Descriptions.Item label="任务编号">{task.taskNo}</Descriptions.Item>
            <Descriptions.Item label="工作空间">{task.workspaceId}</Descriptions.Item>
            <Descriptions.Item label="流程图">{task.graphId}</Descriptions.Item>
            <Descriptions.Item label="用户">{task.userId}</Descriptions.Item>
            <Descriptions.Item label="状态">
              <TaskStatusTag status={task.taskStatus} />
            </Descriptions.Item>
            <Descriptions.Item label="算法类型">{task.algorithmType}</Descriptions.Item>
            <Descriptions.Item label="算法模式">{task.algorithmMode}</Descriptions.Item>
            <Descriptions.Item label="时间权重">{task.timeWeight}</Descriptions.Item>
            <Descriptions.Item label="精度权重">{task.precisionWeight}</Descriptions.Item>
            <Descriptions.Item label="成本权重">{task.costWeight}</Descriptions.Item>
            <Descriptions.Item label="重试次数">
              {task.retryCount}/{task.maxRetryCount}
            </Descriptions.Item>
            <Descriptions.Item label="排队时间">{task.queueTime || '-'}</Descriptions.Item>
            <Descriptions.Item label="开始时间">{task.startedAt || '-'}</Descriptions.Item>
            <Descriptions.Item label="结束时间">{task.finishedAt || '-'}</Descriptions.Item>
            <Descriptions.Item label="结果 ID">{task.resultId ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{task.createdAt}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{task.updatedAt}</Descriptions.Item>
          </Descriptions>
        </Card>
      </Space>
    </>
  )
}

export default TaskDetailPage
