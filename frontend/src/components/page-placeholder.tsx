import { Card, Space, Tag, Typography } from 'antd'
import { useDocumentTitle } from '../hooks/use-document-title'

type PagePlaceholderProps = {
  title: string
  description: string
  badge?: string
  notes?: string[]
}

function PagePlaceholder({
  title,
  description,
  badge = 'Operational Preview',
  notes = [],
}: PagePlaceholderProps) {
  useDocumentTitle(title)

  return (
    <div className="page-placeholder">
      <Card bordered={false}>
        <Space direction="vertical" size="middle">
          <Tag color="gold">{badge}</Tag>
          <Typography.Title level={2} style={{ margin: 0 }}>
            {title}
          </Typography.Title>
          <Typography.Paragraph type="secondary" style={{ margin: 0 }}>
            {description}
          </Typography.Paragraph>
        </Space>
      </Card>
      {notes.length > 0 ? (
        <Card title="Current focus">
          <Space direction="vertical" size="small">
            {notes.map((note) => (
              <Typography.Text key={note}>{note}</Typography.Text>
            ))}
          </Space>
        </Card>
      ) : null}
    </div>
  )
}

export default PagePlaceholder
