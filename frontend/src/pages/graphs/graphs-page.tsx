import {
  DeleteOutlined,
  EditOutlined,
  FileSearchOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  ThunderboltOutlined,
  UploadOutlined,
} from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Card,
  Col,
  Empty,
  Input,
  Modal,
  Row,
  Space,
  Table,
  Tag,
  Typography,
  message,
} from 'antd'
import type { TableColumnsType, TablePaginationConfig } from 'antd'
import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  createGraph,
  deleteGraph,
  getGraphPage,
  updateGraph,
} from '../../api/graph'
import { getWorkspaceDetail } from '../../api/workspace'
import GraphFormModal from '../../components/graph/graph-form-modal'
import type { GraphFormValues } from '../../components/graph/graph-form-modal'
import GraphYamlImportModal from '../../components/import-export/graph-yaml-import-modal'
import GraphYamlExportButton from '../../components/import-export/graph-yaml-export-button'
import { useDebouncedValue } from '../../hooks/use-debounced-value'
import { useDocumentTitle } from '../../hooks/use-document-title'
import type { GraphVO } from '../../types/graph'

const DEFAULT_PAGE_SIZE = 10

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function parseWorkspaceId(value?: string) {
  const id = Number(value)
  return Number.isFinite(id) && id > 0 ? id : null
}

