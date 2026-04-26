import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Table, Tag, Space, Button, Typography, Card, Tooltip, message, Popconfirm } from 'antd';
import {
  SyncOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ClockCircleOutlined,
  EyeOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { getOptimizeTasks, retryTask } from '../api/task';
import type { OptimizeTask } from '../api/task';
import dayjs from 'dayjs';

const { Title } = Typography;

const TaskCenter: React.FC = () => {
  const { workspaceId } = useParams<{ workspaceId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // 查询任务列表，包含自动轮询逻辑
  const { data, isLoading } = useQuery({
    queryKey: ['tasks', workspaceId],
    queryFn: () => getOptimizeTasks({ workspaceId, pageNo: 1, pageSize: 50 }),
    // 如果列表中有运行中的任务，则每 3 秒刷新一次
    refetchInterval: (query) => {
      const tasks = query.state.data?.records || [];
      const hasRunning = tasks.some(
        (t: OptimizeTask) => t.taskStatus === 'PENDING' || t.taskStatus === 'RUNNING',
      );
      return hasRunning ? 3000 : false;
    },
  });

  // 重试 Mutation
  const retryMutation = useMutation({
    mutationFn: (taskId: number) => retryTask(taskId, '用户手动重试'),
    onSuccess: () => {
      message.success('重试任务已提交');
      queryClient.invalidateQueries({ queryKey: ['tasks', workspaceId] });
    },
  });

  const getStatusTag = (status: string) => {
    switch (status) {
      case 'PENDING':
        return (
          <Tag icon={<ClockCircleOutlined />} color="default">
            排队中
          </Tag>
        );
      case 'RUNNING':
        return (
          <Tag icon={<SyncOutlined spin />} color="processing">
            优化中
          </Tag>
        );
      case 'SUCCESS':
        return (
          <Tag icon={<CheckCircleOutlined />} color="success">
            成功
          </Tag>
        );
      case 'FAILED':
        return (
          <Tag icon={<CloseCircleOutlined />} color="error">
            失败
          </Tag>
        );
      default:
        return <Tag>{status}</Tag>;
    }
  };

  const columns = [
    {
      title: '任务编号',
      dataIndex: 'taskNo',
      key: 'taskNo',
      render: (text: string) => <Typography.Text code>{text}</Typography.Text>,
    },
    {
      title: '算法模式',
      key: 'mode',
      render: (_: any, record: OptimizeTask) => (
        <span>
          类型 {record.algorithmType} / 模式 {record.algorithmMode}
        </span>
      ),
    },
    {
      title: '权重 (时/精/费)',
      key: 'weights',
      render: (_: any, record: OptimizeTask) => (
        <span>
          {record.timeWeight} / {record.precisionWeight} / {record.costWeight}
        </span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'taskStatus',
      key: 'taskStatus',
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '提交时间',
      dataIndex: 'queueTime',
      key: 'queueTime',
      render: (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: OptimizeTask) => (
        <Space size="middle">
          {record.taskStatus === 'SUCCESS' && (
            <Tooltip title="查看结果">
              <Button
                type="primary"
                size="small"
                icon={<EyeOutlined />}
                onClick={() => navigate(`/workspace/${workspaceId}/task/${record.taskId}/result`)}
              >
                结果
              </Button>
            </Tooltip>
          )}
          {record.taskStatus === 'FAILED' && (
            <Popconfirm
              title="确定要重试此任务吗？"
              onConfirm={() => retryMutation.mutate(record.taskId)}
            >
              <Button size="small" icon={<ReloadOutlined />} loading={retryMutation.isPending}>
                重试
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 16,
        }}
      >
        <Title level={4} style={{ margin: 0 }}>
          优化任务大厅
        </Title>
        <Button
          icon={<SyncOutlined />}
          onClick={() => queryClient.invalidateQueries({ queryKey: ['tasks', workspaceId] })}
        >
          刷新
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={data?.records || []}
        rowKey="taskId"
        loading={isLoading}
        pagination={{ pageSize: 10 }}
      />
    </Card>
  );
};

export default TaskCenter;
