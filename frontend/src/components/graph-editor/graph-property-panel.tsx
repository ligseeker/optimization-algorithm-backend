import { Card, Descriptions, Empty, Tag } from 'antd'
import type { NodeVO } from '../../types/node'
import type { PathVO } from '../../types/path'

export type SelectedGraphElement =
  | {
      type: 'node'
      value: NodeVO
    }
  | {
      type: 'path'
      value: PathVO
    }

type GraphPropertyPanelProps = {
  selectedElement: SelectedGraphElement | null
}

function GraphPropertyPanel({ selectedElement }: GraphPropertyPanelProps) {
  if (!selectedElement) {
    return (
      <Card title="属性面板" size="small">
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="点击节点或路径查看详情" />
      </Card>
    )
  }

  if (selectedElement.type === 'node') {
    const node = selectedElement.value
    return (
      <Card title="节点属性" size="small">
        <Descriptions column={1} size="small">
          <Descriptions.Item label="ID">{node.id}</Descriptions.Item>
          <Descriptions.Item label="编码">{node.nodeCode}</Descriptions.Item>
          <Descriptions.Item label="名称">{node.nodeName || '-'}</Descriptions.Item>
          <Descriptions.Item label="描述">{node.nodeDescription || '-'}</Descriptions.Item>
          <Descriptions.Item label="装备">{node.equipmentId ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="耗时">{node.timeCost ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="精度">{node.precisionValue ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="成本">{node.costValue ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="排序">{node.sortNo ?? '-'}</Descriptions.Item>
        </Descriptions>
      </Card>
    )
  }

  const path = selectedElement.value
  return (
    <Card title="路径属性" size="small">
      <Descriptions column={1} size="small">
        <Descriptions.Item label="ID">{path.id}</Descriptions.Item>
        <Descriptions.Item label="起点">{path.startNodeId}</Descriptions.Item>
        <Descriptions.Item label="终点">{path.endNodeId}</Descriptions.Item>
        <Descriptions.Item label="关系">
          {path.relationType ? <Tag color="blue">{path.relationType}</Tag> : '-'}
        </Descriptions.Item>
        <Descriptions.Item label="备注">{path.remark || '-'}</Descriptions.Item>
      </Descriptions>
    </Card>
  )
}

export default GraphPropertyPanel
