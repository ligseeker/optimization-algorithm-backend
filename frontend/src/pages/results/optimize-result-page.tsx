import { ArrowLeftOutlined, ReloadOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Card,
  Col,
  Descriptions,
  Empty,
  Row,
  Space,
  Spin,
  Statistic,
  Typography,
} from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import {
  getOptimizeResult,
  getOptimizeTaskDetail,
} from '../../api/optimize-task'
import JsonCodeBlock from '../../components/results/json-code-block'
import MetricComparisonChart from '../../components/results/metric-comparison-chart'
import TaskStatusTag from '../../components/tasks/task-status-tag'
import { useDocumentTitle } from '../../hooks/use-document-title'

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function getValidTaskId(taskId: string | undefined) {
  const value = Number(taskId)
  return Number.isFinite(value) && value > 0 ? value : undefined
}

function OptimizeResultPage() {
  useDocumentTitle('Optimize Result')

  const { taskId } = useParams()
  const taskIdValue = getValidTaskId(taskId)
  const navigate = useNavigate()

  const taskQuery = useQuery({
    queryKey: ['optimize-task', taskIdValue],
    queryFn: () => getOptimizeTaskDetail(taskIdValue ?? 0),
    enabled: Boolean(taskIdValue),
  })

  const resultQuery = useQuery({
    queryKey: ['optimize-result', taskIdValue],
    queryFn: () => getOptimizeResult(taskIdValue ?? 0),
    enabled: Boolean(taskIdValue && taskQuery.data?.taskStatus === 'SUCCESS'),
  })

  if (!taskIdValue) {
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
        <Spin tip="正在确认任务状态..." />
      </Card>
    )
  }

  if (taskQuery.isError) {
    return (
      <Alert
        type="error"
        showIcon
        message="任务状态加载失败"
        description={getErrorMessage(taskQuery.error)}
        action={<Button onClick={() => void taskQuery.refetch()}>重试</Button>}
      />
    )
  }

  const task = taskQuery.data

  if (!task) {
    return (
      <Card>
        <Empty description="未找到任务">
          <Button type="primary" onClick={() => navigate('/tasks')}>
            返回任务中心
          </Button>
        </Empty>
      </Card>
    )
  }

  const isTaskSuccess = task.taskStatus === 'SUCCESS'

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Space align="start" style={{ justifyContent: 'space-between', width: '100%' }}>
        <div>
          <Typography.Title level={2} style={{ margin: 0 }}>
            优化结果
          </Typography.Title>
          <Space>
            <Typography.Text type="secondary">任务：{task.taskNo}</Typography.Text>
            <TaskStatusTag status={task.taskStatus} />
          </Space>
        </div>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(`/tasks/${task.id}`)}>
            返回任务详情
          </Button>
          <Button
            icon={<ReloadOutlined />}
            onClick={() => {
              void taskQuery.refetch()
              void resultQuery.refetch()
            }}
            loading={taskQuery.isFetching || resultQuery.isFetching}
          >
            刷新
          </Button>
        </Space>
      </Space>

      {!isTaskSuccess ? (
        <Alert
          type={task.taskStatus === 'FAILED' ? 'error' : 'info'}
          showIcon
          message="当前任务还没有可展示的成功结果"
          description={
            task.taskStatus === 'FAILED'
              ? task.errorMessage || '任务执行失败，暂无优化结果。'
              : '任务仍在排队或运行中，请等待任务成功后再查看结果。'
          }
          action={<Button onClick={() => navigate(`/tasks/${task.id}`)}>查看任务详情</Button>}
        />
      ) : null}

      {isTaskSuccess && resultQuery.isLoading ? (
        <Card>
          <Spin tip="正在加载优化结果..." />
        </Card>
      ) : null}

      {isTaskSuccess && resultQuery.isError ? (
        <Alert
          type="error"
          showIcon
          message="优化结果加载失败"
          description={getErrorMessage(resultQuery.error)}
          action={<Button onClick={() => void resultQuery.refetch()}>重试</Button>}
        />
      ) : null}

      {isTaskSuccess && !resultQuery.isLoading && !resultQuery.isError && !resultQuery.data ? (
        <Card>
          <Empty description="暂无优化结果" />
        </Card>
      ) : null}

      {resultQuery.data ? (
        <>
          <Card>
            <Descriptions bordered column={2}>
              <Descriptions.Item label="结果 ID">{resultQuery.data.id}</Descriptions.Item>
              <Descriptions.Item label="结果名称">{resultQuery.data.resultName}</Descriptions.Item>
              <Descriptions.Item label="任务 ID">{resultQuery.data.taskId}</Descriptions.Item>
              <Descriptions.Item label="工作空间">{resultQuery.data.workspaceId}</Descriptions.Item>
              <Descriptions.Item label="源流程图">{resultQuery.data.sourceGraphId}</Descriptions.Item>
              <Descriptions.Item label="生成时间">{resultQuery.data.createdAt}</Descriptions.Item>
            </Descriptions>
          </Card>

          <Row gutter={[16, 16]}>
            <Col xs={24} md={8}>
              <Card>
                <Statistic
                  title="总耗时"
                  value={resultQuery.data.totalTimeAfter}
                  suffix={`/ ${resultQuery.data.totalTimeBefore}`}
                />
              </Card>
            </Col>
            <Col xs={24} md={8}>
              <Card>
                <Statistic
                  title="总精度"
                  value={resultQuery.data.totalPrecisionAfter}
                  suffix={`/ ${resultQuery.data.totalPrecisionBefore}`}
                />
              </Card>
            </Col>
            <Col xs={24} md={8}>
              <Card>
                <Statistic
                  title="总成本"
                  value={resultQuery.data.totalCostAfter}
                  suffix={`/ ${resultQuery.data.totalCostBefore}`}
                />
              </Card>
            </Col>
            <Col xs={24} md={8}>
              <Card>
                <Statistic title="综合评分比例" value={resultQuery.data.scoreRatio} />
              </Card>
            </Col>
          </Row>

          <Card title="指标对比">
            <MetricComparisonChart result={resultQuery.data} />
          </Card>

          <Card title="Result Graph">
            <JsonCodeBlock value={resultQuery.data.resultGraph} />
          </Card>

          <Card title="Diff">
            <JsonCodeBlock value={resultQuery.data.diff} />
          </Card>

          <Card title="Map Code">
            <JsonCodeBlock value={resultQuery.data.mapCode} emptyText="暂无 mapCode" />
          </Card>
        </>
      ) : null}
    </Space>
  )
}

export default OptimizeResultPage
