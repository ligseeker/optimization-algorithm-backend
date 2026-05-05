import { Alert, Button, Card, Descriptions, Empty, Space, Spin, Typography } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import { getGraphBase } from '../../api/graph'
import { useDocumentTitle } from '../../hooks/use-document-title'

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function GraphDetailPage() {
  useDocumentTitle('Graph Detail')

  const navigate = useNavigate()
  const { graphId } = useParams()
  const graphIdValue = Number(graphId)
  const canLoad = Number.isFinite(graphIdValue) && graphIdValue > 0

  const graphQuery = useQuery({
    queryKey: ['graph-base', graphIdValue],
    queryFn: () => getGraphBase(graphIdValue),
    enabled: canLoad,
  })

  if (!canLoad) {
    return (
      <Card>
        <Empty description="流程图 ID 无效">
          <Button type="primary" onClick={() => navigate('/workspaces')}>
            返回工作空间
          </Button>
        </Empty>
      </Card>
    )
  }

  if (graphQuery.isLoading) {
    return (
      <Card>
        <Spin tip="正在加载流程图详情..." />
      </Card>
    )
  }

  if (graphQuery.isError) {
    return (
      <Alert
        type="error"
        showIcon
        message="流程图详情加载失败"
        description={getErrorMessage(graphQuery.error)}
        action={<Button onClick={() => void graphQuery.refetch()}>重试</Button>}
      />
    )
  }

  if (!graphQuery.data) {
    return (
      <Card>
        <Empty description="未找到流程图" />
      </Card>
    )
  }

  const graph = graphQuery.data

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Space align="start" style={{ justifyContent: 'space-between', width: '100%' }}>
        <div>
          <Typography.Title level={2} style={{ margin: 0 }}>
            {graph.name}
          </Typography.Title>
          <Typography.Text type="secondary">
            流程图基础信息，编辑器能力将在后续任务中完善。
          </Typography.Text>
        </div>
        <Button type="primary" onClick={() => navigate(`/graphs/${graph.id}/editor`)}>
          进入编辑器
        </Button>
      </Space>

      <Card>
        <Descriptions bordered column={2}>
          <Descriptions.Item label="ID">{graph.id}</Descriptions.Item>
          <Descriptions.Item label="工作空间">{graph.workspaceId}</Descriptions.Item>
          <Descriptions.Item label="来源">{graph.sourceType || '-'}</Descriptions.Item>
          <Descriptions.Item label="状态">{graph.graphStatus || '-'}</Descriptions.Item>
          <Descriptions.Item label="版本">{graph.graphVersion ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{graph.updatedAt || '-'}</Descriptions.Item>
          <Descriptions.Item label="总时间">{graph.totalTime ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="总精度">{graph.totalPrecision ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="总成本">{graph.totalCost ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>
            {graph.description || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>
    </Space>
  )
}

export default GraphDetailPage
