import { ArrowLeftOutlined, ReloadOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Card,
  Col,
  Empty,
  Modal,
  Row,
  Space,
  Spin,
  Typography,
  message,
} from 'antd'
import { useEffect, useState } from 'react'
import ReactFlow, { Background, Controls, MiniMap } from 'reactflow'
import 'reactflow/dist/style.css'
import { useNavigate, useParams } from 'react-router-dom'
import {
  createConstraint,
  deleteConstraint,
  updateConstraint,
} from '../../api/constraint'
import {
  createEquipment,
  deleteEquipment,
  updateEquipment,
} from '../../api/equipment'
import { getGraphDetail } from '../../api/graph'
import { createNode, deleteNode, updateNode } from '../../api/node'
import { createPath, deletePath, updatePath } from '../../api/path'
import GraphElementFormModal from '../../components/graph-editor/graph-element-form-modal'
import GraphElementManager from '../../components/graph-editor/graph-element-manager'
import type {
  EditingGraphElement,
  GraphElementFormValues,
  GraphElementKind,
  GraphElementValue,
} from '../../components/graph-editor/graph-element-types'
import GraphPropertyPanel from '../../components/graph-editor/graph-property-panel'
import type { SelectedGraphElement } from '../../components/graph-editor/graph-property-panel'
import GraphResourcePanel from '../../components/graph-editor/graph-resource-panel'
import { useGraphFlowElements } from '../../hooks/graph-editor/use-graph-flow-elements'
import { useDocumentTitle } from '../../hooks/use-document-title'
import type { ID } from '../../types/common'

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function parseGraphId(value?: string) {
  const graphId = Number(value)
  return Number.isFinite(graphId) && graphId > 0 ? graphId : null
}

function getElementId(value: GraphElementValue) {
  return value.id
}

function getDeleteTitle(kind: GraphElementKind) {
  const labels: Record<GraphElementKind, string> = {
    constraint: '约束',
    equipment: '装备',
    node: '节点',
    path: '路径',
  }

  return `删除${labels[kind]}`
}

function buildNodePayload(values: GraphElementFormValues) {
  return {
    costValue: values.costValue,
    equipmentId: values.equipmentId,
    nodeCode: values.nodeCode ?? '',
    nodeDescription: values.nodeDescription,
    nodeName: values.nodeName,
    precisionValue: values.precisionValue,
    sortNo: values.sortNo,
    timeCost: values.timeCost,
  }
}

function buildPathPayload(values: GraphElementFormValues) {
  return {
    endNodeId: values.endNodeId ?? 0,
    relationType: values.relationType,
    remark: values.remark,
    startNodeId: values.startNodeId ?? 0,
  }
}

function buildEquipmentPayload(values: GraphElementFormValues) {
  return {
    color: values.color,
    description: values.description,
    imagePath: values.imagePath,
    name: values.name ?? '',
  }
}

function buildConstraintPayload(values: GraphElementFormValues) {
  return {
    conditionCode: values.conditionCode ?? '',
    conditionDescription: values.conditionDescription,
    conditionType: values.conditionType ?? '',
    enabled: values.enabled,
    nodeId1: values.nodeId1 ?? 0,
    nodeId2: values.nodeId2 ?? 0,
  }
}

async function saveGraphElement(
  graphId: ID,
  editingElement: EditingGraphElement,
  values: GraphElementFormValues,
) {
  const elementId = editingElement.value ? getElementId(editingElement.value) : null

  if (editingElement.kind === 'node') {
    const payload = buildNodePayload(values)
    return elementId ? updateNode(graphId, elementId, payload) : createNode(graphId, payload)
  }

  if (editingElement.kind === 'path') {
    const payload = buildPathPayload(values)
    return elementId ? updatePath(graphId, elementId, payload) : createPath(graphId, payload)
  }

  if (editingElement.kind === 'equipment') {
    const payload = buildEquipmentPayload(values)
    return elementId
      ? updateEquipment(graphId, elementId, payload)
      : createEquipment(graphId, payload)
  }

  const payload = buildConstraintPayload(values)
  return elementId
    ? updateConstraint(graphId, elementId, payload)
    : createConstraint(graphId, payload)
}

async function deleteGraphElement(
  graphId: ID,
  kind: GraphElementKind,
  value: GraphElementValue,
) {
  const elementId = getElementId(value)

  if (kind === 'node') {
    return deleteNode(graphId, elementId)
  }

  if (kind === 'path') {
    return deletePath(graphId, elementId)
  }

  if (kind === 'equipment') {
    return deleteEquipment(graphId, elementId)
  }

  return deleteConstraint(graphId, elementId)
}

