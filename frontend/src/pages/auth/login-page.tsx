import { Alert, Button, Card, Form, Input, Space, Typography, message } from 'antd'
import { useNavigate } from 'react-router-dom'
import { useDocumentTitle } from '../../hooks/use-document-title'
import { useAuthStore } from '../../store/auth-store'
import { HTTP_NETWORK_ERROR_CODE, isApiError } from '../../utils/api-error'

type LoginFormValues = {
  username: string
  password: string
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '后端服务'

function resolveLoginErrorMessage(error: unknown) {
  if (isApiError(error)) {
    if (error.code === HTTP_NETWORK_ERROR_CODE) {
      return `无法连接后端服务，请确认 ${apiBaseUrl} 已启动。`
    }

    return error.message || '登录失败，请检查用户名和密码。'
  }

  if (error instanceof Error) {
    return error.message
  }

  return '登录失败，请稍后重试。'
}

function LoginPage() {
  useDocumentTitle('Login')

  const navigate = useNavigate()
  const [messageApi, contextHolder] = message.useMessage()
  const login = useAuthStore((state) => state.login)
  const isSubmitting = useAuthStore((state) => state.isSubmitting)
  const lastError = useAuthStore((state) => state.lastError)

  const handleFinish = async (values: LoginFormValues) => {
    try {
      await login(values)
      navigate('/dashboard', { replace: true })
    } catch (error) {
      void messageApi.error(resolveLoginErrorMessage(error))
    }
  }

  return (
    <>
      {contextHolder}
      <main className="app-shell">
        <Card style={{ width: 'min(420px, 100%)' }}>
          <Space direction="vertical" size="large" style={{ width: '100%' }}>
            <div>
              <Typography.Title level={2} style={{ marginBottom: 8 }}>
                登录系统
              </Typography.Title>
              <Typography.Text type="secondary">
                使用已分配账号进入流程优化任务管理系统。
              </Typography.Text>
            </div>

            {lastError ? (
              <Alert
                type="error"
                showIcon
                message="登录未成功"
                description={lastError}
              />
            ) : null}

            <Form<LoginFormValues>
              layout="vertical"
              initialValues={{ username: 'admin', password: 'admin123' }}
              onFinish={handleFinish}
              autoComplete="off"
            >
              <Form.Item
                label="用户名"
                name="username"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input placeholder="请输入用户名" disabled={isSubmitting} />
              </Form.Item>
              <Form.Item
                label="密码"
                name="password"
                rules={[{ required: true, message: '请输入密码' }]}
              >
                <Input.Password placeholder="请输入密码" disabled={isSubmitting} />
              </Form.Item>
              <Button type="primary" htmlType="submit" block loading={isSubmitting}>
                登录
              </Button>
            </Form>
          </Space>
        </Card>
      </main>
    </>
  )
}

export default LoginPage
