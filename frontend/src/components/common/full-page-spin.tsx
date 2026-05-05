import { Flex, Spin, Typography } from 'antd'

type FullPageSpinProps = {
  tip: string
}

function FullPageSpin({ tip }: FullPageSpinProps) {
  return (
    <Flex
      vertical
      align="center"
      justify="center"
      gap={16}
      style={{ minHeight: '100vh', padding: 24 }}
    >
      <Spin size="large" />
      <Typography.Text type="secondary">{tip}</Typography.Text>
    </Flex>
  )
}

export default FullPageSpin