function GraphEditorPage() {
  useDocumentTitle('Graph Editor')

  const { graphId: graphIdParam } = useParams()
  const graphId = parseGraphId(graphIdParam)
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [messageApi, messageContextHolder] = message.useMessage()
  const [modalApi, modalContextHolder] = Modal.useModal()
  const [selectedElement, setSelectedElement] = useState<SelectedGraphElement | null>(null)
  const [editingElement, setEditingElement] = useState<EditingGraphElement | null>(null)
  const [isElementFormOpen, setIsElementFormOpen] = useState(false)
  const [isElementFormDirty, setIsElementFormDirty] = useState(false)

  const graphQuery = useQuery({
    queryKey: ['graph-detail', graphId],
    queryFn: () => getGraphDetail(graphId ?? 0),
    enabled: graphId !== null,
  })

  const { edges, nodes } = useGraphFlowElements(graphQuery.data)

  const refreshGraphDetail = () =>
    queryClient.invalidateQueries({ queryKey: ['graph-detail', graphId] })

  const saveMutation = useMutation({
    mutationFn: (values: GraphElementFormValues) => {
      if (!editingElement || graphId === null) {
        throw new Error('缺少当前图元上下文')
      }

      return saveGraphElement(graphId, editingElement, values)
    },
    onSuccess: async () => {
      await refreshGraphDetail()
      setIsElementFormOpen(false)
      setEditingElement(null)
      setIsElementFormDirty(false)
      void messageApi.success('图元已保存')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const deleteMutation = useMutation({
    mutationFn: ({ kind, value }: { kind: GraphElementKind; value: GraphElementValue }) => {
      if (graphId === null) {
        throw new Error('缺少流程图 ID')
      }

      return deleteGraphElement(graphId, kind, value)
    },
    onSuccess: async () => {
      setSelectedElement(null)
      await refreshGraphDetail()
      void messageApi.success('图元已删除')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  useEffect(() => {
    if (!isElementFormDirty) {
      return undefined
    }

    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault()
      event.returnValue = ''
    }

    window.addEventListener('beforeunload', handleBeforeUnload)

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload)
    }
  }, [isElementFormDirty])

  const openCreateForm = (kind: GraphElementKind) => {
    setEditingElement({ kind })
    setIsElementFormDirty(false)
    setIsElementFormOpen(true)
  }

  const openEditForm = (kind: GraphElementKind, value: GraphElementValue) => {
    setEditingElement({ kind, value })
    setIsElementFormDirty(false)
    setIsElementFormOpen(true)
  }

  const closeElementForm = () => {
    if (!isElementFormDirty) {
      setIsElementFormOpen(false)
      setEditingElement(null)
      return
    }

    modalApi.confirm({
      title: '放弃未保存修改？',
      content: '当前表单存在未保存修改，关闭后这些修改会丢失。',
      okText: '放弃修改',
      cancelText: '继续编辑',
      onOk: () => {
        setIsElementFormOpen(false)
        setEditingElement(null)
        setIsElementFormDirty(false)
      },
    })
  }

  const confirmDeleteElement = (kind: GraphElementKind, value: GraphElementValue) => {
    modalApi.confirm({
      title: getDeleteTitle(kind),
      content: `确认删除 ID=${getElementId(value)} 的图元吗？该操作不可撤销。`,
      okText: '删除',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: () => deleteMutation.mutateAsync({ kind, value }),
    })
  }

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
        <Space direction="vertical" align="center" size="middle" style={{ width: '100%' }}>
          <Spin />
          <Typography.Text type="secondary">正在加载流程图详情...</Typography.Text>
        </Space>
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
    <>
      {messageContextHolder}
      {modalContextHolder}
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <section className="console-hero">
          <div className="console-hero-copy">
            <span className="console-kicker">Graph Editor</span>
            <Typography.Title level={2} className="console-title">
              {graph.name}
            </Typography.Title>
            <Typography.Paragraph className="console-subtitle">
              当前版本 {graph.graphVersion ?? '-'}。左侧维护图元资源，中间查看流程结构，右侧读取属性详情。
            </Typography.Paragraph>
          </div>
          <div className="console-hero-meta">
            <div className="console-meta-chip">
              <span className="console-meta-label">Status</span>
              <strong>{graph.graphStatus || '-'}</strong>
            </div>
            <div className="console-meta-chip">
              <span className="console-meta-label">Graph ID</span>
              <strong>{graph.id}</strong>
            </div>
          </div>
        </section>

        <div className="console-toolbar">
          <div className="console-toolbar-group">
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
          </div>
        </div>

        <Row gutter={[16, 16]}>
          <Col xs={24} md={8}>
            <Card className="console-stat-card">
              <div className="console-stat-label">Total time</div>
              <div className="console-stat-value">{graph.totalTime ?? '-'}</div>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card className="console-stat-card">
              <div className="console-stat-label">Total precision</div>
              <div className="console-stat-value">{graph.totalPrecision ?? '-'}</div>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card className="console-stat-card">
              <div className="console-stat-label">Total cost</div>
              <div className="console-stat-value">{graph.totalCost ?? '-'}</div>
            </Card>
          </Col>
        </Row>

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
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <GraphResourcePanel detail={detail} />
            <GraphElementManager
              detail={detail}
              deleting={deleteMutation.isPending}
              onCreate={openCreateForm}
              onEdit={openEditForm}
              onDelete={confirmDeleteElement}
            />
          </Space>
        </Col>
        <Col xs={24} lg={14}>
          <Card
            className="console-panel console-canvas-card"
            title="画布"
            styles={{ body: { height: 620, padding: 0 } }}
          >
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

      <GraphElementFormModal
        open={isElementFormOpen}
        editingElement={editingElement}
        detail={detail}
        dirty={isElementFormDirty}
        confirmLoading={saveMutation.isPending}
        onCancel={closeElementForm}
        onDirtyChange={setIsElementFormDirty}
        onSubmit={(values) => saveMutation.mutate(values)}
      />
    </>
  )
}

export default GraphEditorPage
