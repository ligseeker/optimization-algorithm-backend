import { Form, Input, InputNumber, Modal, Select, Switch } from 'antd'
import { useEffect } from 'react'
import type { GraphDetailVO } from '../../types/graph'
import type { ConstraintVO } from '../../types/constraint'
import type { EquipmentVO } from '../../types/equipment'
import type { NodeVO } from '../../types/node'
import type { PathVO } from '../../types/path'
import type {
  EditingGraphElement,
  GraphElementFormValues,
  GraphElementKind,
} from './graph-element-types'

type GraphElementFormModalProps = {
  open: boolean
  editingElement: EditingGraphElement | null
  detail: GraphDetailVO
  confirmLoading?: boolean
  dirty?: boolean
  onCancel: () => void
  onDirtyChange: (dirty: boolean) => void
  onSubmit: (values: GraphElementFormValues) => void
}

function getElementTitle(kind: GraphElementKind, isEdit: boolean) {
  const labels: Record<GraphElementKind, string> = {
    constraint: '约束',
    equipment: '装备',
    node: '节点',
    path: '路径',
  }

  return `${isEdit ? '编辑' : '新增'}${labels[kind]}`
}

function getInitialValues(editingElement: EditingGraphElement | null): GraphElementFormValues {
  if (!editingElement?.value) {
    return {
      enabled: 1,
    }
  }

  if (editingElement.kind === 'node') {
    const node = editingElement.value as NodeVO
    return {
      nodeCode: node.nodeCode,
      nodeName: node.nodeName ?? undefined,
      nodeDescription: node.nodeDescription ?? undefined,
      equipmentId: node.equipmentId ?? undefined,
      timeCost: node.timeCost ?? undefined,
      precisionValue: node.precisionValue ?? undefined,
      costValue: node.costValue ?? undefined,
      sortNo: node.sortNo ?? undefined,
    }
  }

  if (editingElement.kind === 'path') {
    const path = editingElement.value as PathVO
    return {
      startNodeId: path.startNodeId,
      endNodeId: path.endNodeId,
      relationType: path.relationType ?? undefined,
      remark: path.remark ?? undefined,
    }
  }

  if (editingElement.kind === 'equipment') {
    const equipment = editingElement.value as EquipmentVO
    return {
      name: equipment.name,
      description: equipment.description ?? undefined,
      color: equipment.color ?? undefined,
      imagePath: equipment.imagePath ?? undefined,
    }
  }

  const constraint = editingElement.value as ConstraintVO
  return {
    conditionCode: constraint.conditionCode,
    conditionType: constraint.conditionType,
    conditionDescription: constraint.conditionDescription ?? undefined,
    nodeId1: constraint.nodeId1,
    nodeId2: constraint.nodeId2,
    enabled: constraint.enabled,
  }
}

