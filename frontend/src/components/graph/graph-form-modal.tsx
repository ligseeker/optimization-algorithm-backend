import { Form, Input, InputNumber, Modal } from 'antd'
import { useEffect } from 'react'
import type { GraphVO } from '../../types/graph'

export type GraphFormValues = {
  name: string
  description?: string
  sourceType?: string
  graphStatus?: string
  totalTime?: number
  totalPrecision?: number
  totalCost?: number
}

type GraphFormModalProps = {
  open: boolean
  mode: 'create' | 'edit'
  initialValue?: GraphVO | null
  confirmLoading?: boolean
  onCancel: () => void
  onSubmit: (values: GraphFormValues) => void
}

function GraphFormModal({
  open,
  mode,
  initialValue,
  confirmLoading,
  onCancel,
  onSubmit,
}: GraphFormModalProps) {
  const [form] = Form.useForm<GraphFormValues>()

  useEffect(() => {
    if (!open) {
      form.resetFields()
      return
    }

    form.setFieldsValue({
      name: initialValue?.name ?? '',
      description: initialValue?.description ?? '',
      sourceType: initialValue?.sourceType ?? 'MANUAL',
      graphStatus: initialValue?.graphStatus ?? 'DRAFT',
      totalTime: initialValue?.totalTime ?? undefined,
      totalPrecision: initialValue?.totalPrecision ?? undefined,
      totalCost: initialValue?.totalCost ?? undefined,
    })
  }, [form, initialValue, open])

  const handleOk = async () => {
    const values = await form.validateFields()
    onSubmit(values)
  }

  return (
    <Modal
      title={mode === 'create' ? '新建流程图' : '编辑流程图'}
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
            { required: true, message: '请输入流程图名称' },
            { max: 128, message: '名称不能超过 128 个字符' },
          ]}
        >
          <Input placeholder="例如：装配流程图 A" />
        </Form.Item>
        <Form.Item
          label="描述"
          name="description"
          rules={[{ max: 500, message: '描述不能超过 500 个字符' }]}
        >
          <Input.TextArea rows={3} placeholder="可选，记录流程图用途" />
        </Form.Item>
        {mode === 'create' ? (
          <Form.Item
            label="来源类型"
            name="sourceType"
            rules={[{ max: 32, message: '来源类型不能超过 32 个字符' }]}
          >
            <Input placeholder="MANUAL" />
          </Form.Item>
        ) : null}
        <Form.Item
          label="流程图状态"
          name="graphStatus"
          rules={[{ max: 32, message: '状态不能超过 32 个字符' }]}
        >
          <Input placeholder="DRAFT / READY" />
        </Form.Item>
        {mode === 'edit' ? (
          <>
            <Form.Item label="总时间" name="totalTime">
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item label="总精度" name="totalPrecision">
              <InputNumber min={0} precision={4} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item label="总成本" name="totalCost">
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
          </>
        ) : null}
      </Form>
    </Modal>
  )
}

export default GraphFormModal
