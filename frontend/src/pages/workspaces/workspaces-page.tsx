import {
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  ShareAltOutlined,
  DeleteOutlined,
} from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Card,
  Empty,
  Input,
  Modal,
  Space,
  Table,
  Tag,
  Typography,
  message,
} from 'antd'
import type { TableColumnsType, TablePaginationConfig } from 'antd'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  createWorkspace,
  deleteWorkspace,
  getWorkspacePage,
  updateWorkspace,
} from '../../api/workspace'
import WorkspaceFormModal from '../../components/workspace/workspace-form-modal'
import type { WorkspaceFormValues } from '../../components/workspace/workspace-form-modal'
import { useDebouncedValue } from '../../hooks/use-debounced-value'
import { useDocumentTitle } from '../../hooks/use-document-title'
import type { WorkspaceVO } from '../../types/workspace'

const DEFAULT_PAGE_SIZE = 10

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}

function WorkspacesPage() {
  useDocumentTitle('Workspaces')

  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [messageApi, messageContextHolder] = message.useMessage()
  const [modalApi, modalContextHolder] = Modal.useModal()
  const [keyword, setKeyword] = useState('')
  const debouncedKeyword = useDebouncedValue(keyword.trim())
  const [pageNo, setPageNo] = useState(1)
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE)
  const [editingWorkspace, setEditingWorkspace] = useState<WorkspaceVO | null>(null)
  const [isFormOpen, setIsFormOpen] = useState(false)

  const workspaceQuery = useQuery({
    queryKey: ['workspaces', { pageNo, pageSize, keyword: debouncedKeyword }],
    queryFn: () =>
      getWorkspacePage({
        pageNo,
        pageSize,
        keyword: debouncedKeyword || undefined,
      }),
  })

  const refreshWorkspaces = () =>
    queryClient.invalidateQueries({ queryKey: ['workspaces'] })

  const createMutation = useMutation({
    mutationFn: createWorkspace,
    onSuccess: async () => {
      setIsFormOpen(false)
      await refreshWorkspaces()
      void messageApi.success('工作空间已创建')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ workspace, values }: { workspace: WorkspaceVO; values: WorkspaceFormValues }) =>
      updateWorkspace(workspace.id, {
        name: values.name,
        description: values.description,
        status: values.status,
      }),
    onSuccess: async () => {
      setEditingWorkspace(null)
      setIsFormOpen(false)
      await refreshWorkspaces()
      void messageApi.success('工作空间已更新')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteWorkspace,
    onSuccess: async () => {
      await refreshWorkspaces()
      void messageApi.success('工作空间已删除')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const rows = workspaceQuery.data?.records ?? []
  const total = workspaceQuery.data?.total ?? 0
  const isMutating =
    createMutation.isPending || updateMutation.isPending || deleteMutation.isPending

  const handleCreate = () => {
    setEditingWorkspace(null)
    setIsFormOpen(true)
  }

  const handleEdit = (workspace: WorkspaceVO) => {
    setEditingWorkspace(workspace)
    setIsFormOpen(true)
  }

  const handleSubmit = (values: WorkspaceFormValues) => {
    if (editingWorkspace) {
      updateMutation.mutate({ workspace: editingWorkspace, values })
      return
    }

    createMutation.mutate({
      name: values.name,
      description: values.description,
    })
  }

  const handleDelete = (workspace: WorkspaceVO) => {
    modalApi.confirm({
      title: '删除工作空间',
      content: `确认删除「${workspace.name}」吗？该操作不可撤销。`,
      okText: '删除',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: () => deleteMutation.mutateAsync(workspace.id),
    })
  }

  const handleTableChange = (pagination: TablePaginationConfig) => {
    setPageNo(pagination.current ?? 1)
    setPageSize(pagination.pageSize ?? DEFAULT_PAGE_SIZE)
  }

  const columns: TableColumnsType<WorkspaceVO> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      render: (name: string, workspace) => (
        <Button
          type="link"
          style={{ padding: 0 }}
          onClick={(event) => {
            event.stopPropagation()
            navigate(`/workspaces/${workspace.id}/graphs`)
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
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: WorkspaceVO['status']) =>
        status === 1 ? <Tag color="green">启用</Tag> : <Tag>停用</Tag>,
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
      width: 260,
      render: (_, workspace) => (
        <Space>
          <Button
            icon={<ShareAltOutlined />}
            onClick={(event) => {
              event.stopPropagation()
              navigate(`/workspaces/${workspace.id}/graphs`)
            }}
          >
            流程图
          </Button>
          <Button
            icon={<EditOutlined />}
            onClick={(event) => {
              event.stopPropagation()
              handleEdit(workspace)
            }}
          >
            编辑
          </Button>
          <Button
            danger
            icon={<DeleteOutlined />}
            loading={deleteMutation.isPending}
            onClick={(event) => {
              event.stopPropagation()
              handleDelete(workspace)
            }}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <>
      {messageContextHolder}
      {modalContextHolder}
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Space align="start" style={{ justifyContent: 'space-between', width: '100%' }}>
          <div>
            <Typography.Title level={2} style={{ margin: 0 }}>
              工作空间
            </Typography.Title>
            <Typography.Text type="secondary">
              管理流程图和优化任务的业务分组。
            </Typography.Text>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建工作空间
          </Button>
        </Space>

        <Card>
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <Space wrap>
              <Input
                allowClear
                prefix={<SearchOutlined />}
                placeholder="搜索工作空间名称"
                value={keyword}
                onChange={(event) => {
                  setKeyword(event.target.value)
                  setPageNo(1)
                }}
                style={{ width: 280 }}
              />
              <Button
                icon={<ReloadOutlined />}
                onClick={() => void workspaceQuery.refetch()}
                loading={workspaceQuery.isFetching}
              >
                刷新
              </Button>
            </Space>

            {workspaceQuery.isError ? (
              <Alert
                type="error"
                showIcon
                message="工作空间加载失败"
                description={getErrorMessage(workspaceQuery.error)}
                action={
                  <Button size="small" onClick={() => void workspaceQuery.refetch()}>
                    重试
                  </Button>
                }
              />
            ) : null}

            <Table<WorkspaceVO>
              rowKey="id"
              loading={workspaceQuery.isLoading || workspaceQuery.isFetching || isMutating}
              columns={columns}
              dataSource={rows}
              locale={{
                emptyText: workspaceQuery.isLoading ? (
                  '加载中...'
                ) : (
                  <Empty description="暂无工作空间" />
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
              onRow={(workspace) => ({
                onClick: () => navigate(`/workspaces/${workspace.id}/graphs`),
                style: { cursor: 'pointer' },
              })}
            />
          </Space>
        </Card>
      </Space>

      <WorkspaceFormModal
        open={isFormOpen}
        mode={editingWorkspace ? 'edit' : 'create'}
        initialValue={editingWorkspace}
        confirmLoading={createMutation.isPending || updateMutation.isPending}
        onCancel={() => {
          setIsFormOpen(false)
          setEditingWorkspace(null)
        }}
        onSubmit={handleSubmit}
      />
    </>
  )
}

export default WorkspacesPage
