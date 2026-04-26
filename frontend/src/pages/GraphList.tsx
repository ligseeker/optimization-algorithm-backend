import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Table,
  Button,
  Space,
  Typography,
  Tag,
  Modal,
  Form,
  Input,
  message,
  Popconfirm,
  Upload,
  Card,
  Tooltip,
} from 'antd';
import {
  PlusOutlined,
  UploadOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  PlayCircleOutlined,
} from '@ant-design/icons';
import { getGraphs, createGraph, updateGraph, deleteGraph, importYaml } from '../api/graph';
import type { FlowGraph } from '../api/graph';

const { Title } = Typography;

const GraphList: React.FC = () => {
  const { workspaceId } = useParams<{ workspaceId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGraph, setEditingGraph] = useState<FlowGraph | null>(null);
  const [form] = Form.useForm();

  // 查询列表
  const { data, isLoading } = useQuery({
    queryKey: ['graphs', workspaceId],
    queryFn: () => getGraphs(workspaceId!, { pageNo: 1, pageSize: 100 }),
    enabled: !!workspaceId,
  });

  // 保存 Mutation
  const saveMutation = useMutation({
    mutationFn: (values: any) => {
      if (editingGraph) {
        return updateGraph(editingGraph.id, values);
      }
      return createGraph(workspaceId!, values);
    },
    onSuccess: () => {
      message.success(`${editingGraph ? '修改' : '创建'}成功`);
      setIsModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ['graphs', workspaceId] });
    },
  });

  // 删除 Mutation
  const deleteMutation = useMutation({
    mutationFn: deleteGraph,
    onSuccess: () => {
      message.success('删除成功');
      queryClient.invalidateQueries({ queryKey: ['graphs', workspaceId] });
    },
  });

  const showModal = (graph?: FlowGraph) => {
    if (graph) {
      setEditingGraph(graph);
      form.setFieldsValue(graph);
    } else {
      setEditingGraph(null);
      form.resetFields();
    }
    setIsModalOpen(true);
  };

  const handleImport = async (options: any) => {
    const { file, onSuccess, onError } = options;
    try {
      await importYaml(file);
      message.success('导入成功');
      queryClient.invalidateQueries({ queryKey: ['graphs', workspaceId] });
      onSuccess('ok');
    } catch (err) {
      onError(err);
    }
  };

  const columns = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      render: (text: string, record: FlowGraph) => (
        <Space direction="vertical" size={0}>
          <Typography.Link onClick={() => navigate(`/workspace/${workspaceId}/graph/${record.id}`)}>
            {text}
          </Typography.Link>
          <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
            {record.description || '暂无描述'}
          </Typography.Text>
        </Space>
      ),
    },
    {
      title: '来源',
      dataIndex: 'sourceType',
      key: 'sourceType',
      render: (type: string) => (
        <Tag color={type === 'YAML_IMPORT' ? 'purple' : 'blue'}>
          {type === 'YAML_IMPORT' ? 'YAML导入' : '手工创建'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'graphStatus',
      key: 'graphStatus',
      render: (status: string) => (
        <Tag color={status === 'RELEASED' ? 'success' : 'default'}>
          {status === 'RELEASED' ? '已发布' : '草稿'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: FlowGraph) => (
        <Space size="middle">
          <Tooltip title="进入详情/编辑器">
            <Button
              type="text"
              icon={<EyeOutlined />}
              onClick={() => navigate(`/workspace/${workspaceId}/graph/${record.id}`)}
            />
          </Tooltip>
          <Tooltip title="编辑基础信息">
            <Button type="text" icon={<EditOutlined />} onClick={() => showModal(record)} />
          </Tooltip>
          <Tooltip title="提交优化任务">
            <Button
              type="text"
              icon={<PlayCircleOutlined style={{ color: '#52c41a' }} />}
              onClick={() => navigate(`/workspace/${workspaceId}/graph/${record.id}`)}
            />
          </Tooltip>
          <Popconfirm
            title="确定要删除流程图吗？"
            onConfirm={() => deleteMutation.mutate(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="text" danger icon={<DeleteOutlined />} />
          </Popconfirm>
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
          流程图管理
        </Title>
        <Space>
          <Upload customRequest={handleImport} showUploadList={false} accept=".yaml,.yml">
            <Button icon={<UploadOutlined />}>导入YAML</Button>
          </Upload>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
            新建流程图
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={data?.records || []}
        rowKey="id"
        loading={isLoading}
        pagination={{
          total: data?.total || 0,
          pageSize: 10,
        }}
      />

      <Modal
        title={editingGraph ? '编辑流程图信息' : '新建流程图'}
        open={isModalOpen}
        onOk={() => form.submit()}
        onCancel={() => setIsModalOpen(false)}
        confirmLoading={saveMutation.isPending}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={(values) => saveMutation.mutate(values)}>
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请输入名称' }]}>
            <Input placeholder="流程图名称" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="流程图描述" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default GraphList;