function GraphElementFormModal({
  open,
  editingElement,
  detail,
  confirmLoading,
  dirty,
  onCancel,
  onDirtyChange,
  onSubmit,
}: GraphElementFormModalProps) {
  const [form] = Form.useForm<GraphElementFormValues>()

  useEffect(() => {
    if (!open) {
      form.resetFields()
      return
    }

    form.setFieldsValue(getInitialValues(editingElement))
  }, [editingElement, form, open])

  const handleOk = async () => {
    const values = await form.validateFields()
    onSubmit(values)
  }

  const nodeOptions = detail.nodes.map((node) => ({
    label: node.nodeName || node.nodeCode,
    value: node.id,
  }))

  const equipmentOptions = detail.equipments.map((equipment) => ({
    label: equipment.name,
    value: equipment.id,
  }))

  const kind = editingElement?.kind
  const isEdit = Boolean(editingElement?.value)

  return (
    <Modal
      title={kind ? getElementTitle(kind, isEdit) : '图元'}
      open={open}
      okText={isEdit ? '保存' : '创建'}
      cancelText="取消"
      confirmLoading={confirmLoading}
      onCancel={onCancel}
      onOk={() => void handleOk()}
      destroyOnHidden
    >
      <Form
        form={form}
        layout="vertical"
        requiredMark="optional"
        onValuesChange={() => {
          if (!dirty) {
            onDirtyChange(true)
          }
        }}
      >
        {kind === 'node' ? (
          <>
            <Form.Item
              label="节点编码"
              name="nodeCode"
              rules={[
                { required: true, message: '请输入节点编码' },
                { max: 64, message: '节点编码不能超过 64 个字符' },
              ]}
            >
              <Input placeholder="nodeCode" />
            </Form.Item>
            <Form.Item label="节点名称" name="nodeName" rules={[{ max: 128 }]}>
              <Input placeholder="nodeName" />
            </Form.Item>
            <Form.Item label="节点描述" name="nodeDescription" rules={[{ max: 500 }]}>
              <Input.TextArea rows={3} placeholder="nodeDescription" />
            </Form.Item>
            <Form.Item label="装备" name="equipmentId">
              <Select allowClear options={equipmentOptions} placeholder="选择装备" />
            </Form.Item>
            <Form.Item label="耗时" name="timeCost">
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item label="精度" name="precisionValue">
              <InputNumber min={0} precision={4} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item label="成本" name="costValue">
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item label="排序" name="sortNo">
              <InputNumber precision={0} style={{ width: '100%' }} />
            </Form.Item>
          </>
        ) : null}

        {kind === 'path' ? (
          <>
            <Form.Item
              label="起点"
              name="startNodeId"
              rules={[{ required: true, message: '请选择起点节点' }]}
            >
              <Select options={nodeOptions} placeholder="startNodeId" />
            </Form.Item>
            <Form.Item
              label="终点"
              name="endNodeId"
              rules={[{ required: true, message: '请选择终点节点' }]}
            >
              <Select options={nodeOptions} placeholder="endNodeId" />
            </Form.Item>
            <Form.Item label="关系类型" name="relationType" rules={[{ max: 64 }]}>
              <Input placeholder="relationType" />
            </Form.Item>
            <Form.Item label="备注" name="remark" rules={[{ max: 500 }]}>
              <Input.TextArea rows={3} placeholder="remark" />
            </Form.Item>
          </>
        ) : null}

        {kind === 'equipment' ? (
          <>
            <Form.Item
              label="装备名称"
              name="name"
              rules={[
                { required: true, message: '请输入装备名称' },
                { max: 128, message: '装备名称不能超过 128 个字符' },
              ]}
            >
              <Input placeholder="name" />
            </Form.Item>
            <Form.Item label="描述" name="description" rules={[{ max: 500 }]}>
              <Input.TextArea rows={3} placeholder="description" />
            </Form.Item>
            <Form.Item label="颜色" name="color" rules={[{ max: 32 }]}>
              <Input placeholder="#2563eb" />
            </Form.Item>
            <Form.Item label="图片路径" name="imagePath" rules={[{ max: 500 }]}>
              <Input placeholder="imagePath" />
            </Form.Item>
          </>
        ) : null}

        {kind === 'constraint' ? (
          <>
            <Form.Item
              label="条件编码"
              name="conditionCode"
              rules={[
                { required: true, message: '请输入条件编码' },
                { max: 64, message: '条件编码不能超过 64 个字符' },
              ]}
            >
              <Input placeholder="conditionCode" />
            </Form.Item>
            <Form.Item
              label="条件类型"
              name="conditionType"
              rules={[
                { required: true, message: '请输入条件类型' },
                { max: 64, message: '条件类型不能超过 64 个字符' },
              ]}
            >
              <Input placeholder="conditionType" />
            </Form.Item>
            <Form.Item label="条件描述" name="conditionDescription" rules={[{ max: 500 }]}>
              <Input.TextArea rows={3} placeholder="conditionDescription" />
            </Form.Item>
            <Form.Item
              label="节点 1"
              name="nodeId1"
              rules={[{ required: true, message: '请选择节点 1' }]}
            >
              <Select options={nodeOptions} placeholder="nodeId1" />
            </Form.Item>
            <Form.Item
              label="节点 2"
              name="nodeId2"
              rules={[{ required: true, message: '请选择节点 2' }]}
            >
              <Select options={nodeOptions} placeholder="nodeId2" />
            </Form.Item>
            <Form.Item label="启用" name="enabled" valuePropName="checked" getValueFromEvent={(checked: boolean) => (checked ? 1 : 0)}>
              <Switch checkedChildren="启用" unCheckedChildren="停用" />
            </Form.Item>
          </>
        ) : null}
      </Form>
    </Modal>
  )
}

export default GraphElementFormModal
