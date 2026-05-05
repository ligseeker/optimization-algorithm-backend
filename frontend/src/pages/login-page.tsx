import { Alert, Button, Card, Form, Input, Space, Typography } from 'antd'
import { useDocumentTitle } from '../hooks/use-document-title'

function LoginPage() {
  useDocumentTitle('Login')

  return (
    <main className="app-shell">
      <Card style={{ width: 'min(420px, 100%)' }}>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div>
            <Typography.Title level={2} style={{ marginBottom: 8 }}>
              Login
            </Typography.Title>
            <Typography.Text type="secondary">
              Authentication wiring will be added in the auth phase.
            </Typography.Text>
          </div>
          <Alert
            type="info"
            showIcon
            message="Phase 1 placeholder"
            description="This screen reserves the login route and form structure without calling the backend yet."
          />
          <Form layout="vertical">
            <Form.Item label="Username">
              <Input placeholder="admin" />
            </Form.Item>
            <Form.Item label="Password">
              <Input.Password placeholder="••••••••" />
            </Form.Item>
            <Button type="primary" block>
              Sign in
            </Button>
          </Form>
        </Space>
      </Card>
    </main>
  )
}

export default LoginPage
