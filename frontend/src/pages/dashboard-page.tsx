import { CompassOutlined, NodeIndexOutlined, ShareAltOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import { Alert, Button, Card, Col, Empty, Row, Space, Typography } from 'antd'
import { useNavigate } from 'react-router-dom'
import TaskStatusTag from '../components/tasks/task-status-tag'
import { getOptimizeTaskPage } from '../api/optimize-task'
import { getWorkspacePage } from '../api/workspace'
import { useDocumentTitle } from '../hooks/use-document-title'

function DashboardPage() {
  useDocumentTitle('Dashboard')

  const navigate = useNavigate()
  const workspaceQuery = useQuery({
    queryKey: ['dashboard-workspaces'],
    queryFn: () =>
      getWorkspacePage({
        pageNo: 1,
        pageSize: 6,
      }),
  })
  const taskQuery = useQuery({
    queryKey: ['dashboard-tasks'],
    queryFn: () =>
      getOptimizeTaskPage({
        pageNo: 1,
        pageSize: 12,
      }),
  })

  const workspaceRecords = workspaceQuery.data?.records ?? []
  const taskRecords = taskQuery.data?.records ?? []

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <section className="console-hero">
        <div className="console-hero-copy">
          <span className="console-kicker">Operations Overview</span>
          <Typography.Title level={1} className="console-title">
            Industrial Control Surface
          </Typography.Title>
          <Typography.Paragraph className="console-subtitle">
            把工作空间、流程图、优化任务和结果分析放进同一个操作视野里。这里优先展示最近的运行态，而不是静态占位信息。
          </Typography.Paragraph>
        </div>
        <div className="console-hero-meta">
          <div className="console-meta-chip">
            <span className="console-meta-label">Recent workspaces</span>
            <strong>{workspaceQuery.data?.total ?? 0}</strong>
          </div>
          <div className="console-meta-chip">
            <span className="console-meta-label">Running queue</span>
            <strong>
              {taskRecords.filter((item) => ['PENDING', 'RUNNING'].includes(item.taskStatus)).length}
            </strong>
          </div>
          <div className="console-meta-chip">
            <span className="console-meta-label">Successful tasks</span>
            <strong>{taskRecords.filter((item) => item.taskStatus === 'SUCCESS').length}</strong>
          </div>
        </div>
      </section>

      {(workspaceQuery.isError || taskQuery.isError) ? (
        <Alert
          type="warning"
          showIcon
          message="Dashboard 数据加载不完整"
          description="部分摘要暂时不可用，你仍然可以直接进入具体模块继续操作。"
        />
      ) : null}

      <div className="console-page-grid">
        <Card className="console-stat-card">
          <div className="console-stat-label">Workspace fleet</div>
          <div className="console-stat-value">{workspaceQuery.data?.total ?? 0}</div>
          <div className="console-stat-footnote">最近 6 个工作空间进入总览</div>
        </Card>
        <Card className="console-stat-card">
          <div className="console-stat-label">Task throughput</div>
          <div className="console-stat-value">{taskQuery.data?.total ?? 0}</div>
          <div className="console-stat-footnote">最近任务记录总数</div>
        </Card>
        <Card className="console-stat-card">
          <div className="console-stat-label">Signal quality</div>
          <div className="console-stat-value">{taskRecords.length > 0 ? 'Live' : 'Idle'}</div>
          <div className="console-stat-footnote">最近任务页是否已有运行信号</div>
        </Card>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} xl={14}>
          <Card
            className="console-panel"
            title={
              <div className="console-panel-title">
                <span className="console-panel-kicker">Workspaces</span>
                <span>Recent operating groups</span>
              </div>
            }
          >
            {workspaceRecords.length === 0 ? (
              <Empty className="console-empty" description="暂无工作空间摘要">
                <Button type="primary" onClick={() => navigate('/workspaces')}>
                  进入工作空间
                </Button>
              </Empty>
            ) : (
              <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                {workspaceRecords.map((workspace) => (
                  <Card key={workspace.id} size="small">
                    <Space
                      align="start"
                      style={{ justifyContent: 'space-between', width: '100%' }}
                    >
                      <div>
                        <Typography.Text strong>{workspace.name}</Typography.Text>
                        <Typography.Paragraph type="secondary" style={{ marginBottom: 0 }}>
                          {workspace.description || '暂无工作空间描述'}
                        </Typography.Paragraph>
                      </div>
                      <Button type="link" onClick={() => navigate(`/workspaces/${workspace.id}/graphs`)}>
                        查看流程图
                      </Button>
                    </Space>
                  </Card>
                ))}
              </Space>
            )}
          </Card>
        </Col>
        <Col xs={24} xl={10}>
          <Card
            className="console-panel"
            title={
              <div className="console-panel-title">
                <span className="console-panel-kicker">Task Radar</span>
                <span>Latest execution signals</span>
              </div>
            }
          >
            {taskRecords.length === 0 ? (
              <Empty className="console-empty" description="暂无任务动态">
                <Button type="primary" onClick={() => navigate('/tasks')}>
                  进入任务中心
                </Button>
              </Empty>
            ) : (
              <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                {taskRecords.slice(0, 5).map((task) => (
                  <Card key={task.id} size="small">
                    <Space
                      align="start"
                      style={{ justifyContent: 'space-between', width: '100%' }}
                    >
                      <div>
                        <Typography.Text strong>{task.taskNo}</Typography.Text>
                        <Typography.Paragraph type="secondary" style={{ marginBottom: 0 }}>
                          Graph #{task.graphId} / {task.algorithmType} / {task.algorithmMode}
                        </Typography.Paragraph>
                      </div>
                      <TaskStatusTag status={task.taskStatus} />
                    </Space>
                  </Card>
                ))}
              </Space>
            )}
          </Card>
        </Col>
      </Row>

      <div className="console-page-grid">
        <Card className="console-panel">
          <Space direction="vertical">
            <CompassOutlined />
            <Typography.Text strong>工作空间导航</Typography.Text>
            <Typography.Text type="secondary">
              从业务分组进入流程图列表，是最稳的操作入口。
            </Typography.Text>
            <Button onClick={() => navigate('/workspaces')}>打开 Workspaces</Button>
          </Space>
        </Card>
        <Card className="console-panel">
          <Space direction="vertical">
            <ShareAltOutlined />
            <Typography.Text strong>流程图操作</Typography.Text>
            <Typography.Text type="secondary">
              图编辑器和 YAML 导入导出都从流程图页进入。
            </Typography.Text>
            <Button onClick={() => navigate('/workspaces')}>进入 Graphs</Button>
          </Space>
        </Card>
        <Card className="console-panel">
          <Space direction="vertical">
            <NodeIndexOutlined />
            <Typography.Text strong>任务与结果</Typography.Text>
            <Typography.Text type="secondary">
              任务中心负责提交、轮询和跳转结果分析页。
            </Typography.Text>
            <Button onClick={() => navigate('/tasks')}>打开 Tasks</Button>
          </Space>
        </Card>
      </div>
    </Space>
  )
}

export default DashboardPage
