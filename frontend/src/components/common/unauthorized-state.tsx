import { Button, Result } from 'antd'
import { useNavigate } from 'react-router-dom'

function UnauthorizedState() {
  const navigate = useNavigate()

  return (
    <Result
      status="403"
      title="未授权"
      subTitle="当前账号暂时没有访问该页面的权限。"
      extra={
        <Button type="primary" onClick={() => navigate('/dashboard', { replace: true })}>
          返回 Dashboard
        </Button>
      }
    />
  )
}

export default UnauthorizedState
