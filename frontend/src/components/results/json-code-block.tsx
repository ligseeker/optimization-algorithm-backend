import { Typography } from 'antd'

type JsonCodeBlockProps = {
  value: unknown
  emptyText?: string
}

function stringifyValue(value: unknown) {
  if (value === null || value === undefined || value === '') {
    return ''
  }

  if (typeof value === 'string') {
    return value
  }

  try {
    return JSON.stringify(value, null, 2)
  } catch {
    return String(value)
  }
}

function JsonCodeBlock({ value, emptyText = '暂无数据' }: JsonCodeBlockProps) {
  const content = stringifyValue(value)

  return (
    <Typography.Paragraph
      style={{
        background: '#111827',
        borderRadius: 8,
        color: '#e5e7eb',
        marginBottom: 0,
        maxHeight: 360,
        overflow: 'auto',
        padding: 16,
        whiteSpace: 'pre-wrap',
      }}
    >
      {content || emptyText}
    </Typography.Paragraph>
  )
}

export default JsonCodeBlock
