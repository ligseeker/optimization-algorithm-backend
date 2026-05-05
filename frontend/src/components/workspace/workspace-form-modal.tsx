import { Form, Input, Modal, Select } from 'antd'
import { useEffect } from 'react'
import type { WorkspaceVO } from '../../types/workspace'

export type WorkspaceFormValues = {
  name: string
  description?: string
  status?: 0 | 1
}

type WorkspaceFormModalProps = {
  open: boolean
  mode: 'create' | 'edit'
  initialValue?: WorkspaceVO | null
  confirmLoading?: boolean
  onCancel: () => void
  onSubmit: (values: WorkspaceFormValues) => void
}

function WorkspaceFormModal({
  open,
  mode,
  initialValue,
  confirmLoading,
  onCancel,
  onSubmit,
}: WorkspaceFormModalProps) {
  const [form] = Form.useForm<WorkspaceFormValues>()

  useEffect(() => {
    if (!open) {
      form.resetFields()
      return
    }

    form.setFieldsValue({
      name: initialValue?.name ?? '',
      description: initialValue?.description ?? '',
      status: initialValue?.status ?? 1,
    })
  }, [form, initialValue, open])

  const handleOk = async () => {
    const values = await form.validateFields()
    onSubmit(values)
  }

  return (
    <Modal
      title={mode === 'create' ? '新建工作空间' : '编辑工作空间'}
      open={open}
      okText={mode === 'create' ? '创建' : '保存'}
      cancelText="取消"
      confirmLoading={confirmLoading}
      onCancel={onCancel}
      onOk={() => void handleOk()}
      destroyOnHidden
    >
      <Form form={form} layout="vertical" requiredMark="optional">
        <Form.Item
          label="名称"
          name="name"
          rules={[
            { required: true, message: '请输入工作空间名称' },
            { max: 128, message: '名称不能超过 128 个字符' },
          ]}
        >
          <Input placeholder="例如：默认工作空间" />
        </Form.Item>
        <Form.Item
          label="描述"
          name="description"
          rules={[{ max: 500, message: '描述不能超过 500 个字符' }]}
        >
          <Input.TextArea rows={4} placeholder="可选，记录用途或说明" />
        </Form.Item>
        {mode === 'edit' ? (
          <Form.Item label="状态" name="status">
            <Select
              options={[
                { label: '启用', value: 1 },
                { label: '停用', value: 0 },
              ]}
            />
          </Form.Item>
        ) : null}
      </Form>
    </Modal>
  )
}

export default WorkspaceFormModal
