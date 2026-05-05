import { Button, Result } from 'antd'
import { useNavigate } from 'react-router-dom'

function NotFoundState() {
  const navigate = useNavigate()

  return (
    <Result
      status="404"
      title="404"
      subTitle="当前页面不存在，请返回系统首页继续操作。"
      extra={
        <Button type="primary" onClick={() => navigate('/dashboard', { replace: true })}>
          返回 Dashboard
        </Button>
      }
    />
  )
}

export default NotFoundState
