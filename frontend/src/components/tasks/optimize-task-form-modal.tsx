import { Form, InputNumber, Modal } from 'antd'
import { useEffect } from 'react'

export type OptimizeTaskFormValues = {
  graphId: number
  algorithmType: number
  algorithmMode: number
  timeWeight?: number
  precisionWeight?: number
  costWeight?: number
}

type OptimizeTaskFormModalProps = {
  open: boolean
  confirmLoading?: boolean
  initialGraphId?: number | null
  onCancel: () => void
  onSubmit: (values: OptimizeTaskFormValues) => void
}

function OptimizeTaskFormModal({
  open,
  confirmLoading,
  initialGraphId,
  onCancel,
  onSubmit,
}: OptimizeTaskFormModalProps) {
  const [form] = Form.useForm<OptimizeTaskFormValues>()

  useEffect(() => {
    if (!open) {
      form.resetFields()
      return
    }

    form.setFieldsValue({
      graphId: initialGraphId ?? undefined,
      algorithmType: 1,
      algorithmMode: 2,
      timeWeight: 1,
      precisionWeight: 1,
      costWeight: 1,
    })
  }, [form, initialGraphId, open])

  const handleOk = async () => {
    const values = await form.validateFields()
    onSubmit(values)
  }

  return (
    <Modal
      title="提交优化任务"
      open={open}
      okText="提交"
      cancelText="取消"
      confirmLoading={confirmLoading}
      onCancel={onCancel}
      onOk={() => void handleOk()}
      destroyOnHidden
    >
      <Form form={form} layout="vertical" requiredMark="optional">
        <Form.Item
          label="流程图 ID"
          name="graphId"
          rules={[{ required: true, message: '请输入流程图 ID' }]}
        >
          <InputNumber min={1} precision={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          label="算法类型"
          name="algorithmType"
          rules={[{ required: true, message: '请输入算法类型' }]}
        >
          <InputNumber min={1} max={3} precision={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          label="算法模式"
          name="algorithmMode"
          rules={[{ required: true, message: '请输入算法模式' }]}
        >
          <InputNumber min={0} max={2} precision={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item label="时间权重" name="timeWeight">
          <InputNumber min={1} max={100} precision={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item label="精度权重" name="precisionWeight">
          <InputNumber min={1} max={100} precision={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item label="成本权重" name="costWeight">
          <InputNumber min={1} max={100} precision={0} style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default OptimizeTaskFormModal
