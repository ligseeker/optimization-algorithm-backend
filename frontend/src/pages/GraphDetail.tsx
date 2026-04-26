import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  Card,
  Typography,
  Descriptions,
  Tabs,
  Table,
  Button,
  Space,
  Modal,
  Form,
  InputNumber,
  Select,
  Tag,
  message,
  Breadcrumb,
} from 'antd';
import { PlayCircleOutlined, LeftOutlined } from '@ant-design/icons';
import request from '../api/request';
import { createOptimizeTask } from '../api/task';

const { Title } = Typography;

interface GraphAggregationDetail {
  graph: FlowGraph;
  nodes: ProcessNode[];
  paths: ProcessPath[];
  equipments: unknown[];
  constraints: unknown[];
}

import type { TaskCreateParams } from '../api/task';
import type { FlowGraph } from '../api/graph';
import type { ProcessNode } from '../api/node';
import type { ProcessPath } from '../api/path';

import FlowCanvas from '../components/FlowEditor/FlowCanvas';

const GraphDetail: React.FC = () => {
  const { workspaceId, graphId } = useParams<{ workspaceId: string; graphId: string }>();
  const navigate = useNavigate();
  const [isOptimizeModalOpen, setIsOptimizeModalOpen] = useState(false);
  const [form] = Form.useForm();

  // 获取聚合详情
  const { data: detail, isLoading } = useQuery({
    queryKey: ['graphDetail', graphId],
    queryFn: () => request.get<unknown, GraphAggregationDetail>(`/api/graphs/${graphId}/detail`),
    enabled: !!graphId,
  });

  // 提交任务 Mutation
  const optimizeMutation = useMutation({
    mutationFn: createOptimizeTask,
    onSuccess: () => {
      message.success('优化任务已提交，正在跳转至任务中心...');
      setIsOptimizeModalOpen(false);
      setTimeout(() => {
        navigate(`/workspace/${workspaceId}/task`);
      }, 1000);
    },
  });

  const handleOptimizeSubmit = (values: Omit<TaskCreateParams, 'graphId'>) => {
    optimizeMutation.mutate({
      graphId: Number(graphId),
      ...values,
    });
  };

  const nodeColumns = [
    { title: '编号', dataIndex: 'nodeCode', key: 'nodeCode' },
    { title: '名称', dataIndex: 'nodeName', key: 'nodeName' },
    { title: '耗时', dataIndex: 'timeCost', key: 'timeCost' },
    { title: '精度', dataIndex: 'precisionValue', key: 'precisionValue' },
    { title: '成本', dataIndex: 'costValue', key: 'costValue' },
  ];

  const pathColumns = [
    { title: '起点ID', dataIndex: 'startNodeId', key: 'startNodeId' },
    { title: '终点ID', dataIndex: 'endNodeId', key: 'endNodeId' },
    { title: '类型', dataIndex: 'relationType', key: 'relationType' },
  ];

  if (isLoading) return <Card loading />;

  const { graph, nodes, paths, equipments, constraints } = detail || {};

  return (
    <div style={{ padding: '0 0 24px 0' }}>
      <Breadcrumb style={{ marginBottom: 16 }}>
        <Breadcrumb.Item
          onClick={() => navigate(`/workspace/${workspaceId}/graph`)}
          style={{ cursor: 'pointer' }}
        >
          流程图列表
        </Breadcrumb.Item>
        <Breadcrumb.Item>{graph?.name}</Breadcrumb.Item>
      </Breadcrumb>

      <Card
        title={
          <Space>
            <Button icon={<LeftOutlined />} onClick={() => navigate(-1)} />
            <Title level={4} style={{ margin: 0 }}>
              {graph?.name}
            </Title>
          </Space>
        }
        extra={
          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            onClick={() => setIsOptimizeModalOpen(true)}
          >
            发起优化
          </Button>
        }
      >
        <Descriptions bordered size="small" column={3}>
          <Descriptions.Item label="来源">
            <Tag>{graph?.sourceType}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color="blue">{graph?.graphStatus}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">{graph?.createdAt}</Descriptions.Item>
          <Descriptions.Item label="描述" span={3}>
            {graph?.description || '无'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card style={{ marginTop: 24 }}>
        <Tabs
          defaultActiveKey="nodes"
          items={[
            {
              key: 'nodes',
              label: `节点 (${nodes?.length || 0})`,
              children: (
                <Table
                  dataSource={nodes}
                  columns={nodeColumns}
                  rowKey="id"
                  pagination={false}
                  size="small"
                />
              ),
            },
            {
              key: 'paths',
              label: `路径 (${paths?.length || 0})`,
              children: (
                <Table
                  dataSource={paths}
                  columns={pathColumns}
                  rowKey="id"
                  pagination={false}
                  size="small"
                />
              ),
            },
            {
              key: 'equipments',
              label: `关联装备 (${equipments?.length || 0})`,
              children: (
                <Table
                  dataSource={equipments}
                  columns={[
                    { title: '名称', dataIndex: 'name' },
                    { title: '描述', dataIndex: 'description' },
                  ]}
                  rowKey="id"
                  pagination={false}
                  size="small"
                />
              ),
            },
            {
              key: 'constraints',
              label: `约束条件 (${constraints?.length || 0})`,
              children: (
                <Table
                  dataSource={constraints}
                  columns={[
                    { title: '类型', dataIndex: 'conditionType' },
                    { title: '描述', dataIndex: 'conditionDescription' },
                  ]}
                  rowKey="id"
                  pagination={false}
                  size="small"
                />
              ),
            },
            {
              key: 'visual',
              label: `可视化编辑器 (核心攻坚)`,
              children: (
                <FlowCanvas
                  graphId={Number(graphId)}
                  initialNodes={nodes || []}
                  initialPaths={paths || []}
                />
              ),
            },
          ]}
        />
      </Card>

      <Modal
        title="配置优化算法"
        open={isOptimizeModalOpen}
        onOk={() => form.submit()}
        onCancel={() => setIsOptimizeModalOpen(false)}
        confirmLoading={optimizeMutation.isPending}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            algorithmType: 1,
            algorithmMode: 2,
            timeWeight: 1,
            precisionWeight: 1,
            costWeight: 1,
          }}
          onFinish={handleOptimizeSubmit}
        >
          <Space style={{ width: '100%' }} size="large">
            <Form.Item name="algorithmType" label="算法类型" style={{ flex: 1 }}>
              <Select options={[{ value: 1, label: '标准优化算法' }]} />
            </Form.Item>
            <Form.Item name="algorithmMode" label="运行模式" style={{ flex: 1 }}>
              <Select
                options={[
                  { value: 2, label: '快速启发式' },
                  { value: 1, label: '精确穷举' },
                ]}
              />
            </Form.Item>
          </Space>

          <Title level={5}>指标权重配置 (1-10)</Title>
          <Space wrap size="large">
            <Form.Item name="timeWeight" label="耗时权重">
              <InputNumber min={1} max={10} />
            </Form.Item>
            <Form.Item name="precisionWeight" label="精度权重">
              <InputNumber min={1} max={10} />
            </Form.Item>
            <Form.Item name="costWeight" label="成本权重">
              <InputNumber min={1} max={10} />
            </Form.Item>
          </Space>
        </Form>
      </Modal>
    </div>
  );
};

export default GraphDetail;
