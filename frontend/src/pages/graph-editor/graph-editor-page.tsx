import { ArrowLeftOutlined, ReloadOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import { Alert, Button, Card, Col, Descriptions, Empty, Row, Space, Spin, Typography } from 'antd'
import { useState } from 'react'
import ReactFlow, { Background, Controls, MiniMap } from 'reactflow'
import 'reactflow/dist/style.css'
import { useNavigate, useParams } from 'react-router-dom'
import { getGraphDetail } from '../../api/graph'
import GraphPropertyPanel from '../../components/graph-editor/graph-property-panel'
import type { SelectedGraphElement } from '../../components/graph-editor/graph-property-panel'
import GraphResourcePanel from '../../components/graph-editor/graph-resource-panel'
import { useGraphFlowElements } from '../../hooks/graph-editor/use-graph-flow-elements'
import { useDocumentTitle } from '../../hooks/use-document-title'

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function parseGraphId(value?: string) {
  const graphId = Number(value)
  return Number.isFinite(graphId) && graphId > 0 ? graphId : null
}

function GraphEditorPage() {
  useDocumentTitle('Graph Editor')

  const { graphId: graphIdParam } = useParams()
  const graphId = parseGraphId(graphIdParam)
  const navigate = useNavigate()
  const [selectedElement, setSelectedElement] = useState<SelectedGraphElement | null>(null)

  const graphQuery = useQuery({
    queryKey: ['graph-detail', graphId],
    queryFn: () => getGraphDetail(graphId ?? 0),
    enabled: graphId !== null,
  })

  const { edges, nodes } = useGraphFlowElements(graphQuery.data)

  if (graphId === null) {
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
        <Empty description="未找到流程图详情" />
      </Card>
    )
  }

  const detail = graphQuery.data
  const graph = detail.graph

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Space align="start" style={{ justifyContent: 'space-between', width: '100%' }}>
        <div>
          <Typography.Title level={2} style={{ margin: 0 }}>
            {graph.name}
          </Typography.Title>
          <Typography.Text type="secondary">
            图编辑器基础框架，当前版本：{graph.graphVersion ?? '-'}
          </Typography.Text>
        </div>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(`/graphs/${graph.id}/detail`)}>
            返回详情
          </Button>
          <Button
            icon={<ReloadOutlined />}
            loading={graphQuery.isFetching}
            onClick={() => void graphQuery.refetch()}
          >
            刷新
          </Button>
        </Space>
      </Space>

      <Card>
        <Descriptions column={3} size="small">
          <Descriptions.Item label="graphId">{graph.id}</Descriptions.Item>
          <Descriptions.Item label="workspaceId">{graph.workspaceId}</Descriptions.Item>
          <Descriptions.Item label="状态">{graph.graphStatus || '-'}</Descriptions.Item>
          <Descriptions.Item label="总时间">{graph.totalTime ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="总精度">{graph.totalPrecision ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="总成本">{graph.totalCost ?? '-'}</Descriptions.Item>
        </Descriptions>
      </Card>

      {nodes.length === 0 ? (
        <Alert
          type="info"
          showIcon
          message="当前流程图暂无节点"
          description="可以先通过 YAML 导入或后续图元 CRUD 功能补充节点。"
        />
      ) : null}

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={5}>
          <GraphResourcePanel detail={detail} />
        </Col>
        <Col xs={24} lg={14}>
          <Card title="画布" bodyStyle={{ height: 620, padding: 0 }}>
            {nodes.length > 0 ? (
              <ReactFlow
                nodes={nodes}
                edges={edges}
                fitView
                onNodeClick={(_, node) => {
                  setSelectedElement({ type: 'node', value: node.data })
                }}
                onEdgeClick={(_, edge) => {
                  if (edge.data) {
                    setSelectedElement({ type: 'path', value: edge.data })
                  }
                }}
              >
                <MiniMap />
                <Controls />
                <Background />
              </ReactFlow>
            ) : (
              <Empty description="暂无可展示节点" style={{ paddingTop: 220 }} />
            )}
          </Card>
        </Col>
        <Col xs={24} lg={5}>
          <GraphPropertyPanel selectedElement={selectedElement} />
        </Col>
      </Row>
    </Space>
  )
}

export default GraphEditorPage