function GraphsPage() {
  useDocumentTitle('Graphs')

  const { workspaceId: workspaceIdParam } = useParams()
  const workspaceId = parseWorkspaceId(workspaceIdParam)
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [messageApi, messageContextHolder] = message.useMessage()
  const [modalApi, modalContextHolder] = Modal.useModal()
  const [keyword, setKeyword] = useState('')
  const debouncedKeyword = useDebouncedValue(keyword.trim())
  const [pageNo, setPageNo] = useState(1)
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE)
  const [editingGraph, setEditingGraph] = useState<GraphVO | null>(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [isImportOpen, setIsImportOpen] = useState(false)

  const workspaceQuery = useQuery({
    queryKey: ['workspace', workspaceId],
    queryFn: () => getWorkspaceDetail(workspaceId ?? 0),
    enabled: workspaceId !== null,
  })

  const graphQuery = useQuery({
    queryKey: ['graphs', { workspaceId, pageNo, pageSize, keyword: debouncedKeyword }],
    queryFn: () =>
      getGraphPage(workspaceId ?? 0, {
        pageNo,
        pageSize,
        keyword: debouncedKeyword || undefined,
      }),
    enabled: workspaceId !== null,
  })

  const refreshGraphs = () => queryClient.invalidateQueries({ queryKey: ['graphs'] })

  const createMutation = useMutation({
    mutationFn: (values: GraphFormValues) =>
      createGraph(workspaceId ?? 0, {
        name: values.name,
        description: values.description,
        sourceType: values.sourceType,
        graphStatus: values.graphStatus,
      }),
    onSuccess: async () => {
      setIsFormOpen(false)
      await refreshGraphs()
      void messageApi.success('流程图已创建')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ graph, values }: { graph: GraphVO; values: GraphFormValues }) =>
      updateGraph(graph.id, {
        name: values.name,
        description: values.description,
        graphStatus: values.graphStatus,
        totalTime: values.totalTime,
        totalPrecision: values.totalPrecision,
        totalCost: values.totalCost,
      }),
    onSuccess: async () => {
      setEditingGraph(null)
      setIsFormOpen(false)
      await refreshGraphs()
      void messageApi.success('流程图已更新')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteGraph,
    onSuccess: async () => {
      await refreshGraphs()
      void messageApi.success('流程图已删除')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const rows = graphQuery.data?.records ?? []
  const total = graphQuery.data?.total ?? 0
  const isMutating =
    createMutation.isPending || updateMutation.isPending || deleteMutation.isPending

  const handleCreate = () => {
    setEditingGraph(null)
    setIsFormOpen(true)
  }

  const handleEdit = (graph: GraphVO) => {
    setEditingGraph(graph)
    setIsFormOpen(true)
  }

  const handleSubmit = (values: GraphFormValues) => {
    if (editingGraph) {
      updateMutation.mutate({ graph: editingGraph, values })
      return
    }

    createMutation.mutate(values)
  }

  const handleDelete = (graph: GraphVO) => {
    modalApi.confirm({
      title: '删除流程图',
      content: `确认删除「${graph.name}」吗？该操作不可撤销。`,
      okText: '删除',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: () => deleteMutation.mutateAsync(graph.id),
    })
  }

  const handleTableChange = (pagination: TablePaginationConfig) => {
    setPageNo(pagination.current ?? 1)
    setPageSize(pagination.pageSize ?? DEFAULT_PAGE_SIZE)
  }

  const columns: TableColumnsType<GraphVO> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      fixed: 'left',
      render: (name: string, graph) => (
        <Button
          type="link"
          style={{ padding: 0 }}
          onClick={(event) => {
            event.stopPropagation()
            navigate(`/graphs/${graph.id}/detail`)
          }}
        >
          {name}
        </Button>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      render: (description: string | null) => description || '-',
    },
    {
      title: '来源',
      dataIndex: 'sourceType',
      key: 'sourceType',
      width: 120,
      render: (sourceType: string | null) => sourceType || '-',
    },
    {
      title: '状态',
      dataIndex: 'graphStatus',
      key: 'graphStatus',
      width: 120,
      render: (graphStatus: string | null) =>
        graphStatus ? <Tag color="blue">{graphStatus}</Tag> : '-',
    },
    {
      title: '版本',
      dataIndex: 'graphVersion',
      key: 'graphVersion',
      width: 90,
      render: (version: number | null) => version ?? '-',
    },
    {
      title: '总时间',
      dataIndex: 'totalTime',
      key: 'totalTime',
      width: 100,
      render: (value: number | null) => value ?? '-',
    },
    {
      title: '总精度',
      dataIndex: 'totalPrecision',
      key: 'totalPrecision',
      width: 100,
      render: (value: number | null) => value ?? '-',
    },
    {
      title: '总成本',
      dataIndex: 'totalCost',
      key: 'totalCost',
      width: 100,
      render: (value: number | null) => value ?? '-',
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (updatedAt: string) => updatedAt || '-',
    },
    {
      title: '操作',
      key: 'actions',
      width: 440,
      render: (_, graph) => (
        <Space>
          <Button
            icon={<FileSearchOutlined />}
            onClick={(event) => {
              event.stopPropagation()
              navigate(`/graphs/${graph.id}/detail`)
            }}
          >
            详情
          </Button>
          <Button
            icon={<ThunderboltOutlined />}
            onClick={(event) => {
              event.stopPropagation()
              navigate(`/graphs/${graph.id}/editor`)
            }}
          >
            编辑器
          </Button>
          <Button
            icon={<EditOutlined />}
            onClick={(event) => {
              event.stopPropagation()
              handleEdit(graph)
            }}
          >
            编辑
          </Button>
          <GraphYamlExportButton
            graphId={graph.id}
            graphName={graph.name}
            onClick={(event) => event.stopPropagation()}
          />
          <Button
            danger
            icon={<DeleteOutlined />}
            loading={deleteMutation.isPending}
            onClick={(event) => {
              event.stopPropagation()
              handleDelete(graph)
            }}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ]

  if (workspaceId === null) {
    return (
      <Card>
        <Empty
          description="请先从工作空间进入流程图列表"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        >
          <Button type="primary" onClick={() => navigate('/workspaces')}>
            返回工作空间
          </Button>
        </Empty>
      </Card>
    )
  }

  return (
    <>
      {messageContextHolder}
      {modalContextHolder}
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <section className="console-hero">
          <div className="console-hero-copy">
            <span className="console-kicker">Graph Registry</span>
            <Typography.Title level={2} className="console-title">
              流程图阵列
            </Typography.Title>
            <Typography.Paragraph className="console-subtitle">
              {workspaceQuery.data?.name
                ? `当前工作空间：${workspaceQuery.data.name}。从这里进入详情、编辑器、YAML 导入导出和后续优化任务。`
                : '管理当前工作空间下的流程图，作为 YAML、图元 CRUD 和优化任务的主入口。'}
            </Typography.Paragraph>
          </div>
          <div className="console-hero-meta">
            <div className="console-meta-chip">
              <span className="console-meta-label">Workspace</span>
              <strong>{workspaceQuery.data?.name || `#${workspaceId}`}</strong>
            </div>
            <div className="console-meta-chip">
              <span className="console-meta-label">Visible graphs</span>
              <strong>{rows.length}</strong>
            </div>
            <div className="console-meta-chip">
              <span className="console-meta-label">Ready graphs</span>
              <strong>{rows.filter((graph) => graph.graphStatus === 'READY').length}</strong>
            </div>
          </div>
        </section>

        <Row gutter={[16, 16]}>
          <Col xs={24} md={8}>
            <Card className="console-stat-card">
              <div className="console-stat-label">Total graphs</div>
              <div className="console-stat-value">{total}</div>
              <div className="console-stat-footnote">当前工作空间内累计流程图</div>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card className="console-stat-card">
              <div className="console-stat-label">Draft or ready</div>
              <div className="console-stat-value">
                {rows.filter((graph) => graph.graphStatus === 'DRAFT').length}/
                {rows.filter((graph) => graph.graphStatus === 'READY').length}
              </div>
              <div className="console-stat-footnote">草稿与可用图数量对照</div>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card className="console-stat-card">
              <div className="console-stat-label">Source modes</div>
              <div className="console-stat-value">{debouncedKeyword ? 'Filtered' : 'Mixed'}</div>
              <div className="console-stat-footnote">支持手建图与 YAML 导入图</div>
            </Card>
          </Col>
        </Row>

        {workspaceQuery.isError ? (
          <Alert
            type="warning"
            showIcon
            message="工作空间信息加载失败"
            description={getErrorMessage(workspaceQuery.error)}
          />
        ) : null}

        <Card
          className="console-panel console-table"
          title={
            <div className="console-panel-title">
              <span className="console-panel-kicker">Graph Operations</span>
              <span>Search, edit, export, and enter the graph editor</span>
            </div>
          }
          extra={
            <Space>
              <Button onClick={() => navigate('/workspaces')}>返回工作空间</Button>
              <Button icon={<UploadOutlined />} onClick={() => setIsImportOpen(true)}>
                导入 YAML
              </Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
                新建流程图
              </Button>
            </Space>
          }
        >
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <div className="console-toolbar">
              <div className="console-toolbar-group">
              <Input
                allowClear
                prefix={<SearchOutlined />}
                placeholder="搜索流程图名称"
                value={keyword}
                onChange={(event) => {
                  setKeyword(event.target.value)
                  setPageNo(1)
                }}
                style={{ width: 280 }}
              />
              <Button
                icon={<ReloadOutlined />}
                onClick={() => void graphQuery.refetch()}
                loading={graphQuery.isFetching}
              >
                刷新
              </Button>
              </div>
            </div>

            {graphQuery.isError ? (
              <Alert
                type="error"
                showIcon
                message="流程图加载失败"
                description={getErrorMessage(graphQuery.error)}
                action={
                  <Button size="small" onClick={() => void graphQuery.refetch()}>
                    重试
                  </Button>
                }
              />
            ) : null}

            <Table<GraphVO>
              rowKey="id"
              loading={graphQuery.isLoading || graphQuery.isFetching || isMutating}
              columns={columns}
              dataSource={rows}
              scroll={{ x: 1320 }}
              locale={{
                emptyText: graphQuery.isLoading ? (
                  '加载中...'
                ) : (
                  <Empty className="console-empty" description="暂无流程图" />
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
              onRow={(graph) => ({
                onClick: () => navigate(`/graphs/${graph.id}/detail`),
                style: { cursor: 'pointer' },
              })}
            />
          </Space>
        </Card>
      </Space>

      <GraphFormModal
        open={isFormOpen}
        mode={editingGraph ? 'edit' : 'create'}
        initialValue={editingGraph}
        confirmLoading={createMutation.isPending || updateMutation.isPending}
        onCancel={() => {
          setIsFormOpen(false)
          setEditingGraph(null)
        }}
        onSubmit={handleSubmit}
      />
      <GraphYamlImportModal
        open={isImportOpen}
        workspaceId={workspaceId}
        onCancel={() => setIsImportOpen(false)}
        onSuccess={async () => {
          await refreshGraphs()
        }}
      />
    </>
  )
}

export default GraphsPage
