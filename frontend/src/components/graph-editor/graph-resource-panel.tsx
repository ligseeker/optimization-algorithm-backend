import { Card, Descriptions, List, Space, Typography } from 'antd'
import type { GraphDetailVO } from '../../types/graph'

type GraphResourcePanelProps = {
  detail: GraphDetailVO
}

function GraphResourcePanel({ detail }: GraphResourcePanelProps) {
  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Card title="图资源" size="small">
        <Descriptions column={1} size="small">
          <Descriptions.Item label="节点">{detail.nodes.length}</Descriptions.Item>
          <Descriptions.Item label="路径">{detail.paths.length}</Descriptions.Item>
          <Descriptions.Item label="装备">{detail.equipments.length}</Descriptions.Item>
          <Descriptions.Item label="约束">{detail.constraints.length}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="装备" size="small">
        <List
          size="small"
          dataSource={detail.equipments}
          locale={{ emptyText: '暂无装备' }}
          renderItem={(equipment) => (
            <List.Item>
              <Space direction="vertical" size={0}>
                <Typography.Text strong>{equipment.name}</Typography.Text>
                <Typography.Text type="secondary">
                  {equipment.description || equipment.color || '-'}
                </Typography.Text>
              </Space>
            </List.Item>
          )}
        />
      </Card>

      <Card title="约束" size="small">
        <List
          size="small"
          dataSource={detail.constraints}
          locale={{ emptyText: '暂无约束' }}
          renderItem={(constraint) => (
            <List.Item>
              <Space direction="vertical" size={0}>
                <Typography.Text strong>{constraint.conditionCode}</Typography.Text>
                <Typography.Text type="secondary">
                  {constraint.conditionDescription || constraint.conditionType}
                </Typography.Text>
              </Space>
            </List.Item>
          )}
        />
      </Card>
    </Space>
  )
}

export default GraphResourcePanel
