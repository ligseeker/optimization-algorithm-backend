import { InboxOutlined } from '@ant-design/icons'
import { useMutation } from '@tanstack/react-query'
import {
  Alert,
  Descriptions,
  Form,
  Input,
  Modal,
  Space,
  Typography,
  Upload,
  message,
} from 'antd'
import { useState } from 'react'
import { importGraphYaml } from '../../api/yaml'
import { ApiError, type ID } from '../../types/common'
import type { GraphImportResponse } from '../../types/yaml'

type GraphYamlImportModalProps = {
  open: boolean
  workspaceId: ID
  onCancel: () => void
  onSuccess: (result: GraphImportResponse) => void | Promise<void>
}

type GraphYamlImportFormValues = {
  graphName?: string
}

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '导入失败，请稍后重试'
}

function getErrorDetails(error: unknown) {
  return error instanceof ApiError ? error.details : undefined
}

function stringifyDetails(details: unknown) {
  if (!details) {
    return ''
  }

  if (typeof details === 'string') {
    return details
  }

  try {
    return JSON.stringify(details, null, 2)
  } catch {
    return String(details)
  }
}

function isYamlFile(file: File) {
  const fileName = file.name.toLowerCase()
  return fileName.endsWith('.yaml') || fileName.endsWith('.yml')
}

function GraphYamlImportModal({
  open,
  workspaceId,
  onCancel,
  onSuccess,
}: GraphYamlImportModalProps) {
  const [form] = Form.useForm<GraphYamlImportFormValues>()
  const [messageApi, messageContextHolder] = message.useMessage()
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [importResult, setImportResult] = useState<GraphImportResponse | null>(null)

  const importMutation = useMutation({
    mutationFn: async (values: GraphYamlImportFormValues) => {
      if (!selectedFile) {
        throw new Error('请选择 .yaml 或 .yml 文件')
      }

      return importGraphYaml({
        file: selectedFile,
        workspaceId,
        graphName: values.graphName,
      })
    },
    onSuccess: async (result) => {
      setImportResult(result)
      void messageApi.success('YAML 导入成功')
      await onSuccess(result)
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  const handleOk = async () => {
    const values = await form.validateFields()
    if (!selectedFile) {
      void messageApi.error('请选择 .yaml 或 .yml 文件')
      return
    }

    importMutation.mutate(values)
  }

  const handleCancel = () => {
    form.resetFields()
    setSelectedFile(null)
    setImportResult(null)
    onCancel()
  }

  const errorDetails = stringifyDetails(getErrorDetails(importMutation.error))

  return (
    <Modal
      title="导入 YAML 流程图"
      open={open}
      forceRender
      okText="导入"
      cancelText="取消"
      confirmLoading={importMutation.isPending}
      onCancel={handleCancel}
      onOk={() => void handleOk()}
      destroyOnHidden
      width={720}
    >
      {messageContextHolder}
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <Typography.Text type="secondary">
          导入接口会上传文件、workspaceId 和可选 graphName，成功后自动刷新流程图列表。
        </Typography.Text>

        <Form form={form} layout="vertical" requiredMark="optional">
          <Form.Item
            label="流程图名称"
            name="graphName"
            rules={[{ max: 128, message: '流程图名称不能超过 128 个字符' }]}
          >
            <Input placeholder="可选；留空时以后端解析结果为准" />
          </Form.Item>
          <Form.Item label="YAML 文件" required>
            <Upload.Dragger
              accept=".yaml,.yml"
              beforeUpload={(file) => {
                if (!isYamlFile(file)) {
                  void messageApi.error('仅支持 .yaml / .yml 文件')
                  return Upload.LIST_IGNORE
                }

                setSelectedFile(file)
                setImportResult(null)
                return false
              }}
              maxCount={1}
              onRemove={() => {
                setSelectedFile(null)
                return true
              }}
            >
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽 YAML 文件到此处</p>
              <p className="ant-upload-hint">支持 .yaml / .yml，导入后会写入当前工作空间。</p>
            </Upload.Dragger>
          </Form.Item>
        </Form>

        {importMutation.isError ? (
          <Alert
            type="error"
            showIcon
            message="YAML 导入失败"
            description={
              <Space direction="vertical" style={{ width: '100%' }}>
                <Typography.Text>{getErrorMessage(importMutation.error)}</Typography.Text>
                {errorDetails ? (
                  <Typography.Paragraph
                    style={{
                      background: '#111827',
                      borderRadius: 8,
                      color: '#e5e7eb',
                      marginBottom: 0,
                      maxHeight: 240,
                      overflow: 'auto',
                      padding: 12,
                      whiteSpace: 'pre-wrap',
                    }}
                  >
                    {errorDetails}
                  </Typography.Paragraph>
                ) : null}
              </Space>
            }
          />
        ) : null}

        {importResult ? (
          <Alert
            type="success"
            showIcon
            message="导入成功"
            description={
              <Descriptions column={2} size="small">
                <Descriptions.Item label="graphId">{importResult.graphId}</Descriptions.Item>
                <Descriptions.Item label="graphName">
                  {importResult.graphName}
                </Descriptions.Item>
                <Descriptions.Item label="nodeCount">
                  {importResult.nodeCount}
                </Descriptions.Item>
                <Descriptions.Item label="pathCount">
                  {importResult.pathCount}
                </Descriptions.Item>
                <Descriptions.Item label="equipmentCount">
                  {importResult.equipmentCount}
                </Descriptions.Item>
                <Descriptions.Item label="constraintCount">
                  {importResult.constraintCount}
                </Descriptions.Item>
              </Descriptions>
            }
          />
        ) : null}
      </Space>
    </Modal>
  )
}

export default GraphYamlImportModal
