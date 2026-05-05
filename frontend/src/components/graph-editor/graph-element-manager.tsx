import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import { Button, Card, List, Space, Tabs, Tag, Typography } from 'antd'
import type { GraphDetailVO } from '../../types/graph'
import type {
  GraphElementKind,
  GraphElementValue,
} from './graph-element-types'

type GraphElementManagerProps = {
  detail: GraphDetailVO
  deleting?: boolean
  onCreate: (kind: GraphElementKind) => void
  onEdit: (kind: GraphElementKind, value: GraphElementValue) => void
  onDelete: (kind: GraphElementKind, value: GraphElementValue) => void
}

function GraphElementManager({
  detail,
  deleting,
  onCreate,
  onEdit,
  onDelete,
}: GraphElementManagerProps) {
  return (
    <Card title="图元管理" size="small">
      <Tabs
        size="small"
        items={[
          {
            key: 'node',
            label: `节点 ${detail.nodes.length}`,
            children: (
              <Space direction="vertical" style={{ width: '100%' }}>
                <Button block icon={<PlusOutlined />} onClick={() => onCreate('node')}>
                  新增节点
                </Button>
                <List
                  size="small"
                  dataSource={detail.nodes}
                  locale={{ emptyText: '暂无节点' }}
                  renderItem={(node) => (
                    <List.Item
                      actions={[
                        <Button key="edit" size="small" icon={<EditOutlined />} onClick={() => onEdit('node', node)} />,
                        <Button
                          key="delete"
                          danger
                          size="small"
                          icon={<DeleteOutlined />}
                          loading={deleting}
                          onClick={() => onDelete('node', node)}
                        />,
                      ]}
                    >
                      <List.Item.Meta
                        title={node.nodeName || node.nodeCode}
                        description={`code: ${node.nodeCode}`}
                      />
                    </List.Item>
                  )}
                />
              </Space>
            ),
          },
          {
            key: 'path',
            label: `路径 ${detail.paths.length}`,
            children: (
              <Space direction="vertical" style={{ width: '100%' }}>
                <Button block icon={<PlusOutlined />} onClick={() => onCreate('path')}>
                  新增路径
                </Button>
                <List
                  size="small"
                  dataSource={detail.paths}
                  locale={{ emptyText: '暂无路径' }}
                  renderItem={(path) => (
                    <List.Item
                      actions={[
                        <Button key="edit" size="small" icon={<EditOutlined />} onClick={() => onEdit('path', path)} />,
                        <Button
                          key="delete"
                          danger
                          size="small"
                          icon={<DeleteOutlined />}
                          loading={deleting}
                          onClick={() => onDelete('path', path)}
                        />,
                      ]}
                    >
                      <List.Item.Meta
                        title={`${path.startNodeId} -> ${path.endNodeId}`}
                        description={path.relationType || path.remark || '-'}
                      />
                    </List.Item>
                  )}
                />
              </Space>
            ),
          },
          {
            key: 'equipment',
            label: `装备 ${detail.equipments.length}`,
            children: (
              <Space direction="vertical" style={{ width: '100%' }}>
                <Button block icon={<PlusOutlined />} onClick={() => onCreate('equipment')}>
                  新增装备
                </Button>
                <List
                  size="small"
                  dataSource={detail.equipments}
                  locale={{ emptyText: '暂无装备' }}
                  renderItem={(equipment) => (
                    <List.Item
                      actions={[
                        <Button key="edit" size="small" icon={<EditOutlined />} onClick={() => onEdit('equipment', equipment)} />,
                        <Button
                          key="delete"
                          danger
                          size="small"
                          icon={<DeleteOutlined />}
                          loading={deleting}
                          onClick={() => onDelete('equipment', equipment)}
                        />,
                      ]}
                    >
                      <List.Item.Meta
                        title={
                          <Space>
                            <Typography.Text>{equipment.name}</Typography.Text>
                            {equipment.color ? <Tag color={equipment.color}>{equipment.color}</Tag> : null}
                          </Space>
                        }
                        description={equipment.description || '-'}
                      />
                    </List.Item>
                  )}
                />
              </Space>
            ),
          },
          {
            key: 'constraint',
            label: `约束 ${detail.constraints.length}`,
            children: (
              <Space direction="vertical" style={{ width: '100%' }}>
                <Button block icon={<PlusOutlined />} onClick={() => onCreate('constraint')}>
                  新增约束
                </Button>
                <List
                  size="small"
                  dataSource={detail.constraints}
                  locale={{ emptyText: '暂无约束' }}
                  renderItem={(constraint) => (
                    <List.Item
                      actions={[
                        <Button key="edit" size="small" icon={<EditOutlined />} onClick={() => onEdit('constraint', constraint)} />,
                        <Button
                          key="delete"
                          danger
                          size="small"
                          icon={<DeleteOutlined />}
                          loading={deleting}
                          onClick={() => onDelete('constraint', constraint)}
                        />,
                      ]}
                    >
                      <List.Item.Meta
                        title={constraint.conditionCode}
                        description={`${constraint.conditionType} / ${constraint.enabled ? '启用' : '停用'}`}
                      />
                    </List.Item>
                  )}
                />
              </Space>
            ),
          },
        ]}
      />
    </Card>
  )
}

export default GraphElementManager
