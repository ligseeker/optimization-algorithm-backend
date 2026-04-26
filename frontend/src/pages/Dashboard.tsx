import React from 'react';
import { Row, Col, Card, Statistic, List, Typography, Space, Tag, Empty } from 'antd';
import {
  DesktopOutlined,
  CheckCircleOutlined,
  SyncOutlined,
  HistoryOutlined,
} from '@ant-design/icons';
import { useQuery } from '@tanstack/react-query';
import { getWorkspaces } from '../api/workspace';
import { getOptimizeTasks } from '../api/task';
import dayjs from 'dayjs';

const { Title, Text } = Typography;

const Dashboard: React.FC = () => {
  // 获取工作空间总数
  const { data: workspaces } = useQuery({
    queryKey: ['workspaces'],
    queryFn: getWorkspaces,
  });

  // 获取最近任务
  const { data: tasks } = useQuery({
    queryKey: ['dashboard-tasks'],
    queryFn: () => getOptimizeTasks({ pageNo: 1, pageSize: 5 }),
  });

  const stats = [
    {
      title: '工作空间',
      value: workspaces?.records?.length || 0,
      icon: <DesktopOutlined style={{ color: '#1677ff' }} />,
      suffix: '个',
    },
    {
      title: '运行中任务',
      value:
        tasks?.records?.filter((t) => t.taskStatus === 'RUNNING' || t.taskStatus === 'PENDING')
          .length || 0,
      icon: <SyncOutlined spin style={{ color: '#faad14' }} />,
      suffix: '项',
    },
    {
      title: '累计优化成功',
      value: tasks?.total || 0, // 简化处理，展示总任务数
      icon: <CheckCircleOutlined style={{ color: '#52c41a' }} />,
      suffix: '次',
    },
  ];

  return (
    <div style={{ padding: '0 0 24px 0' }}>
      <Title level={3} style={{ marginBottom: 24 }}>
        控制台概览
      </Title>

      <Row gutter={16}>
        {stats.map((item, index) => (
          <Col span={8} key={index}>
            <Card variant="outlined">
              <Statistic
                title={item.title}
                value={item.value}
                prefix={item.icon}
                suffix={item.suffix}
              />
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={16} style={{ marginTop: 24 }}>
        <Col span={16}>
          <Card
            title={
              <span>
                <HistoryOutlined /> 最近优化任务
              </span>
            }
            variant="outlined"
            extra={<Typography.Link onClick={() => {}}>查看全部</Typography.Link>}
          >
            <List
              dataSource={tasks?.records || []}
              locale={{ emptyText: <Empty description="暂无近期任务" /> }}
              renderItem={(item) => (
                <List.Item actions={[<Typography.Link key="view">详情</Typography.Link>]}>
                  <List.Item.Meta
                    title={<Text strong>{item.taskNo}</Text>}
                    description={`提交时间: ${dayjs(item.queueTime).format('YYYY-MM-DD HH:mm:ss')}`}
                  />
                  <Tag
                    color={
                      item.taskStatus === 'SUCCESS'
                        ? 'success'
                        : item.taskStatus === 'FAILED'
                          ? 'error'
                          : 'processing'
                    }
                  >
                    {item.taskStatus}
                  </Tag>
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card title="系统通知" variant="outlined">
            <List
              size="small"
              dataSource={[
                { date: '2026-04-27', content: 'Sprint 5 完善与交付阶段开启' },
                { date: '2026-04-26', content: '可视化编辑器核心攻坚完成' },
                { date: '2026-04-25', content: '后端 API 重构版全线对接成功' },
              ]}
              renderItem={(item) => (
                <List.Item>
                  <Space direction="vertical" size={0}>
                    <Text type="secondary" style={{ fontSize: '12px' }}>
                      {item.date}
                    </Text>
                    <Text>{item.content}</Text>
                  </Space>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
